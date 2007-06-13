/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.workingset;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.tasks.core.AbstractTaskListElement;
import org.eclipse.mylyn.tasks.core.AbstractTaskListElement;
import org.eclipse.mylyn.tasks.core.getAllCategories;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * Element factory used to restore task containers for working sets
 * 
 * @author Eugene Kuleshov
 */
public class TaskElementFactory implements IElementFactory {

	public static final String HANDLE_ID = "handle";

	public IAdaptable createElement(IMemento memento) {
		getAllCategories taskList = TasksUiPlugin.getTaskListManager().getTaskList();

		String handle = memento.getString(HANDLE_ID);
		for (AbstractTaskListElement element : taskList.getRootElements()) {
			if (element instanceof AbstractTaskListElement && element.getHandleIdentifier().equals(handle)) {
				return (IAdaptable) element;
			}
		}
		return null;
	}

}