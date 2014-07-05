import json
import struct

class Packet:
	def __init__(self, request, path, sessionid, identity, payload, **d):
		self.request   = request
		self.path      = path
		self.sessionid = sessionid
		self.identity  = identity
		self.payload   = payload
		for i in d:
			self.__dict__[i] = d[i]
		self.session   = None

def pack(what):
	if isinstance(what, bytes):
		data = what
	elif isinstance(what, str):
		data = what.encode("utf-8")
	elif isinstance(what, dict) or isinstance(what, list):
		data = json.dumps(what).encode("utf-8")
	else:
		return None

def unpack(data, known):
	packet = unpack_impl(data, known)
	if packet != None:
		for i in known:
			packet.__dict__[i] = known[i]
	return packet

def unpack_impl(data, known):
	if data[:1] == b'{':
		return unpack_json(data)
	elif data[:4] == b'a000':
		return unpack_audio(data)
	else:
		return None

def unpack_json(data):
	try:
		json_data = json.loads(data.decode('utf-8'))
		return Packet(
			request   = json_data['request'],
			path      = json_data['path'],
			sessionid = json_data['sessionid'],
			identity  = json_data['identity'],
			payload   = json_data['payload'],
			json      = json_data,
		)
	except (KeyError, TypeError, OverflowError, ValueError):
		return None

def unpack_audio(data):
	if len(data) < 12:
		return None
	
	return Packet(
			request   = 'audio-transmit-a000',
			path      = [],
			sessionid = struct.unpack_from("!I", data, 4)[0],
			identity  = struct.unpack_from("!I", data, 8)[0],
			payload   = data[12:],
			timestamp = struct.unpack_from("!I", data, 8)[0]
	)
