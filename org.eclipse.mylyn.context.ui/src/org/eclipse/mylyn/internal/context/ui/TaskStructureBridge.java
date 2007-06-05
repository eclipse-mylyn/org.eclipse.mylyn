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

package org.eclipse.mylar.internal.context.ui;

import java.util.Collections;
import java.util.List;

import org.eclipse.mylar.context.core.AbstractContextStructureBridge;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 */
public class TaskStructureBridge extends AbstractContextStructureBridge {

	public static final String CONTENT_TYPE = "meta/task";

	@Override
	public String getContentType() {
		return CONTENT_TYPE;
	}

	@Override
	public String getHandleIdentifier(Object object) {
		if (object instanceof ITask) {
			return ((ITask)object).getHandleIdentifier();
		} else {
			return null;
		}
	}

	@Override
	public String getParentHandle(String handle) {
		return null;
	}

	@Override
	public Object getObjectForHandle(String handle) {
		return TasksUiPlugin.getTaskListManager().getTaskList().getTask(handle);
	}

	@Override
	public List<String> getChildHandles(String handle) {
		return Collections.emptyList();
	}

	@Override
	public String getName(Object object) {
		if (object instanceof ITask) {
			return ((ITask)object).getSummary();
		} else {
			return null;
		}
	}

	@Override
	public boolean canBeLandmark(String handle) {
		return false;
	}

	@Override
	public boolean acceptsObject(Object object) {
		return object instanceof ITaskListElement;
	}

	@Override
	public boolean canFilter(Object object) {
		return object instanceof ITask;
	}

	@Override
	public boolean isDocument(String handle) {
		return getObjectForHandle(handle) instanceof ITask;
	}

	@Override
	public String getHandleForOffsetInObject(Object resource, int offset) {
		return null;
	}

	@Override
	public String getContentType(String elementHandle) {
		return getContentType();
	}
}
