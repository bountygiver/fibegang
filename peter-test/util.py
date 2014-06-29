import struct

def itob(n, k):
	return bytes([(n>>(i*8))&0XFF for i in range(k)])

def mkheader_raw(ptype, uid, sign):
	uid  = itob(uid, 8)
	sign = bytes(sign)
	data = b'0'
	data = data + bytes([ord(i) for i in ptype] + [0]*7 )[:7 ]
	data = data + bytes([    i  for i in uid  ] + [0]*8 )[:8 ]
	data = data + bytes([    i  for i in sign ] + [0]*16)[:16]
	return data
def mkheader_aud(atype, offset, channel, **d):
	off = itob(offset, 8)
	data = b''
	data = data + bytes([    i  for i in atype] + [0]*8 )[:8 ]
	data = data + bytes([    i  for i in off  ] + [0]*8 )[:8 ]
	data = data + struct.pack("!Q", channel)
	data = data + bytes([0]*8)
	return data