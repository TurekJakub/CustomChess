import json

from channels.generic.websocket import WebsocketConsumer
from asgiref.sync import async_to_sync
from .views import game_name, username


class ChatConsumer(WebsocketConsumer):
    def connect(self):
        self.accept()
        print('accept')
        async_to_sync(self.channel_layer.group_add)(
            game_name,
            self.channel_name
        )
        async_to_sync(self.channel_layer.group_add)(
            username,
            self.channel_name
        )

    def disconnect(self, close_code):
         async_to_sync(self.channel_layer.group_discard)(
            game_name,
            self.channel_name
        )
         async_to_sync(self.channel_layer.group_add)(
            username,
            self.channel_name
        )
   

    def send_message(self, event):      
        # Send message to WebSocket
        self.send(text_data=json.dumps({
            "cordinates": event['cordinates'],
            "action": event['action'],
            "figure": event['figure'],
        }))

    def game_message(self, event):      
        self.send_message(event)
       
  
