import sessions.manager
import sessions.generic
import nodes.root
import json
import utils.packet

class Hub:

	def __init__(self, sock):
		root_node = nodes.root.Node()
		main_session = sessions.generic.Session(node=root_node)
		
		self.session_manager = sessions.manager.SessionManager()
		self.session_manager.manage(main_session, forceid = 0)
		
		self.sock = sock

	def dispatch(self, packet):
		session = self.session_manager.get(packet['sessionid'])
		if session:
			session.process(packet, self)
	
	def ack(self, packet, code, data):
		if not isinstance(data,dict):
			data = {"data":data}
		data['status'] = code
		self.sock.sendto(json.dumps(data).encode("utf-8"), packet['addr'])
	def send(self, addr, data):
		if addr:
			self.sock.sendto(utils.packet.pack(data), addr)