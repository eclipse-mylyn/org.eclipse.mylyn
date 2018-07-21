/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;

/**
 * @author Mik Kersten
 */
public final class TaskCategory extends AbstractTaskCategory {

	private String summary;

	public TaskCategory(String handle, String summary) {
		super(handle);
		setSummary(summary);
	}

	public TaskCategory(String handleAndDescription) {
		this(handleAndDescription, handleAndDescription);
	}

	/**
	 * null if no parent category
	 */
	public static AbstractTaskCategory getParentTaskCategory(ITask task) {
		AbstractTaskCategory category = null;
		if (task != null) {
			for (ITaskContainer container : ((AbstractTask) task).getParentContainers()) {
				if (container instanceof AbstractTaskCategory) {
					category = (AbstractTaskCategory) container;
				}
			}
		}
		return category;
	}

	@Override
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

}
