from . import node
import json
import sessions.user
from utils.json import fetch_json, store_json
from . import group
from . import audio

def login(packet, hub):
	session, node = packet.session, packet.node
	data = json_data(packet.payload,('username', 'password', 'sessionkey'))
	if data == None:
		return hub.ack("error", "packet format?")
	
	username, password, sessionkey = data

	passwds = fetch_json("data/passwd")
	if username not in passwds:
		return hub.ack(packet, 'fail', 'username not found')
	
	if passwds[username]['password'] != password:
		return hub.ack(packet, 'fail', 'password mismatch')
	
	userid = passwds[username]['userid']
	
	usession = sessions.user.Session(userid = userid, node = node, auth = (lambda x:x['sessionkey']==sessionkey))
	hub.sessions.manage(usession)
	
	return hub.ack(packet, 'success', {"payload":{"sessionid":usession.sessionid}})

def regist(packet, hub):
	data = json_data(packet.payload,('username', 'password'))
	if data == None:
		return hub.ack("error", "packet format?")
	username, password = data

	passwds = fetch_json("data/passwd")

	if username in passwds:
		return hub.ack(packet, 'fail', 'username can not be used')
	
	uid = len(passwds)
	passwds[username] = {"password":password,"userid":uid}
	store_json("data/passwd", passwds)

	return hub.ack(packet, 'success', {})

def create_group(packet, hub):
	data = json_data(packet.payload,('name',))
	if data == None:
		return hub.ack("error", "packet format?")
	groupname = data[0]
	
	# assume group name is in a good form
	if packet.node.child_node(groupname) == None:
		packet.node.add_child(groupname, group.Node())
		return hub.ack(packet, 'success', {})
	else:
		return hub.ack(packet, 'fail', 'group already existed')

# this is not a class
def Node():
	n = node.Node()
	n.add_function('login', login)
	n.add_function('regist', regist)
	n.add_function('create_group', create_group)
	return n