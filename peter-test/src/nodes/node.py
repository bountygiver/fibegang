class Node:
	def __init__(self):
		self.children  = {}
		self.functions = {}

	def add_child(self, name, obj):
		self.children[name] = obj

	def child_node(self, name):
		if name in self.children:
			return self.children[name]
		else:
			return None

	def add_function(self, name, func):
		self.functions[name] = func
	
	def authenticate(self, session, request):
		return True
	
	def process(self, packet, hub, session):
		request = packet['request']
		if not self.authenticate(session, request):
			return False
		if request in self.functions:
			callback = self.functions[request]
			callback(packet, hub, session, self)
		elif request == 'ls':
			hub.ack(packet, 'default',{"list":dict((i,type(self.children[i]).__name__) for i in self.children)})
