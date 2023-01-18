package org.connection;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;


public class SecureConnectionManager {
    private KeyStore keyStore;
    private String password;


    public SecureConnectionManager(ServerParameters param) {
        if (param.getMethod().equals("files")) {
            password = "password";
            initializeKeyStore(password, password,
                    "JKS", param.getCertificateChain(), param.getCertificate(), param.getPrivateKey());
            return;
        }
        if (param.getMethod().equals("jks")) {
            password = param.getKeyStorePassword();
            initializeKeyStore(param.getKeyStore(), param.getKeyStorePassword());
            return;
        }
        throw new RuntimeException("Argument 'method:' je v konfiguračním souboru chybně vyplněn");

    }

    private KeyStore loadKeyStore(String keyStorePath, String keyStorePassword, String type) {
        try {
            KeyStore keyStore = KeyStore.getInstance(type);
            keyStore.load(new FileInputStream(keyStorePath), keyStorePassword.toCharArray());
            return keyStore;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Soubor: " + keyStorePath + " se nepodařilo načíst." +
                    " Zkontrolujte konfigurační soubor a zkuste to znovu");
        } catch (CertificateException | IOException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new RuntimeException();
        }
    }

    private KeyStore saveToKeyStore(PrivateKey privateKey, Certificate certificate, Certificate certificateChain, String password, String type, String privateKeyPassword) {
        try {
            KeyStore keyStore = KeyStore.getInstance(type);
            keyStore.load(null, password.toCharArray());
            keyStore.setKeyEntry("privateKey", privateKey, privateKeyPassword.toCharArray(), new Certificate[]{certificateChain});
            keyStore.setCertificateEntry("serverCertificate", certificate);
            return keyStore;
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    private Certificate loadCertificate(String certificatePath) {
        Certificate serverCertificate;
        try (InputStream certificateIn = new FileInputStream(certificatePath)) {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            serverCertificate = certificateFactory.generateCertificate(certificateIn);
        } catch (IOException e) {
            throw new RuntimeException("Soubor: " + certificatePath + " se nepodařilo načíst." +
                    " Zkontrolujte konfigurační soubor a zkuste to znovu");
        } catch (CertificateException e) {
            throw new RuntimeException("Při načítaní certifikátu došlo k chybě");
        }
        return serverCertificate;
    }

    private PrivateKey loadPrivateKey(String privateKeyPath) {
        PrivateKeyInfo privateKeyInfo;
        Object parsedFile = loadPEMObject(privateKeyPath);
        if (parsedFile instanceof PrivateKeyInfo)
            privateKeyInfo = (PrivateKeyInfo) parsedFile;
        else if (parsedFile instanceof PEMKeyPair) {
            PEMKeyPair keyPair = (PEMKeyPair) parsedFile;
            privateKeyInfo = keyPair.getPrivateKeyInfo();
        } else {
            throw new RuntimeException("Odkazovaný soubor neobsahuje validní klíč. Zkontrolujte konfigurační soubor");
        }
        JcaPEMKeyConverter convertor = new JcaPEMKeyConverter();
        try {
            return convertor.getPrivateKey(privateKeyInfo);
        } catch (PEMException e) {
            throw new RuntimeException("Soubor neobsahuje validní klíč ve formátu PEM");
        }
    }

    private Object loadPEMObject(String objectFilePath) {
        try (PEMParser parser = new PEMParser(new FileReader(objectFilePath))) {
            return parser.readObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Odkazovaný soubor s klíčem neexistuje. Zkontrolujte konfigurační soubor");
        } catch (IOException e) {
            throw new RuntimeException("Došlo k chybě při spouštění programu. Zkuste to znovu.");
        }
    }

    private void initializeKeyStore(String keyStorePassword, String privateKeyPassword,
                                    String keyStoreType, String certificateChainPath, String serverCertificatePath, String privateKeyPath) {
        if (privateKeyPath != null && serverCertificatePath != null && certificateChainPath != null) {
            Certificate serverCertificate = loadCertificate(serverCertificatePath);
            Certificate certificateChain = loadCertificate(certificateChainPath);
            PrivateKey privateKey = loadPrivateKey(privateKeyPath);
            keyStore = saveToKeyStore(privateKey, serverCertificate, certificateChain, keyStorePassword, keyStoreType, privateKeyPassword);
            return;
        }
        throw new RuntimeException("V konfihuračním souboru nejsou vyplněny všechny parametry.");


    }

    private void initializeKeyStore(String keyStorePath, String keyStorePassword) {
        if (keyStorePassword != null && keyStorePath != null) {
            keyStore = loadKeyStore(keyStorePath, keyStorePassword, "JKS");
            System.out.println("loaded");
            return;
        }
        throw new RuntimeException("V konfihuračním souboru nejsou vyplněny všechny parametry.");
    }

    public SSLServerSocket getSecureServerSocket(int port) {
        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password.toCharArray());
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, SecureRandom.getInstanceStrong());
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
            sslServerSocket.setEnabledProtocols(new String[]{"TLSv1.3"});
            return sslServerSocket;
        } catch (Exception e) {
            //throw new RuntimeException("Došlo k chybě při spouštění. Zkontrolujte konfiguraci a zkuste to znovu.");
            throw new RuntimeException(e);
        }
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }
}
