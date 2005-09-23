/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.tasklist.ui.views;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;


/**
 * @author Ken Sueda (interface)
 * @author Wesley Coelho (new implementation)
 * 
 * Note: Getting the previous or next task involves iterating until one is found
 * because there could be any number of interaction events for the same task
 * in the history. However, n should be less than 10 or so in most cases so
 * this shouldn't be a performance problem.
 */
public class TaskActivationHistory {
	
	protected MylarContextManager manager = MylarPlugin.getContextManager();
	protected int currentIndex = -1;
	protected int stackPos = 0;
    
	public TaskActivationHistory() {	
	
	
	}

	public void addTask(ITask task) {
		clear();
	}
	
	public ITask getPreviousTask() {
		if (hasPrevious()){
			stackPos--;
			return getUniquePreviousTask(true);
		}
		else{
			return null;
		}
	}

	public boolean hasPrevious() {		
		return getUniquePreviousTask(false) != null;
	}
	
	protected ITask getUniquePreviousTask(boolean setIndex){
		int pos = currentIndex;
		
		if (pos == -1){
			pos = manager.getActivityHistory().getInteractionHistory().size() - 1;
		}
		
		while (pos >= 0){
			if (getHistoryTaskAt(pos) != null && !getHistoryTaskAt(pos).isActive()) {
							
				//Don't go back to this task if it's
				// a duplicate of something already backed through
				ITask proposedPrevTask = getHistoryTaskAt(pos);
				boolean anotherTaskReached = false;
				boolean duplicate = false;
				for(int i = pos; i < manager.getActivityHistory().getInteractionHistory().size() - 1; i++){
					ITask currTask = getHistoryTaskAt(i);
					if (currTask != proposedPrevTask){
						anotherTaskReached = true;
						continue;
					}
				
					if (anotherTaskReached && currTask == proposedPrevTask){
						duplicate = true;
					}
				}
				//---
				
				
				if (!duplicate){
					if(setIndex){
						currentIndex = pos;
					}
					return proposedPrevTask;
				}
			}
			pos--;
		}
		
		return null;
	}
	
	public ITask getNextTask() {
		if (hasNext()) {
			for(int i = currentIndex; i < manager.getActivityHistory().getInteractionHistory().size(); i++){
				ITask task = getHistoryTaskAt(i);
				if(task != null && !task.isActive()){
					currentIndex = i;
					stackPos++;
					if (stackPos == 0){
						currentIndex = -1;
					}
					return task;
				}
			}
			return null;
		} else {
			return null;
		}		
	}
	
	public boolean hasNext() {
		if (currentIndex == -1){
			return false;
		}
		else{
			for(int i = currentIndex; i < manager.getActivityHistory().getInteractionHistory().size(); i++){
				if(getHistoryTaskAt(i) != null && !getHistoryTaskAt(i).isActive()){
					return true;
				}
			}
			return false;
		}
	}
	
	/** Returns the task corresponding to the interaction event history item at the specified position */
	protected ITask getHistoryTaskAt(int pos){
		InteractionEvent event = manager.getActivityHistory().getInteractionHistory().get(pos);
		return MylarTasklistPlugin.getTaskListManager().getTaskForHandle(event.getStructureHandle(), false);
	}
	
	/** Note: Doesn't really clear, just resets the history pointer*/
	public void clear() {
		currentIndex = -1;
		stackPos = 0;
	}
}


///**
// * @author Ken Sueda
// */
//public class TaskActivationHistory {
//	private List<ITask> history = new ArrayList<ITask>();
//	private int currentIndex = -1;
//    
//	public TaskActivationHistory() {	
//	}
//	
//	public void addTask(ITask task) {
//		if (hasNext()) {
//			for (int i = currentIndex+1; i < history.size();) {
//				history.remove(i);
//			}			
//		} 			
//		history.add(task);
//		currentIndex++;
//	}
//	
//	public ITask getPreviousTask() {
//		if (hasPrevious()) {
//			if((currentIndex == 0 && !history.get(currentIndex).isActive())){
//				return history.get(currentIndex);
//			} else {
//				return history.get(--currentIndex);
//			}
//		} else {
//			return null;
//		}		
//	}
//	
//	public boolean hasPrevious() {
//		return (currentIndex == 0 && !history.get(currentIndex).isActive()) || currentIndex > 0;			
//	}
//	
//	public ITask getNextTask() {
//		if (hasNext()) {
//			return history.get(++currentIndex);
//		} else {
//			return null;
//		}		
//	}
//	
//	public boolean hasNext() {
//		return currentIndex < history.size() - 1;
//	}
//	
//	public void clear() {
//		history.clear();
//	}
//}
