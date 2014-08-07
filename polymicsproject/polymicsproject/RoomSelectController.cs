using System;
using MonoTouch.Foundation;
using MonoTouch.UIKit;
using System.CodeDom.Compiler;
using System.Threading.Tasks;
using System.Collections.Generic;
using StudentDemo;

namespace polymicsproject
{
	partial class RoomSelectController : UITableViewController
	{
		static NSString classCellId = new NSString ("ClassList");

		public List<ClassItem> classList;

		public RoomSelectController (IntPtr handle) : base (handle)
		{
			TableView.Source = new RoomListController (this);
		}

		public override void ViewDidLoad ()
		{
			base.ViewDidLoad ();
		}

		class RoomListController : UITableViewSource 
		{
			RoomSelectController controller;

			public RoomListController (RoomSelectController controller)
			{
				this.controller = controller;
			}

			// Returns the number of rows in each section of the table
			public override int RowsInSection (UITableView tableView, int section)
			{
				return controller.classList.Count;
			}
			//
			// Returns a table cell for the row indicated by row property of the NSIndexPath
			// This method is called multiple times to populate each row of the table.
			// The method automatically uses cells that have scrolled off the screen or creates new ones as necessary.
			//
			public override UITableViewCell GetCell (UITableView tableView, NSIndexPath indexPath)
			{
				var cell = tableView.DequeueReusableCell (RoomSelectController.classCellId);

				if (cell == null)
					cell = new UITableViewCell (UITableViewCellStyle.Default, RoomSelectController.classCellId);

				int row = indexPath.Row;
				cell.TextLabel.Text = controller.classList [row];
				return cell;
			}

			public override void RowSelected (UITableView tableView, NSIndexPath indexPath)
			{
				// NOTE: Don't call the base implementation on a Model class
				// see http://docs.xamarin.com/guides/ios/application_fundamentals/delegates,_protocols,_and_events
				ClassItem joiningClass = controller.classList [indexPath.Row];
				FibeClass joined = Application.engine.JoinClass (joiningClass.ClassPath.Split(','));

				InClassController classView = controller.Storyboard.InstantiateViewController ("InClassController") as InClassController;
				classView.joined = joined;

				controller.NavigationController.PushViewController (classView, true);
			}
		}
	}
}
