import struct
from . import node
import sessions.session

class Node(node.Node):

	def __init__(self):
		super(Node, self).__init__()
		self.queue    = {}
		self.sessions = {}
		self.add_function('ping',   self.ping)
		self.add_function('enqueue',self.ping)
		self.add_function('cancel', self.cancel)
		self.add_function('permit', self.permit)
		self.add_function('audio-transmit-a000', self.transmit)
		self.dest = None
		self.dest_identity = None
		self.pure_pcm = False

	def ping(self, packet, hub):
		# TODO need to check permission
		self.queue[session.userid] = {
			'info'    : packet.payload,
			'addr'    : packet.addr,
			'identity': packet.identity,
			'session' : None
		}
		hub.ack(packet, 'success', {})
		hub.notify4a(self.dest, 'enqueue', self.dest_identity, {
			"userid"   : packet.session.userid,
			"usedname" : "undefined",
			"tags" : packet.payload["tags"],
			"time" : packet.payload["time"]
			})

	def cancel(self, packet, hub):
		if 'target_user' in packet.payload:
			user = self.queue[packet.payload['target-user']]
			del self.queue[packet.payload['target-user']]
			hub.notify4a(user['addr'], 'cancel', user['identity'], {})
		else:
			del self.queue[session.userid]
			hub.notify4a(self.dest, 'cancel', self.dest_identity, {
				"userid" : session.userid
				})
		hub.ack(packet,'success', '')

	def permit(self, packet, hub):
		if 'pcm' in packet.payload and packet.payload['pcm'] == 'pure':
			self.pure_pcm = True
		
		self.dest          = packet.addr
		self.dest_identity = packet.identity
		
		audsession = sessions.session.Session(node=self)
		hub.session_manager.manage(audsession)
		
		self.sessions[audsession.sessionid] = packet.payload['target-user']
		
		user = self.queue[packet.payload['target-user']]
		user['session'] = audsession.sessionid
		
		sessionid = audsession.sessionid
		
		hub.ack(packet, 'success', {})
		hub.notify4a(user['addr'], 'permit', user['identity'],{"session":ssionid})

	def transmit(self, packet, hub):
		if self.pure_pcm:
			payload = packet.payload
		else:
			payload = b'a000' + b' '*8 + packet.payload
		
		if packet.sessionid in self.sessions:
			hub.send(self.dest, payload)
