import json

def fetch_json(filename):
	try:
		return json.loads(open(filename).read())
	except Exception:
		return {}

def store_json(filename, obj):
	open(filename,"w").write(json.dumps(obj))

def json_data(payload, required_attrs):
	l = []
	for i in required_attrs:
		if i in payload:
			l.append(payload[i])
		else:
			return None
	return l