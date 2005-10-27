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

import org.eclipse.jface.action.MenuManager;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;

public interface IDynamicSubMenuContributor {

	public abstract MenuManager getSubMenuManager(TaskListView view,
			ITaskListElement selection);

}