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

import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.DelegatingTaskExternalizer;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskExternalizationException;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.w3c.dom.Element;

/**
 * @author Rob Elves
 */
public class LocalTaskExternalizer extends DelegatingTaskExternalizer {

	@Override
	public boolean canCreateElementFor(AbstractTask task) {
		return task instanceof LocalTask;
	}

	@Override
	public AbstractTask createTask(String repositoryUrl, String taskId, String summary, Element element, TaskList taskList,
			AbstractTaskContainer category, AbstractTask parent) throws TaskExternalizationException {
//		String handle;
//		if (element.hasAttribute(KEY_HANDLE)) {
//			handle = element.getAttribute(KEY_HANDLE);
//		} else {
//			throw new TaskExternalizationException("Handle not stored for task");
//		}
		LocalTask task = new LocalTask(taskId, summary);
		return task;
	}

}
