import hub

try:
	hub.Hub().mainloop()
except KeyboardInterrupt as e:
	print("Interrupt > Keyboard Interrupt")
except Exception as e:
	raise e
	print(type(e).__name__, ">", e)