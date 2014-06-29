import numpy
from numpy import fft
import random
import socket
import math
import sys
import time


from matplotlib import pyplot, animation
POINTS = 400

fig = pyplot.figure()
pyplot.axis([0,100,0,10])
pyplot.ion()
pyplot.show()

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
	regulator = sum(list(sorted(map(abs,datas)))[len(datas)*2//3:]) / (len(datas) - len(datas)*2//3)
	print(regulator)
	result  = []
	rresult = []
	acc = 0
	for i, j in zip(datas[:len(datas)//2], datas[len(datas)//2::-1]):
		acc = acc*param + abs(i)*(1-param)
		if acc < thr / 2:
			result.append(0)
			rresult.append(0)
		else:
			#result.append(i)
			#rresult.append(j)
			#result.append(i*(math.atan((acc-thr/2)/regulator))**1)
			#rresult.append(j*(math.atan((acc-thr/2)/regulator))**1)
			result.append(i * math.e**(-1/((acc/regulator)**2+0.001)))
			rresult.append(j * math.e**(-1/((acc/regulator)**2+0.001)))
		#result.append(i if acc > thr else i*(acc/thr)**4)
	return result+rresult[::-1]

tbegin = time.time()
toffset = 0

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
	
	beforenoice = datas
	
	datas = noice_filter(datas, thr+ampthr*0.5, 0.90)
	
	afternoice = datas
	
	ave2 = sum(abs(i) for i in datas)/len(datas)
	
	datas = fft.ifft(datas)
	datas = list(map(short,datas))
	raw = datas
	print("THR %7.1f %7.1f %7.1f %7.1f %7.1f"%(ampthr, mp, ave, thr, ave2))#(list(zip(org, datas)))
	datas = b''.join([struct.pack("<h",i) for i in datas])
	
	#print(data[:8],data[8:16],data[32:40],data[40:48])
	stream.write(datas)
	
	toffset = toffset + len(raw)/audio_config.SAMPLE_RATE
	if time.time() - tbegin - toffset < 3 * len(raw)/audio_config.SAMPLE_RATE:
		pyplot.clf()
		beforenoice = [abs(i) for i in beforenoice]
		afternoice  = [abs(i) for i in afternoice]
		#beforenoice = [math.log(abs(i)+1) for i in beforenoice]
		#afternoice  = [math.log(abs(i)+1) for i in afternoice]
		beforenoice = [0, 20] + beforenoice
		afternoice  = [0, 20] + afternoice
		fftidx = [0, 0] + [i*audio_config.SAMPLE_RATE/len(raw) for i in range(len(raw))]
		cutoff = len(fftidx)//4
		pyplot.plot(fftidx[:cutoff], beforenoice[:cutoff])
		pyplot.plot(fftidx[:cutoff], afternoice[:cutoff])
		
		pyplot.draw()
		pyplot.pause(0.0001)
	#print(" ".join("%X"%data[i*40] for i in range(20)))