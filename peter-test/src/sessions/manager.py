from utils import idpool

class SessionManager:
	def __init__(self):
		self.idpool   = idpool.Idpool()
		self.sessions = {}

	def manage(self, session, forceid=None):
		
		if forceid != None:
			self.idpool.set_used(forceid)
			sid = forceid
		else:
			sid = self.idpool.alloc()
		
		session.set_sessionid(sid)
		
		self.sessions[sid] = session
		
		return session

	def get(self, sessionid):
		if sessionid in self.sessions:
			return self.sessions[sessionid]
		else:
			return None

	def rm(self, sessionid):
		if isinstance(sessionid, Session):
			sessionid = session.sessionid
		del self.sessions[sessionid]
		self.idpool.free(sessionid)

