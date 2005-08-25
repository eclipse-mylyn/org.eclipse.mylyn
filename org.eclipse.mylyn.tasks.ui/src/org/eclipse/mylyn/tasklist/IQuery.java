package org.eclipse.mylar.tasklist;

import java.util.List;

public interface IQuery extends ITaskListElement{

	public String getQueryString();
	
	public void setQueryString(String query);
	
	public List<IQueryHit> getChildren();
	
	public int getMaxHits();
	
	public void setMaxHits(int maxHits);
}
