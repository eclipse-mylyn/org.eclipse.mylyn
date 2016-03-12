/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.internal.tasks.ui.editors.CheckboxMultiSelectAttributeEditor;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;

/**
 * @author Rob Elves
 */
public class BugzillaKeywordAttributeEditor extends CheckboxMultiSelectAttributeEditor {

	public BugzillaKeywordAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);

	}

	@Override
	public List<String> getValues() {
		List<String> values = new ArrayList<String>();
		values = getTaskAttribute().getValues();
		return values;
	}

	@Override
	public void setValues(List<String> newValues) {
		Collections.sort(newValues);
		getTaskAttribute().clearValues();
		getTaskAttribute().setValues(newValues);
		attributeChanged();
	}

}