import threading
import socket
import ssl
import select

message = ''

def comunnicate():

    context = ssl.SSLContext(ssl.PROTOCOL_TLS_CLIENT)
    context.verify_mode = ssl.CERT_REQUIRED
    context.load_verify_locations('C:/users/jakub/desktop/c.pem')

    with socket.create_connection(('127.0.0.1', 443)) as socke:
        with context.wrap_socket(socke, server_hostname='UwU') as c:
            global connection
            connection = c
            end = False
            while not end:
                message = connection.recv().decode('utf-8')
                end = 'end' in message
                if ('file' in message):
                    receive_files(int(message.split(':')[1]))
            print('Successfully finished')


def receive_files(number_of_files):
    i = number_of_files
    while number_of_files > 0:
        message = connection.recv().decode('utf-8')
        x = message.split(':')
        with open('./chess_test/static/temp/' + x[0], 'wb') as file:
            size = int(x[1])
            while 0 < size:

                file_bytes = connection.recv(4096)
                print(file_bytes)
                file.write(file_bytes)
                file_bytes = []
                size = size - 4096
            print('Successfully transferred')
            i = i-1


def send_figures_move(move):
    connection.send(len(move).to_bytes(4, 'big'))
    connection.send(move.encode())


def start_communicatio():
    t = threading.Thread(target=comunnicate)
    t.setDaemon(True)
    t.start()


def get_message():
    return message
