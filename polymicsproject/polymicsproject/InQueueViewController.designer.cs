// WARNING
//
// This file has been generated automatically by Xamarin Studio from the outlets and
// actions declared in your storyboard file.
// Manual changes to this file will not be maintained.
//
using System;
using MonoTouch.Foundation;
using MonoTouch.UIKit;
using System.CodeDom.Compiler;

namespace polymicsproject
{
	[Register ("InQueueViewController")]
	partial class InQueueViewController
	{
		[Outlet]
		[GeneratedCode ("iOS Designer", "1.0")]
		UIButton btnStop { get; set; }

		[Outlet]
		[GeneratedCode ("iOS Designer", "1.0")]
		UIImageView imgQueue { get; set; }

		[Outlet]
		[GeneratedCode ("iOS Designer", "1.0")]
		UILabel labelCounter { get; set; }

		[Outlet]
		[GeneratedCode ("iOS Designer", "1.0")]
		UILabel labelStop { get; set; }

		void ReleaseDesignerOutlets ()
		{
			if (btnStop != null) {
				btnStop.Dispose ();
				btnStop = null;
			}
			if (imgQueue != null) {
				imgQueue.Dispose ();
				imgQueue = null;
			}
			if (labelCounter != null) {
				labelCounter.Dispose ();
				labelCounter = null;
			}
			if (labelStop != null) {
				labelStop.Dispose ();
				labelStop = null;
			}
		}
	}
}
