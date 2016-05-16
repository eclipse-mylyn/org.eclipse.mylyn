/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.externalization;

import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.ITask;
import org.xml.sax.SAXException;

public class SaxCategoryWriter extends SaxTaskListElementWriter<AbstractTaskCategory> {

	public SaxCategoryWriter(ContentHandlerWrapper handler) {
		super(handler);
	}

	@Override
	public void writeElement(AbstractTaskCategory category) throws SAXException {
		AttributesWrapper attributes = new AttributesWrapper();
		attributes.addAttribute(TaskListExternalizationConstants.KEY_HANDLE, category.getHandleIdentifier());
		attributes.addAttribute(TaskListExternalizationConstants.KEY_NAME, category.getSummary());
		handler.startElement(TaskListExternalizationConstants.NODE_CATEGORY, attributes);
		for (ITask task : category.getChildren()) {
			createTaskReference(task);
		}
		handler.endElement(TaskListExternalizationConstants.NODE_CATEGORY);
	}

	private void createTaskReference(ITask task) throws SAXException {
		AttributesWrapper attributes = new AttributesWrapper();
		attributes.addAttribute(TaskListExternalizationConstants.KEY_HANDLE, task.getHandleIdentifier());
		handler.startElement(TaskListExternalizationConstants.NODE_TASK_REFERENCE, attributes);
		handler.endElement(TaskListExternalizationConstants.NODE_TASK_REFERENCE);
	}

}
