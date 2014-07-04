import random

# assume that this stuff works, not going to make it for
# the first demo
class Idpool:
	def __init__(self):
		self.used = set()
	def set_used(self, x):
		self.used.add(x)
	def alloc(self):
		x = random.getrandbits(30)
		while x in self.used:
			x = random.getrandbits(30)
		self.used.add(x)
		return x
	def free(self, x):
		self.used.remove(x)