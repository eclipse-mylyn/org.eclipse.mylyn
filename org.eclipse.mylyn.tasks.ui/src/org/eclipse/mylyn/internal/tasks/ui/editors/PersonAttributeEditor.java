/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.mylyn.tasks.core.data.AttributeManager;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Steffen Pingel
 */
public class PersonAttributeEditor extends TextAttributeEditor {

	public PersonAttributeEditor(AttributeManager manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
	}

	@Override
	public String getValue() {
		return getAttributeMapper().getRepositoryPerson(getTaskAttribute()).toString();
	}

}
