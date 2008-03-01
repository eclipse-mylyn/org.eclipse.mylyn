/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;

/**
 * @since 2.3
 * @author Steffen Pingel
 */
public class AttributeEditorFactory {

	private final AttributeManager manager;

	public AttributeEditorFactory(AttributeManager manager) {
		this.manager = manager;
	}

	public AbstractAttributeEditor createEditor(String type, RepositoryTaskAttribute taskAttribute) {
		Assert.isNotNull(type);

		if (RepositoryTaskAttribute.TYPE_DATE.equals(type)) {
			// FIXME map attribute ids
			if (RepositoryTaskAttribute.DATE_CREATION.equals(taskAttribute.getId())
					|| RepositoryTaskAttribute.DATE_MODIFIED.equals(taskAttribute.getId())) {
				return new SimpleDateAttributeEditor(manager, taskAttribute);
			} else {
				return new DateAttributeEditor(manager, taskAttribute);
			}
		} else if (RepositoryTaskAttribute.TYPE_LONG_TEXT.equals(type)) {
			return new LongTextAttributeEditor(manager, taskAttribute);
		} else if (RepositoryTaskAttribute.TYPE_MULTI_SELECT.equals(type)) {
			return new MultiSelectionAttributeEditor(manager, taskAttribute);
		} else if (RepositoryTaskAttribute.TYPE_SHORT_TEXT.equals(type)) {
			return new TextAttributeEditor(manager, taskAttribute);
		} else if (RepositoryTaskAttribute.TYPE_SINGLE_SELECT.equals(type)) {
			return new SingleSelectionAttributeEditor(manager, taskAttribute);
		} else if (RepositoryTaskAttribute.TYPE_TASK_DEPENDENCY.equals(type)) {
			return new TaskDependendyAttributeEditor(manager, taskAttribute);
		}

		throw new IllegalArgumentException("Unsupported editor type: \"" + type + "\"");
	}

}
