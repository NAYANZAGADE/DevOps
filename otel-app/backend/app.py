# backend/app.py
from fastapi import FastAPI
import requests
from opentelemetry import trace
from opentelemetry.instrumentation.fastapi import FastAPIInstrumentor
from opentelemetry.instrumentation.requests import RequestsInstrumentor
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.resources import SERVICE_NAME, Resource
from opentelemetry.exporter.otlp.proto.grpc.trace_exporter import OTLPSpanExporter
from opentelemetry.sdk.trace.export import BatchSpanProcessor

app = FastAPI()

resource = Resource(attributes={SERVICE_NAME: "backend"})
trace.set_tracer_provider(TracerProvider(resource=resource))
tracer = trace.get_tracer(__name__)
otlp_exporter = OTLPSpanExporter(endpoint="http://otel-collector.monitoring:4317", insecure=True)
trace.get_tracer_provider().add_span_processor(BatchSpanProcessor(otlp_exporter))

FastAPIInstrumentor().instrument_app(app)
RequestsInstrumentor().instrument()

@app.get("/data")
def get_data():
    db_response = requests.get("http://database.default.svc.cluster.local:5002/query")
    return {"backend": "ok", "database": db_response.json()}
