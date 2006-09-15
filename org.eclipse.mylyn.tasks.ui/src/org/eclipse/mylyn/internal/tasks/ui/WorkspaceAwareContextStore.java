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

package org.eclipse.mylar.internal.tasks.ui;

import java.io.File;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.context.core.AbstractContextStore;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.tasks.ui.TaskListDataMigration;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 */
public class WorkspaceAwareContextStore extends AbstractContextStore {

	public WorkspaceAwareContextStore() {
		migrateFrom06Format();
	}
	
	public File getRootDirectory() {
		return new File(TasksUiPlugin.getDefault().getDataDirectory());
	}

	private void migrateFrom06Format() {
		File dataDir = new File(TasksUiPlugin.getDefault().getDataDirectory());
		try {
			new TaskListDataMigration(dataDir).run(new NullProgressMonitor());
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Error occurred while migrating mylar data", false);			
		}
	}

}
