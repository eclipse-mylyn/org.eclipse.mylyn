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

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

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
		String selectedKeywords = getAttributeMapper().getValue(getTaskAttribute());
		StringTokenizer st = new StringTokenizer(selectedKeywords, ",", false); //$NON-NLS-1$
		while (st.hasMoreTokens()) {
			String s = st.nextToken().trim();
			values.add(s);
		}

		return values;
	}

	@Override
	public void setValues(List<String> newValues) {
		StringBuilder valueString = new StringBuilder();
		Collections.sort(newValues);
		for (int i = 0; i < newValues.size(); i++) {
			valueString.append(newValues.get(i));
			if (i != newValues.size() - 1) {
				valueString.append(", "); //$NON-NLS-1$
			}
		}
		getAttributeMapper().setValue(getTaskAttribute(), valueString.toString());
		attributeChanged();
	}

}
