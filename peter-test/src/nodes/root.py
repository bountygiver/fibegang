from . import node
import json
import sessions.user
from . import group
from . import audio

def fetch_json(filename):
	try:
		return json.loads(open(filename).read())
	except Exception:
		return {}

def store_json(filename, obj):
	open(filename,"w").write(json.dumps(obj))

def json_data(packet, required_attrs):
	if 'json' not in packet:
		return None# this is not standard format, so ignore this packet
	data = packet['json']
	l = []
	for i in required_attrs:
		if i in data:
			l.append(data[i])
		else:
			return None
	return l

def login(packet, hub, session, node):
	print("on login")
	data = json_data(packet,('username', 'password', 'sessionkey'))
	if data == None: return # this is not standard format, so ignore this packet
	username, password, sessionkey = data

	passwds = fetch_json("data/passwd")
	if username not in passwds:
		return hub.ack(packet, 'error', 'username not found')
	
	if passwds[username]['password'] != password:
		return hub.ack(packet, 'error', 'password mismatch')
	
	userid = passwds[username]['userid']
	
	usession = sessions.user.Session(userid = userid, node = node, auth = (lambda x:x['sessionkey']==sessionkey))
	hub.session_manager.manage(usession)
	
	return hub.ack(packet, 'success', {"sessionid":usession.sessionid})

def regist(packet, hub, session, node):
	data = json_data(packet,('username', 'password'))
	if data == None: return # this is not standard format, so ignore this packet
	username, password = data

	passwds = fetch_json("data/passwd")

	if username in passwds:
		return hub.ack(packet, 'error', 'username can not be used')
	
	uid = len(passwds)
	passwds[username] = {"password":password,"userid":uid}
	store_json("data/passwd", passwds)

	return hub.ack(packet, 'success', '')

def create_group(packet, hub, session, node):
	print("> create group")
	data = json_data(packet,('groupname',))
	print(data)
	if data == None: return # this is not standard format, so ignore this packet
	groupname = data[0]
	
	# assume group name is in a good form
	if node.child_node(groupname) == None:
		node.add_child(groupname, group.Node())
		return hub.ack(packet, 'success', 'group created')
	else:
		return hub.ack(packet, 'error', 'group already existed')

# this is not a class
def Node():
	n = node.Node()
	n.add_function('login', login)
	n.add_function('regist', regist)
	n.add_function('create_group', create_group)
	return n