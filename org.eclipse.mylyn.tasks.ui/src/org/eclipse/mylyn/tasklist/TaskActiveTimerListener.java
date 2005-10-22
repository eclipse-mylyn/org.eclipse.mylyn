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

package org.eclipse.mylar.tasklist;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.ActivityTimerThread;
import org.eclipse.mylar.core.util.IActiveTimerListener;
import org.eclipse.mylar.core.util.IInteractionEventListener;

/**
 * @author Shawn Minto
 */
public class TaskActiveTimerListener implements IActiveTimerListener, IInteractionEventListener {

	private ActivityTimerThread timer;
	
	private ITask task;

	private boolean isTaskStalled = false;
	
	public TaskActiveTimerListener(ITask task){
		this.task = task;
		timer = new ActivityTimerThread(MylarPlugin.getContextManager().getActivityTimeoutSeconds());
		timer.addListener(this);
		timer.start();
		MylarPlugin.getDefault().addInteractionListener(this);
	}
	
	public void fireTimedOut() {
		task.setActive(task.isActive(), true);
		isTaskStalled = true;
		timer.resetTimer();
	}

	public void interactionObserved(InteractionEvent event) {
		timer.resetTimer();		
		
		if(isTaskStalled){
			task.setActive(task.isActive(), false);
		}
		isTaskStalled = false;
	} 

	public void start() {} 

	public void stopTimer() {
		timer.killThread();
		MylarPlugin.getDefault().removeInteractionListener(this);
	}

	public void stop() {}
}
