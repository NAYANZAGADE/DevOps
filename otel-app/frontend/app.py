# frontend/app.py
from flask import Flask, jsonify
import requests
from opentelemetry import trace
from opentelemetry.instrumentation.flask import FlaskInstrumentor
from opentelemetry.instrumentation.requests import RequestsInstrumentor
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.exporter.otlp.proto.grpc.trace_exporter import OTLPSpanExporter
from opentelemetry.sdk.trace.export import BatchSpanProcessor
from opentelemetry.sdk.resources import SERVICE_NAME, Resource

app = Flask(__name__)

resource = Resource(attributes={SERVICE_NAME: "frontend"})
trace.set_tracer_provider(TracerProvider(resource=resource))
tracer = trace.get_tracer(__name__)
otlp_exporter = OTLPSpanExporter(endpoint="http://otel-collector.monitoring:4317", insecure=True)
trace.get_tracer_provider().add_span_processor(BatchSpanProcessor(otlp_exporter))

FlaskInstrumentor().instrument_app(app)
RequestsInstrumentor().instrument()

@app.route("/")
def root():
    resp = requests.get("http://backend.default.svc.cluster.local:5001/data")
    return jsonify({"frontend": "ok", "backend_response": resp.json()})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
