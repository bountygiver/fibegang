#==========================================================================
#
#
#==========================================================================

import selectors
import json
import socket # -_-'
import packets
import nodes.root
import sessions.generic
import sessionmanager

class Hub:
	def __init__(self, addr=('',56789)):
		# setup connection
		self.selector = selectors.DefaultSelector()
		self.setup_udp(addr)
		
		# sessions
		self.sessions = sessionmanager.SessionManager()
		
		# create root node
		root_node = nodes.root.Node()
		main_session = sessions.generic.Session(node=root_node)
		
		self.sessions.manage(main_session, forceid = 0)

	def setup_udp(self, addr):
		# setup listening socket
		sock_udp = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		sock_udp.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		sock_udp.bind(addr)

		# add to queue list
		self.selector.register(sock_udp, selectors.EVENT_READ, self.on_udp)
		
		self.sock = sock_udp

	def on_udp(self, sock, mask):
		data, addr = sock.recvfrom(4096)
		
		packet = packets.unpack(data, {'addr':addr})
		
		
		if packet:
			self.dispatch(packet)
		else:
			self.log("> unknown packet format")

	def log(self, *l, **d):
		print(*l, **d)

	def dispatch(self, packet):
		packet.session = self.sessions.get(packet.sessionid)
		if packet.session:
			packet.session.process(packet, self)
		else:
			self.log("> session not found")

	def ack(self, packet, status, data):
		result = {"status":status, "identity":packet.identity}
		if status == 'success':
			result['payload'] = data
		else:
			result['message'] = data
		self.sock.sendto(result.dumps(data).encode("utf-8"), packet.addr)

	def notify4a(self, addr, ptype, identity, payload):
		self.sock.sendto(result.dumps({
			"status"   : "notify",
			"type"     : ptype,
			"identity" : identity,
			"payload"  : payload
			}).encode("utf-8"), addr)

	def send(self, addr, data):
		if addr:
			self.sock.sendto(utils.packet.pack(data), addr)

	def mainloop(self):
		while True:
			events = self.selector.select(timeout=10.0)

			for key, mask in events:
				callback = key.data
				callback(key.fileobj, mask)

			# cleanup the mess