import ossaudiodev
import socket
from numpy import fft
import struct
import audio_config
import util
import math

stream = ossaudiodev.open("w")

RECV_ADDR   = ('', 50005)

stream.setparameters(audio_config.SAMPLE_FORMAT,
                     audio_config.N_CHANNELS,
                     audio_config.SAMPLE_RATE)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.bind(('', 50005))

sock.sendto(b'0crsink\0' ,('23.239.9.164', 51453))

data, addr = sock.recvfrom(1024+audio_config.READ_SIZE*2)
print("Channel:", struct.unpack_from("!Q", data, 32)[0])

while True:
	data, addr = sock.recvfrom(1024+audio_config.READ_SIZE*2)
	datas = data[64:]
	print(data[:8],data[8:16],data[32:40],data[40:48])
	stream.write(datas)