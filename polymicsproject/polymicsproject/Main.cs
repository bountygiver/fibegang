using System;
using System.Collections.Generic;
using System.Linq;

using MonoTouch.Foundation;
using MonoTouch.UIKit;
using StudentDemo;

namespace polymicsproject
{
	public class Application
	{

		public static FibeClient engine;

		// This is the main entry point of the application.
		static void Main (string[] args)
		{
			// if you want to use a different Application Delegate class from "AppDelegate"
			// you can specify it here.
			engine = new FibeClient ();
			engine.ConnectTo ("hysw.org");
			UIApplication.Main (args, null, "AppDelegate");


		}
	}
}
