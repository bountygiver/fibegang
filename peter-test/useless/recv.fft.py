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

def short(k):
	k = int(k.real)
	if k < -32760:
		return -32760
	if k > 32760:
		return 32760
	return k

def fx(x):
	return x**3/(x**2+10)

thr = 0

def noice_filter(datas, thr, param = 0.9):
	result = []
	acc = 0
	for i in datas:
		acc = acc*param + abs(i)*(1-param)
		result.append(i*(math.atan(acc/thr)/math.pi*2)**2)
		
		#result.append(i if acc > thr else i*(acc/thr)**4)
	return result

while True:
	data, addr = sock.recvfrom(1024+audio_config.READ_SIZE*2)
	datas = data[64:]
	datas = [struct.unpack("<h",datas[i*2:i*2+2])[0] for i in range(len(datas)//2)]
	org = datas
	datas = fft.fft(datas)

	
	mp = max(abs(i) for i in datas)
	power = sum(abs(i)**2 for i in datas)
	ampthr = (power/len(datas))**0.5
	ave = sum(abs(i) for i in datas)/len(datas)
	mmt = (sum(abs(i)**2 for i in datas)/len(datas))**0.5
	thr = thr*0.99 + (ave+2*ampthr)*0.01
	
	datas = noice_filter(datas, thr+ampthr*0.5, 0.9)
	
	ave2 = sum(abs(i) for i in datas)/len(datas)
	datas = fft.ifft(datas)
	datas = list(map(short,datas))
	print("THR %7.1f %7.1f %7.1f %7.1f %7.1f"%(ampthr, mp, ave, thr, ave2))#(list(zip(org, datas)))
	datas = b''.join([struct.pack("<h",i) for i in datas])
	
	#print(data[:8],data[8:16],data[32:40],data[40:48])
	stream.write(datas)