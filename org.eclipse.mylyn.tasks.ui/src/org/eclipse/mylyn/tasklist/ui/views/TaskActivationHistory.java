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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;

/**
* @author Ken Sueda (Original implementation)
* @author Wesley Coelho (Added persistent tasks)
*/
public class TaskActivationHistory {
	
	private List<ITask> history = new ArrayList<ITask>();
	
	private int currentIndex = -1;
	
	/** The number of tasks from the previous session to load into the history*/
	private static final int PERSISTENT_HISTORY_SIZE = 5;
 
	private boolean persistentHistoryLoaded = false;
	
	public TaskActivationHistory() {	


	}

	/**
	 * Load in a number of saved history tasks from previous session.
	 * Should be called from constructor but ContextManager doesn't 
	 * seem to be able to provide activity history at that point
	 * @author Wesley Coelho
	 */
	protected void loadPersistentHistory(){
		int tasksAdded = 0;
		
		for(int i = MylarPlugin.getContextManager().getActivityHistory().getInteractionHistory().size() - 1; i >=0; i--){
			ITask prevTask = getHistoryTaskAt(i);
			
			if (prevTask != null && !isDuplicate(prevTask, i + 1)) {
				history.add(0, prevTask);
				currentIndex++;
				tasksAdded++;
				if (tasksAdded == PERSISTENT_HISTORY_SIZE){
					break;
				}
			}
		}		
	}
	
	/**
	 * Returns true if the specified task appears in the activity 
	 * history between the starting index and the end of the history list.
	 * @author Wesley Coelho
	 */
	protected boolean isDuplicate(ITask task, int startingIndex){
		for (int i = startingIndex; i < MylarPlugin.getContextManager().getActivityHistory().getInteractionHistory().size(); i++){
			ITask currTask = getHistoryTaskAt(i);
			if(currTask != null && currTask.getHandle() == task.getHandle()){
				return true;
			}
		}
		return false;
	}
	
	/** 
	 * Returns the task corresponding to the interaction event history item at the specified position
	 * @author Wesley Coelho
	 */
	protected ITask getHistoryTaskAt(int pos){
		InteractionEvent event = MylarPlugin.getContextManager().getActivityHistory().getInteractionHistory().get(pos);
		return MylarTasklistPlugin.getTaskListManager().getTaskForHandle(event.getStructureHandle(), false);
	}
	
	public void addTask(ITask task) {
		try {
			if (!persistentHistoryLoaded){
				loadPersistentHistory();
				persistentHistoryLoaded = true;
			}
			
			if (hasNext()) {
				for (int i = currentIndex+1; i < history.size();) {
					history.remove(i);
				}			
			} 
			if (history.remove(task)){
				currentIndex--;
			}
			history.add(task);
			currentIndex++;
		} catch (RuntimeException e) {
			MylarPlugin.fail(e, "could not add task to history", false);
		}
	}
	
	public ITask getPreviousTask() {
		try {
			if (hasPrevious()) {
				if((currentIndex == 0 && !history.get(currentIndex).isActive())){
					return history.get(currentIndex);
				} else {
					return history.get(--currentIndex);
				}
			} else {
				return null;
			}
		} catch (RuntimeException e) {
			MylarPlugin.fail(e, "could not get previous task from history", false);
			return null;
		}		
	}
	
	public boolean hasPrevious() {
		try {
			if (!persistentHistoryLoaded){
				loadPersistentHistory();
				persistentHistoryLoaded = true;
			}
			
			return (currentIndex == 0 && !history.get(currentIndex).isActive()) || currentIndex > 0;
		} catch (RuntimeException e) {
			MylarPlugin.fail(e, "could determine previous task", false);
			return false;
		}			
	}
	
	public ITask getNextTask() {
		try {
			if (hasNext()) {
				return history.get(++currentIndex);
			} else {
				return null;
			}
		} catch (RuntimeException e) {
			MylarPlugin.fail(e, "could not get next task", false);
			return null;
		}		
	}
	
	public boolean hasNext() {
		try {
			return currentIndex < history.size() - 1;
		} catch (RuntimeException e) {
			MylarPlugin.fail(e, "could not get next task", false);
			return false;
		}
	}
	
	public void clear() {
		try {
			history.clear();
		} catch (RuntimeException e) {
			MylarPlugin.fail(e, "could not clear history", false);
		}
	}
}

