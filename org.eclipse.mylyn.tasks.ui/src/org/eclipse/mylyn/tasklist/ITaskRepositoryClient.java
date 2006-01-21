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

import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractNewQueryPage;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;


/**
 * @author Mik Kersten
 */
public interface ITaskRepositoryClient {

	public abstract String getLabel();

	/**
	 * @return the unique type of the repository, e.g. "bugzilla"
	 */
	public abstract String getKind();
	
	/**
	 * @param id	identifier, e.g. "123" bug Bugzilla bug 123
	 * @return		null if task could not be created
	 */
	public abstract ITask createTaskFromExistingId(TaskRepository repository, String id);
	
	public abstract AbstractRepositorySettingsPage getSettingsPage();
	
	public abstract AbstractNewQueryPage getQueryPage(TaskRepository repository);
}
