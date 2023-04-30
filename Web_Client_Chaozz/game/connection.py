import threading
import socket as s
import ssl
import json

connection = None

def get_connection():
    global connection
    if connection == None:
        try:
         connection = Connection()
        except:
            return None      
    return connection


class Connection:
    def __init__(self):
        params = self.parse_connection_params()
        self.connection = self.establish_connectio(params['server_ip'],params['server_port'], params['institution'],params['cert_path'])
        self.message = ""
        self.waiting = False

    def establish_connectio(self, host, port, host_name,cert_path):
        context = ssl.SSLContext(ssl.PROTOCOL_TLS_CLIENT)
        context.verify_mode = ssl.CERT_REQUIRED
        context.load_verify_locations(
            cert_path
        )  # path to certificate file set for testing certificate
        socket = s.create_connection((host, port))
        return context.wrap_socket(socket, server_hostname=host_name)

    def wait_until_next_turn(self):
        while True:
            message = self.recieve_data()
            if "notify" in message:
                break
            if "update" in message:
                print("update")
        print("Successfully finished")
      

    def receive_files(self, number_of_files):
        while number_of_files > 0:
            self.recieve_file()
            print("Successfully transferred")
            number_of_files = number_of_files - 1

    def recieve_file(self):
        file_info = self.recieve_data.split(":")
        size = int(file_info[1].strip())
        with open("./game/static/temp/" + file_info[0], "wb") as file:
            while 0 < size:
                file_bytes = self.connection.recv(4096)
                file.write(file_bytes)
                file_bytes = []
                size = size - 4096
            return file_info[0]

    def send_file(self, file):
        print(file.size)
        self.send_data(file.name + ":" + str(file.size))
        for chunk in file.chunks():
            self.connection.sendall(chunk)

    def recieve_data(self):
        return self.connection.recv().decode("utf-8").strip()

    def send_data(self, data):
        self.connection.sendall(len(data).to_bytes(4, "big"))
        self.connection.sendall(data.encode())

    def start_waiting_until_next_turn(self):
        t = threading.Thread(target=self.wait_until_next_turn())
        t.setDaemon(True)
        t.start()

    def get_message(self):
        return self.message
    
    def parse_connection_params(self):
      try:
        with open('./config.json', 'r') as config_file:
          content = config_file.read()
          return json.loads(content)
      except:
        print("Error while parsing config file")
        return None
