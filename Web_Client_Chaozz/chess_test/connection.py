import threading
import socket as s
import ssl
import select
from . import routing
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync


class Connection:

    def __init__(self):
        self.connection = self.establish_connectio('127.0.0.1', 443, 'UwU')
        self.message = ""
        self.waiting = False

    def establish_connectio(self, host, port, host_name):
        context = ssl.SSLContext(ssl.PROTOCOL_TLS_CLIENT)
        context.verify_mode = ssl.CERT_REQUIRED
        context.load_verify_locations('C:/users/jakub/desktop/c.pem')
        socket = s.create_connection((host, port))
        return context.wrap_socket(socket, server_hostname=host_name)

    def wait_until_next_turn(self):

        while True:
            message = self.connection.recv().decode('utf-8')
            if ('notify' in message):
                break
            if ('update' in message):
                print('update')
        print('Successfully finished')
        layer = get_channel_layer()
       

    def receive_files(self, number_of_files):
        while number_of_files > 0:
            message = self.connection.recv().decode('utf-8')
            file_info = message.split(':')
            self.recieve_file(file_info[0], int(file_info[1]))
            print('Successfully transferred')
            number_of_files = number_of_files - 1

    def recieve_file(self, name, size):
        with open('./chess_test/static/temp/' + name, 'wb') as file:
            while 0 < size:
                file_bytes = self.connection.recv(4096)
                file.write(file_bytes)
                file_bytes = []
                size = size - 4096

    def recieve_data(self):
        return self.connection.recv().decode('utf-8')

    def send_data(self, data):
        self.connection.send(len(data).to_bytes(4, 'big'))
        self.connection.send(data.encode())

    def start_waiting_until_next_turn(self):
        t = threading.Thread(target=self.wait_until_next_turn())
        t.setDaemon(True)
        t.start()

    def get_message(self):
        return self.message
