/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;


/**
 * Listener for task list modifications and task content modifications.
 * 
 * @author Mik Kersten
 */
public interface ITaskListChangeListener {

	/**
	 * called when task changes state (incoming/outgoing/synchronizing...)
	 */
	public abstract void localInfoChanged(AbstractTask task);

	/**
	 * Called when task data (repository task data or edits) have changed
     * TODO: Extract into separate interface
	 */
	public abstract void repositoryInfoChanged(AbstractTask task);
	
	public abstract void taskDeleted(AbstractTask task);
	
	public abstract void containerAdded(AbstractTaskListElement container);
		
	public abstract void containerDeleted(AbstractTaskListElement container);

	public abstract void containerInfoChanged(AbstractTaskListElement container);

	/**
	 * @param task
	 * @param fromContainer	can be null
	 * @param toContainer	can be null
	 */
	public abstract void taskMoved(AbstractTask task, AbstractTaskListElement fromContainer, AbstractTaskListElement toContainer);

	public abstract void taskAdded(AbstractTask task);

}
