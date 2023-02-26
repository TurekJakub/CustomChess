import json

from channels.generic.websocket import WebsocketConsumer
from asgiref.sync import async_to_sync


class ChatConsumer(WebsocketConsumer):
    def connect(self):
        self.accept()
        print('accept')
        async_to_sync(self.channel_layer.group_add)(
            'events',
            self.channel_name
        )
        

    def disconnect(self, close_code):
         async_to_sync(self.channel_layer.group_discard)(
            'events',
            self.channel_name
        )
        
    def receive(self, text):

        self.send('kdjjs')

    def send_message(self, res):
        """ Receive message from room group """
        # Send message to WebSocket
        self.send(text_data=json.dumps({
            "payload": res,
        }))
    def events_alarm(self, event):
        print("UwU//")
        self.send_message(event['content'])
       
  
