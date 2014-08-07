using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace StudentDemo
{
    class Enities
    {
    }

    public class ClassItem
    {
        public String ClassPath { get; private set; }
        public String Description { get; private set; }

        public String Title
        {
            get
            {
                return ClassPath + " - " + Description;
            }
        }

        public ClassItem(String Path, String Description)
        {
            ClassPath = Path;
            this.Description = Description;
        }

		public static implicit operator String(ClassItem theItem) {
			return theItem.Title;
		}
    }
}
