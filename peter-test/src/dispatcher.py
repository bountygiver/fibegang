import selectors
import socket
import json
import random
from . import packetassembler

class ConnectionPool:
	def __init__(self):
		# the session connection information
		self.sessions = {}
	
	def reply(self, packet, what):
		data = pack(what)
		if data == None:
			raise debug.DebugException("reply format wrong")
	
	def send(self, addr, data):
		self.sock_udp.sendto(data, addr)

class Dispacher:
	def __init__(self, addr, config):
		self.selector = selectors.DefaultSelector()
		
		self.sock_udp = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		self.sock_udp.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		self.sock_udp.bind(addr)
		
		self.selector.register(self.sock_udp, selectors.EVENT_READ, self.on_udp)

Dispacher_default_config = {
	"udp_buffer_size" : 4096
}

class Dispacher:
	def __init__(self, addr, config):
		self.sock_udp = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		self.sock_udp.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		self.sock_udp.bind(addr)

		self.sock_tcp = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.sock_tcp.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		self.sock_tcp.bind(addr)
		self.sock_tcp.listen(5)

		#TODO blocking?

		self.selector = selectors.DefaultSelector()

		self.selector.register(self.sock_udp, selectors.EVENT_READ, self.on_udp)
		self.selector.register(self.sock_tcp, selectors.EVENT_READ, self.on_tcp)
		
		self.config = {}
		for i in Dispacher_default_config:
			self.config[i] = Dispacher_default_config[i]
		for i in config:
			self.config[i] = config[i]
		
		self.packet_handler = None
		
		self.sessions = {}

	# note that the dispacher arguement is totally useless
	def on_tcp(self, dispacher, sock, mask):
		con, addr = sock.accept()  # Should be ready
		print('accepted', conn, 'from', addr)
		#TODO blocking?
		con.close()
		#NOTE this directly drop the connection
		#     because no function is implemented

	def set_packet_handler(self, callback):
		self.packet_handler = callback
	
	def on_udp(self, dispacher, sock, mask):
		data, addr = sock.recvfrom(self.config["udp_buffer_size"])
		
		packet = packetassembler.assemble(data, {'addr':addr})
		
		# need to get correct/valid uid via security apis
		# there are no reason to let client give a uid that is not valid
		if 'session' in packet and packet['session'] in self.sessions:
			self.sessions[packet['session']](self, packet)
		elif self.packet_handler:
			self.packet_handler(self, packet)
		else:
			print("droped udp packet: %(request)s @ %(addr)s"%packet)

	def alarm(self):
		pass # TODO cleanup objects that are timedout

	def reply(self, packet, data):
		self.send(packet['addr'], data)
	
	def send(self, info, data):
		if isinstance(data, str):
			data = data.encode("utf-8")
		elif isinstance(data, dict):
			# tempory solution
			del data['payload']
			data = json.dumps(data).encode("utf-8")
		if info:
			self.sock_udp.sendto(data, info)

	def new_session(self, obj):
		
		sid = random.getrandbits(30)
		
		while sid in self.sessions:
			sid = random.getrandbits(30)
		
		self.sessions[sid] = obj
		
		return sid

	def mainloop(self):
		try:
			while True:
				events = self.selector.select(timeout=10.0)

				for key, mask in events:
					callback = key.data
					callback(self, key.fileobj, mask)

				self.alarm()
		except KeyboardInterrupt as e:
			print("Interrupt > Keyboard Interrupt")
		except Exception as e:
			raise e
			print(type(e).__name__, ">", e)