# from kafka import KafkaConsumer

# bootstrap_servers = [
#     'b-1.glidingpathdev.gnkmpn.c2.kafka.ap-south-1.amazonaws.com',
#     'b-2.glidingpathdev.gnkmpn.c2.kafka.ap-south-1.amazonaws.com'
# ]

# consumer = KafkaConsumer(
#     'demo-topic',
#     bootstrap_servers=bootstrap_servers,
#     auto_offset_reset='earliest',
#     group_id='demo-group',
#     value_deserializer=lambda v: v.decode('utf-8')
# )

# print("Listening for messages...")
# for msg in consumer:
#     print(f"Received: {msg.value}")



# import redis

# # Replace with your endpoint and auth token
# VALKEY_HOST = "master.gliding-path-valkey1.pfifcw.aps1.cache.amazonaws.com"
# VALKEY_PORT = 6379
# VALKEY_AUTH_TOKEN = "Str0ngAuthKey*9876_ABC"  # Replace with your actual AUTH token

# # Connect securely
# r = redis.Redis(
#     host=VALKEY_HOST,
#     port=VALKEY_PORT,
#     password=VALKEY_AUTH_TOKEN,
#     ssl=True,  # Must be True since encryption in transit is required
#     decode_responses=True
# )

# # Test connection
# try:
#     r.set("test_key", "hello-valkey-secure")
#     value = r.get("test_key")
#     print("Connection successful! Retrieved:", value)
# except Exception as e:
#     print("Connection failed:", e)


# import redis

# # Replace with your actual details
# VALKEY_HOST = "master.gliding-path-valkey1.pfifcw.aps1.cache.amazonaws.com"
# VALKEY_PORT = 6379
# VALKEY_AUTH_TOKEN = "Str0ngAuthKey*9876_ABC"

# # Connect securely with SSL (since Encryption in Transit = Required)
# r = redis.Redis(
#     host=VALKEY_HOST,
#     port=VALKEY_PORT,
#     password=VALKEY_AUTH_TOKEN,
#     ssl=True,
#     decode_responses=True
# )

# # Example: Get a key
# try:
#     key_name = "test_key"   # replace with your key name
#     value = r.get(key_name)
    
#     if value:
#         print(f"✅ Value for '{key_name}': {value}")
#     else:
#         print(f"⚠️ Key '{key_name}' does not exist.")
# except Exception as e:
#     print("❌ Error while fetching key:", e)
