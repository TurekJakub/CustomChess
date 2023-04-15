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
   

    def send_message(self, event):      
        # Send message to WebSocket
        self.send(text_data=json.dumps({
            "cordinates": event['cordinates'],
            "action": event['action'],
            "figure": event['figure'],
        }))

    def events_alarm(self, event):      
        self.send_message(event)
       
  
