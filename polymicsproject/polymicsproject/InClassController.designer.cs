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
	[Register ("InClassController")]
	partial class InClassController
	{
		[Outlet]
		[GeneratedCode ("iOS Designer", "1.0")]
		UIButton btnRequest { get; set; }

		[Outlet]
		[GeneratedCode ("iOS Designer", "1.0")]
		UILabel labelRequest { get; set; }

		void ReleaseDesignerOutlets ()
		{
			if (btnRequest != null) {
				btnRequest.Dispose ();
				btnRequest = null;
			}
			if (labelRequest != null) {
				labelRequest.Dispose ();
				labelRequest = null;
			}
		}
	}
}
