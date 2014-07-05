class BasicNode:
	def __init__(self, description="N/A"):
		self.children    = {}
		self.functions   = {}
		self.description = description

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
	
	def process(self, packet, hub):
		packet.node = self
		request = packet.request
		if not self.authenticate(packet.session, request):
			return False
		if request in self.functions:
			callback = self.functions[request]
			callback(packet, hub)
		elif request == 'ls':
			hub.ack(packet, 'success',{"list":dict((i,self.children[i].description) for i in self.children)})

