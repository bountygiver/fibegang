from . import node
from . import audio

def create_audio(packet, hub, session, node):
	data = json_data(packet.payload,('name',))
	if data == None:
		return hub.ack("error", "packet format?")
	groupname = data[0]
	
	# assume group name is in a good form
	if node.child_node(groupname) == None:
		node.add_child(groupname, audio.Node())
		return hub.ack(packet, 'success', {})
	else:
		return hub.ack(packet, 'fail', 'name already existed')

# this is not a class
def Node():
	n = node.Node()
	n.add_function('create_audio', create_audio)
	return n