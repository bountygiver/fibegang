import debug
import selectors
import socket
import utils.packet
import sessions.manager
import sessions.session
from hub import Hub

addr = ('', 56789)

# use selector because I am lazy
selector = selectors.DefaultSelector()

# setup listening socket
sock_udp = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock_udp.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
sock_udp.bind(addr)


system_hub = Hub(sock_udp)

def on_udp(dispacher, sock, mask):
	data, addr = sock.recvfrom(4096)
	
	print(data)
	packet = utils.packet.unpack(data, {'addr':addr})
	print(packet)
	if packet:
		system_hub.dispatch(packet)

# add to queue list
selector.register(sock_udp, selectors.EVENT_READ, on_udp)

# start a event loop
try:
	while True:
		events = selector.select(timeout=10.0)

		for key, mask in events:
			callback = key.data
			callback(None, key.fileobj, mask)
		
		# cleanup the mess
except KeyboardInterrupt as e:
	print("Interrupt > Keyboard Interrupt")
except Exception as e:
	raise e
	print(type(e).__name__, ">", e)
"""
packet    : #request / #audio-transmition / #ack / #notification
path      : the module to handle request
request   : (path, request)
session   : uint32(audio) / uint64
userid    : uint64
payload   : $payload 
	- in json, it should be ['base64', '...'] or ['append'] if need to send raw data
timestamp : $timestamp(when send)
requestid : $id(incremental to prevent resend attack, int the audio case it is timestamp)
"""