using System;
using System.Drawing;

using MonoTouch.Foundation;
using MonoTouch.UIKit;

namespace polymicsproject
{
	public partial class polymicsprojectViewController : UIViewController
	{
		public static StudentDemo.FibeClient engine;

		public polymicsprojectViewController (IntPtr handle) : base (handle)
		{
		}

		public override void DidReceiveMemoryWarning ()
		{
			// Releases the view if it doesn't have a superview.
			base.DidReceiveMemoryWarning ();
			
			// Release any cached data, images, etc that aren't in use.
		}

		#region View lifecycle

		public override void ViewDidLoad ()
		{
			base.ViewDidLoad ();
			engine = new StudentDemo.FibeClient ();

			// Perform any additional setup after loading the view, typically from a nib.
			btnConnect.TouchUpInside += (object sender, EventArgs e) =>  {

			};
		}

		public override void ViewWillAppear (bool animated)
		{
			base.ViewWillAppear (animated);
		}

		public override void ViewDidAppear (bool animated)
		{
			base.ViewDidAppear (animated);
		}

		public override void ViewWillDisappear (bool animated)
		{
			base.ViewWillDisappear (animated);
		}

		public override void ViewDidDisappear (bool animated)
		{
			base.ViewDidDisappear (animated);
		}

		#endregion

		public override void PrepareForSegue (UIStoryboardSegue segue, NSObject sender)
		{
			base.PrepareForSegue (segue, sender);
		}
	}
}

