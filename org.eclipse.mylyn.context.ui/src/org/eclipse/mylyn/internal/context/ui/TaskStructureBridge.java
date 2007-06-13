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

package org.eclipse.mylyn.internal.context.ui;

import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskListElement;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

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
		if (object instanceof AbstractTask) {
			return ((AbstractTask)object).getHandleIdentifier();
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
		if (object instanceof AbstractTask) {
			return ((AbstractTask)object).getSummary();
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
		return object instanceof AbstractTaskListElement;
	}

	@Override
	public boolean canFilter(Object object) {
		return object instanceof AbstractTask;
	}

	@Override
	public boolean isDocument(String handle) {
		return getObjectForHandle(handle) instanceof AbstractTask;
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
