import socket
import random
import struct
import time

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.bind(('', 51453))

status = {}

lastreport = 0

def report():
	global lastreport
	t = time.time()
	if t > lastreport + 1:
		print(t,"$",status)
		lastreport = t


def log(what, addr, *l):
	global lastreport
	t = time.time()
	if addr == None:
		addr = ('localhost',-1)
	print("%16.6f %s(%s:%d) >"%(t, what,addr[0],addr[1]),*l)
	report()

log('Server', None, 'start at port 51453')

sinks = {}


def create_sink(data, addr):
	global sinks
	channel = random.getrandbits(31)
	log('crsink', addr, 'Channel:%d'%channel)
	while channel in sinks:
		channel = random.getrandbits(31)
	sinks[channel] = {"addr":addr, "expire":time.time()+120, "acl":set()}
	data = struct.pack('!Q', channel)
	data = b'!crsink\0'+b'\0'*24+data
	sock.sendto(data, addr)

def aud_transmit(data, addr):
	global sinks
	channel = struct.unpack_from('!Q', data, 48)[0]
	# TODO check ACL
	if channel in sinks and time.time() > sinks[channel]["expire"]:
		del sinks[channel]
	if channel in sinks:
		log('transmit', addr, 'Channel:%d'%channel)
		sock.sendto(data, sinks[channel]["addr"])
	else:
		if "audio-rejects" not in status:
			status["audio-rejects"]=0
		status["audio-rejects"] += 1
		report()

supported = {
	b'0crsink\0'  : create_sink ,
	b'0audio\0\0' : aud_transmit,
}


while True:
	data, addr = sock.recvfrom(4096)
	if data[:8] in supported:
		supported[data[:8]](data, addr)
	else:
		pass #drop packet
