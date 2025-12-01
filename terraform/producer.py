from kafka import KafkaProducer

# Replace with your MSK bootstrap servers
bootstrap_servers = [
    'b-1.glidingpathdev.gnkmpn.c2.kafka.ap-south-1.amazonaws.com',
    'b-2.glidingpathdev.gnkmpn.c2.kafka.ap-south-1.amazonaws.com'
]

producer = KafkaProducer(
    bootstrap_servers=bootstrap_servers,
    value_serializer=lambda v: v.encode('utf-8')
)

topic_name = 'demo-topic'

# Send 5 messages
for i in range(5):
    message = f"Hello MSK {i}"
    producer.send(topic_name, message)
    print(f"Sent: {message}")

producer.flush()
print("All messages sent!")
