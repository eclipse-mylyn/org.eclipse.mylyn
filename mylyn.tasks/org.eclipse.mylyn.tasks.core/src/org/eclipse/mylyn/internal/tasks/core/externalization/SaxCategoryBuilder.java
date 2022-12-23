/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core.externalization;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.ITransferList;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.xml.sax.Attributes;

import com.google.common.base.Strings;

public class SaxCategoryBuilder extends SaxTaskListElementBuilder<AbstractTaskCategory> {

	private AbstractTaskCategory category;

	private final ITransferList taskList;

	public SaxCategoryBuilder(ITransferList taskList) {
		this.taskList = taskList;
	}

	@Override
	public void beginItem(Attributes elementAttributes) {
		try {
			String name = elementAttributes.getValue(TaskListExternalizationConstants.KEY_NAME);
			if (!Strings.isNullOrEmpty(name)) {
				String handle = elementAttributes.getValue(TaskListExternalizationConstants.KEY_HANDLE);
				if (Strings.isNullOrEmpty(handle)) {
					handle = name;
				}
				category = taskList.getContainerForHandle(handle);
				if (category == null) {
					category = new TaskCategory(handle, name);
				} else if (!UncategorizedTaskContainer.HANDLE.equals(handle)) {
					addError(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
							String.format("Category with handle \"%s\" already exists in task list", handle))); //$NON-NLS-1$
				}
			} else {
				addError(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
						"Category is missing name attribute")); //$NON-NLS-1$
			}
		} catch (Exception e) {
			addError(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
					String.format("Exception reading category: %s", e.getMessage()), e)); //$NON-NLS-1$
		}
	}

	@Override
	protected void applyAttribute(String attributeKey, String attributeValue) {
		// do nothing categories do not have arbitrary attributes
	}

	@Override
	public AbstractTaskCategory getItem() {
		return category;
	}

	@Override
	public void addToTaskList(ITransferList taskList) {
		// don't add unmatched categories
		if (category instanceof TaskCategory) {
			taskList.addCategory((TaskCategory) category);
		}
	}

}
