/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Jevgeni Holodkov - query insertion
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.mylyn.internal.tasks.ui.util.TaskListElementImporter;

/**
 * Provides facilities for using and managing the Task List and task activity information.
 * 
 * @author Mik Kersten
 * @author Rob Elves (task activity)
 * @since 3.0
 */
@Deprecated
public class TaskListManager {

	private final TaskListElementImporter importer;

	public TaskListManager(TaskListElementImporter importer) {
		this.importer = importer;
	}

}