///**
// * @author Ken Sueda (interface)
// * @author Wesley Coelho (new implementation)
// * 
// * Note: Getting the previous or next task involves iterating until one is found
// * because there could be any number of interaction events for the same task
// * in the history. However, n should be less than 10 or so in most cases so
// * this shouldn't be a performance problem.
// */
//public class TaskActivationHistory {
//	
//	protected MylarContextManager manager = MylarPlugin.getContextManager();
//	protected int currentIndex = -1;
//	protected int stackPos = 0;
//    
//	public TaskActivationHistory() {	
//	
//	
//	}
//
//	public void addTask(ITask task) {
//		clear();
//	}
//	
//	public ITask getPreviousTask() {
//		if (hasPrevious()){
//			stackPos--;
//			return getUniquePreviousTask(true);
//		}
//		else{
//			return null;
//		}
//	}
//
//	public boolean hasPrevious() {		
//		return getUniquePreviousTask(false) != null;
//	}
//	
//	protected ITask getUniquePreviousTask(boolean setIndex){
//		int pos = currentIndex;
//		
//		if (pos == -1){
//			pos = manager.getActivityHistory().getInteractionHistory().size() - 1;
//		}
//		
//		while (pos >= 0){
//			if (getHistoryTaskAt(pos) != null && !getHistoryTaskAt(pos).isActive()) {
//							
//				//Don't go back to this task if it's
//				// a duplicate of something already backed through
//				ITask proposedPrevTask = getHistoryTaskAt(pos);
//				boolean anotherTaskReached = false;
//				boolean duplicate = false;
//				for(int i = pos; i < manager.getActivityHistory().getInteractionHistory().size() - 1; i++){
//					ITask currTask = getHistoryTaskAt(i);
//					if (currTask != proposedPrevTask){
//						anotherTaskReached = true;
//						continue;
//					}
//				
//					if (anotherTaskReached && currTask == proposedPrevTask){
//						duplicate = true;
//					}
//				}	
//				
//				if (!duplicate){
//					if(setIndex){
//						currentIndex = pos;
//					}
//					return proposedPrevTask;
//				}
//			}
//			pos--;
//		}
//		
//		return null;
//	}
//	
//	public ITask getNextTask() {
//		if (hasNext()) {
//			for(int i = currentIndex; i < manager.getActivityHistory().getInteractionHistory().size(); i++){
//				ITask task = getHistoryTaskAt(i);
//				if(task != null && !task.isActive()){
//					currentIndex = i;
//					stackPos++;
//					if (stackPos == 0){
//						currentIndex = -1;
//					}
//					else{
//						
////						//See if there is another task further down (but before the starting point)
////						//that is the same as this task. If so, the
////						//current task would have been skipped on the
////						//way back so we should move the pointer over.
////						int tempStackPos = stackPos;
////						ITask prevTask = task;
////						for(int j = currentIndex; j < manager.getActivityHistory().getInteractionHistory().size();j++){
////							ITask currTask = getHistoryTaskAt(j);
////							
////							if (currTask == null){
////								continue;
////							}
////							
////							if (currTask != prevTask){
////								prevTask = currTask;
////								tempStackPos++;
////							}
////							
////							if (tempStackPos > 0){
////								break;
////							}
////							
////							if(currTask == task){
////								currentIndex = j;
////							}
////						}
//					}
//					return task;
//				}
//			}
//			return null;
//		} else {
//			return null;
//		}		
//	}
//	
//	public boolean hasNext() {
//		if (currentIndex == -1){
//			return false;
//		}
//		else{
//			for(int i = currentIndex; i < manager.getActivityHistory().getInteractionHistory().size(); i++){
//				if(getHistoryTaskAt(i) != null && !getHistoryTaskAt(i).isActive()){
//					return true;
//				}
//			}
//			return false;
//		}
//	}
//	
//	/** Returns the task corresponding to the interaction event history item at the specified position */
//	protected ITask getHistoryTaskAt(int pos){
//		InteractionEvent event = manager.getActivityHistory().getInteractionHistory().get(pos);
//		return MylarTasklistPlugin.getTaskListManager().getTaskForHandle(event.getStructureHandle(), false);
//	}
//	
//	/** Note: Doesn't really clear, just resets the history pointer*/
//	public void clear() {
//		currentIndex = -1;
//		stackPos = 0;
//	}
//}



