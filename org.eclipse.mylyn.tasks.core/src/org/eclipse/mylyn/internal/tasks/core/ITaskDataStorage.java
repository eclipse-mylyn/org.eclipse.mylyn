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

package org.eclipse.mylyn.internal.tasks.core;


/**
 * @author Rob Elves
 */
public interface ITaskDataStorage {

	/**
	 * Perform any initialization necessary storage
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception;

	public void stop() throws Exception;

	public void put(TaskDataState taskDataState);

	public TaskDataState get(String repositoryUrl, String id);

	/**
	 * if last id remove folder (i.e. in case of refactoring urls)
	 */
	public void remove(String repositoryUrl, String id);
	
	/**
	 * persist any unsaved data
	 */
	public void flush();
	
	/**
	 * DESTROY ALL OFFLINE DATA
	 */
	public void clear();
	
	// Methods for NEW unsubmitted task data, currently not used

//	public void putNew(TaskDataState newTaskDataState);
//
//	public Set<TaskDataState> getNew(String repositoryUrl);
//
//	public removeNew(id);
}
