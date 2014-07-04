regist
	> {
		"sessionid"  : 0,
		"path"       : [],
		"request"    : "regist",
		"identity"   : $(gen_random()),
		"sessionkey" : "",
		"payload"    : {
			"username"   : $username,
			"password"   : $password
		}
	}
	< {
		"identity" : $(packet.identity),
		"status"   : "success" / "fail" / "error",
		"message"  : $(message) if fail
		"payload"  : {} if success
	}

login
	> {
		"sessionid"  : 0,
		"path"       : [],
		"request"    : "login",
		"identity"   : $(gen_random()),
		"sessionkey" : "",
		"payload"    : {
			"username"   : $username,
			"password"   : $password,
			"sessionkey" : $(gen_sessionkey())
		}
	}
	< {
		"identity" : $(packet.identity),
		"status"   : "success" / "fail" / "error",
		"message"  : $(message) if fail
		"payload"  : {"sessionid":$(sessionid)} if success
	}

create group
	> {
		"sessionid"  : $(login_session),
		"path"       : [],
		"request"    : "create_group",
		"identity"   : $(gen_random()),
		"sessionkey" : $(sessionkey),
		"payload"    : {
			"name"  : $name
		}
	}
	< {
		"identity" : $(packet.identity),
		"status"   : "success" / "fail" / "error",
		"message"  : $(message) if fail
		"payload"  : {} if success
	}

create audio queue
	> {
		"sessionid"  : $(login_session),
		"path"       : [],
		"request"    : "create_audio_queue",
		"identity"   : $(gen_random()),
		"sessionkey" : $(sessionkey),
		"payload"    : {
			"name"  : $name
		}
	}
	< {
		"identity" : $(packet.identity),
		"status"   : "success" / "fail" / "error",
		"message"  : $(message) if fail
		"payload"  : {} if success
	}
