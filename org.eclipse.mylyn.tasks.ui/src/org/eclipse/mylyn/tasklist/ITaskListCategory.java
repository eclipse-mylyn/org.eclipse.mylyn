package org.eclipse.mylar.tasklist;

import java.util.List;


public interface ITaskListCategory extends ITaskListElement {

	public List<ITask> getChildren();
	
	public void removeTask(ITask task);
	
	public boolean isArchive();

	public void setIsArchive(boolean isArchive);

}