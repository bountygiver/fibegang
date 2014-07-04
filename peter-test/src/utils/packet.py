import json
import struct

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
			packet[i] = known[i]
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
		result = {}
		# path: there might have path prefix
		# identity: sent back with the packet
		#           it will be the timestamp for audio
		for i in ('sessionid', 'path', 'request', 'identity'):
			result[i] = json_data[i]
		result['json'] = json_data
		return result
	except (KeyError, TypeError, OverflowError, ValueError):
		return None

def unpack_audio(data):
	if len(data) < 12:
		return None
	
	return {
		'path'      : [],
		'request'   : 'audio-transmit-a000',
		'sessionid' : struct.unpack_from("!I", data, 4)[0],
		'tmstamp'   : struct.unpack_from("!I", data, 8)[0],
		'payload'   : data[12:],
		'identity'  : struct.unpack_from("!I", data, 8)[0],
	}
