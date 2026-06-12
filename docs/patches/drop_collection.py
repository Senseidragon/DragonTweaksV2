from pymilvus import MilvusClient

c = MilvusClient(uri="http://localhost:19530")
c.drop_collection("ms_dragontweaksv2_4403422f")
print("dropped")
