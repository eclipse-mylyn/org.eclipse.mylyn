/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac;

import org.eclipse.mylar.internal.tasklist.TaskExternalizationException;
import org.eclipse.mylar.provisional.tasklist.AbstractTaskContainer;
import org.eclipse.mylar.provisional.tasklist.DelegatingTaskExternalizer;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.TaskList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Steffen Pingel
 */
public class TracTaskExternalizer extends DelegatingTaskExternalizer {

	private static final String KEY_TRAC = "Trac";

	private static final String KEY_TRAC_CATEGORY = KEY_TRAC + KEY_CATEGORY;

	private static final String KEY_TRAC_TASK = KEY_TRAC + KEY_TASK;

	@Override
	public boolean canCreateElementFor(ITask task) {
		return task instanceof TracTask;
	}

	@Override
	public boolean canReadCategory(Node node) {
		return node.getNodeName().equals(KEY_TRAC_CATEGORY);
	}

	@Override
	public String getCategoryTagName() {
		return KEY_TRAC_CATEGORY;
	}

	@Override
	public String getTaskTagName() {
		return KEY_TRAC_TASK;
	}

	@Override
	public ITask readTask(Node node, TaskList taskList, AbstractTaskContainer category, ITask parent)
			throws TaskExternalizationException {

		Element element = (Element) node;
		String handle;
		String label;
		if (element.hasAttribute(KEY_HANDLE)) {
			handle = element.getAttribute(KEY_HANDLE);
		} else {
			throw new TaskExternalizationException("Handle not stored for task");
		}
		if (element.hasAttribute(KEY_LABEL)) {
			label = element.getAttribute(KEY_LABEL);
		} else {
			throw new TaskExternalizationException("Description not stored for task");
		}

		TracTask task = new TracTask(handle, label, false);
		readTaskInfo(task, taskList, element, parent, category);
		return task;
	}

}
