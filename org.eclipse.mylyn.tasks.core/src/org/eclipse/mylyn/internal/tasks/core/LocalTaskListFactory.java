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

import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskListFactory;
import org.w3c.dom.Element;

/**
 * @author Rob Elves
 */
public class LocalTaskListFactory extends AbstractTaskListFactory {

	@Override
	public boolean canCreate(AbstractTask task) {
		return task instanceof LocalTask;
	}

	@Override
	public AbstractTask createTask(String repositoryUrl, String taskId, String summary, Element element) {
		LocalTask task = new LocalTask(taskId, summary);
		return task;
	}

	@Override
	public String getTaskElementName() {
		return AbstractTaskListFactory.KEY_TASK;
	}

}
