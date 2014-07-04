class DebugException(Exception):
	def __init__(self, *l, **d):
		super(DebugException, self).__init__(*l, **d)