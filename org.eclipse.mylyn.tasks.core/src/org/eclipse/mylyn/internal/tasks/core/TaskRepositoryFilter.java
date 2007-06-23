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

import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Task repository filter to build list of repositories with required capabilities.
 * 
 * @author Eugene Kleshov 
 * @since 2.0
 */
public interface TaskRepositoryFilter {

	public static TaskRepositoryFilter ALL = new TaskRepositoryFilter() {
		public boolean accept(TaskRepository repository, AbstractRepositoryConnector connector) {
			return true;
		}
	};

	public static TaskRepositoryFilter CAN_QUERY = new TaskRepositoryFilter() {
		public boolean accept(TaskRepository repository, AbstractRepositoryConnector connector) {
			return !(connector instanceof LocalRepositoryConnector);
		}
	};
	
	public static TaskRepositoryFilter CAN_CREATE_NEW_TASK = new TaskRepositoryFilter() {
		public boolean accept(TaskRepository repository, AbstractRepositoryConnector connector) {
			return connector.canCreateNewTask(repository);
		}
	};

	public static TaskRepositoryFilter CAN_CREATE_TASK_FROM_KEY = new TaskRepositoryFilter() {
		public boolean accept(TaskRepository repository, AbstractRepositoryConnector connector) {
			return connector.canCreateTaskFromKey(repository);
		}
	};
	
	public static TaskRepositoryFilter IS_USER_MANAGED = new TaskRepositoryFilter(){
		public boolean accept(TaskRepository repository, AbstractRepositoryConnector connector) {
			return connector.isUserManaged();
		}
	};

	boolean accept(TaskRepository repository, AbstractRepositoryConnector connector);

}