# database/app.py
from flask import Flask, jsonify
from opentelemetry import trace
from opentelemetry.instrumentation.flask import FlaskInstrumentor
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.resources import SERVICE_NAME, Resource
from opentelemetry.exporter.otlp.proto.grpc.trace_exporter import OTLPSpanExporter
from opentelemetry.sdk.trace.export import BatchSpanProcessor

app = Flask(__name__)
resource = Resource(attributes={SERVICE_NAME: "database"})
trace.set_tracer_provider(TracerProvider(resource=resource))
otlp_exporter = OTLPSpanExporter(endpoint="http://otel-collector.monitoring:4317", insecure=True)
trace.get_tracer_provider().add_span_processor(BatchSpanProcessor(otlp_exporter))
FlaskInstrumentor().instrument_app(app)

@app.route("/query")
def query():
    return jsonify({"result": "fake_db_data"})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5002)
