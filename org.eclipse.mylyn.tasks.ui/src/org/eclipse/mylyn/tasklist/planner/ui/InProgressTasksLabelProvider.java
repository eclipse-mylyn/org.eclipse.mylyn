
package org.eclipse.mylar.tasklist.planner.ui;

import java.text.DateFormat;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.DateUtil;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskListElement;
import org.eclipse.swt.graphics.Image;

/**
 * @author Wesley Coelho (Adapted from CompletedTasksLabelProvider)
 */
public class InProgressTasksLabelProvider extends LabelProvider implements ITableLabelProvider {

	//private String[] columnNames = new String[] { "Description", "Priority", "Date Completed", "Duration"};
	public Image getColumnImage(Object element, int columnIndex) {		
		if (! (element instanceof ITaskListElement)) { 
        	return null;
        }
		if (columnIndex == 0) {
			return ((ITaskListElement)element).getIcon();
		} else {
			return null;
		}
	}

	public String getColumnText(Object element, int columnIndex) {
		
		try {
			if (element instanceof ITask) {
				ITask task = (ITask) element;
				switch(columnIndex) {
				case 1: 
					return task.getDescription(true);				
				case 2:
					return task.getPriority();
				case 3:
					return DateFormat.getDateInstance(DateFormat.SHORT).format(task.getCreationDate());
				case 4:
					return DateUtil.getFormattedDurationShort(task.getElapsedMillis());
				}	
			}
		} catch (RuntimeException e) {
			MylarPlugin.fail(e, "Could not produce in progress task label", false);
			return "";
		}		
		return null;
	}

}
