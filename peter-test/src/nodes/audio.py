import struct
from . import node
import sessions.session

class Node(node.Node):

	def __init__(self):
		super(Node, self).__init__()
		self.queue    = {}
		self.sessions = {}
		self.add_function('ping',   self.ping)
		self.add_function('cancel', self.cancel)
		self.add_function('permit', self.permit)
		self.add_function('audio-transmit-a0000', self.transmit)
		self.dest = None
		self.pure_pcm = False

	def ping(self, packet, hub, session, node):
		# TODO need to check permission
		self.queue[session.userid] = {
			'info'    : packet['payload'],
			'addr'    : packet['addr'],
			'session' : None
		}
		hub.ack(packet, 'success', '')
		hub.send(self.dest, {"status":"ping", "userid":session.userid, "text":packet['json']["text"]})

	def cancel(self, packet, hub, session, node):
		if 'target_user' in packet['json']:
			del self.queue[packet['json']['target-user']]
			hub.send(self.queue[packet['json']['target-user']]['addr'], {"status":"rejection"})
		else:
			del self.queue[session.userid]
			hub.send(self.dest, {"status":"cancel"})
		hub.ack(packet,'success', '')

	def permit(self, packet, hub, session, node):
		if 'pcm' in packet['json'] and packet['json']['pcm'] == 'pure':
			self.pure_pcm = True
		
		self.dest = packet['addr']
		
		audsession = sessions.session.Session(node=self)
		hub.session_manager.manage(audsession)
		self.sessions[audsession.sessionid] = packet['json']['target-user']
		user = self.queue[packet['json']['target-user']]
		user['session'] = audsession.sessionid
		
		sessionid = audsession.sessionid
		
		hub.ack(packet, 'success', {"session":ssionid})
		hub.send(user['addr'], '{"status":"permit","session":%d}'%sessionid)

	def transmit(self, packet, hub, session, node):
		if self.pure_pcm:
			payload = packet['payload']
		else:
			payload = b'a000' + b' '*8 + packet['payload']
		
		if packet['sessionid'] in self.sessions:
			hub.send(self.dest, payload)
