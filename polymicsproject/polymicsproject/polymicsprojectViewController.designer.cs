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
	[Register ("polymicsprojectViewController")]
	partial class polymicsprojectViewController
	{
		[Outlet]
		[GeneratedCode ("iOS Designer", "1.0")]
		UIButton btnConnect { get; set; }

		[Outlet]
		[GeneratedCode ("iOS Designer", "1.0")]
		UITextField txtConnectTo { get; set; }

		void ReleaseDesignerOutlets ()
		{
			if (btnConnect != null) {
				btnConnect.Dispose ();
				btnConnect = null;
			}
			if (txtConnectTo != null) {
				txtConnectTo.Dispose ();
				txtConnectTo = null;
			}
		}
	}
}
