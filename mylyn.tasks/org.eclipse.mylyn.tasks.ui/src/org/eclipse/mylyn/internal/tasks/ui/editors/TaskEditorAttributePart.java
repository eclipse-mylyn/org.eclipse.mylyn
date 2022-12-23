/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;

/**
 * @author Steffen Pingel
 */
public class TaskEditorAttributePart extends AbstractTaskEditorAttributeSection {

	public TaskEditorAttributePart() {
		setPartName(Messages.TaskEditorAttributePart_Attributes);
		setNeedsRefresh(true);
	}

	@Override
	protected Collection<TaskAttribute> getAttributes() {
		Map<String, TaskAttribute> allAttributes = getTaskData().getRoot().getAttributes();
		List<TaskAttribute> attributes = new ArrayList<TaskAttribute>(allAttributes.size());
		for (TaskAttribute attribute : allAttributes.values()) {
			TaskAttributeMetaData properties = attribute.getMetaData();
			if (TaskAttribute.KIND_DEFAULT.equals(properties.getKind())) {
				attributes.add(attribute);
			}
		}
		return attributes;
	}

	@Override
	protected List<TaskAttribute> getOverlayAttributes() {
		TaskAttribute product = getModel().getTaskData().getRoot().getMappedAttribute(TaskAttribute.PRODUCT);
		List<TaskAttribute> attributes = new ArrayList<TaskAttribute>(2);
		if (product != null) {
			attributes.add(product);
		}
		TaskAttribute component = getModel().getTaskData().getRoot().getMappedAttribute(TaskAttribute.COMPONENT);
		if (component != null) {
			attributes.add(component);
		}
		return attributes;
	}

}
