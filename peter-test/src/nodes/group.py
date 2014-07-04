from . import node
from . import audio

def create_audio(packet, hub, session, node):
	groupname = packet["json"]["name"]
	
	# assume group name is in a good form
	if node.child_node(groupname) == None:
		node.add_child(groupname, audio.Node())
	else:
		return hub.ack(packet, 'error', 'name already existed')

# this is not a class
def Node():
	n = node.Node()
	n.add_function('create_audio', create_audio)
	return n