/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Steffen Pingel
 */
public class DefaultTaskMapping extends TaskMapping {

	public Map<String, String> values = new HashMap<String, String>();

	@Override
	public String getDescription() {
		return values.get(TaskAttribute.DESCRIPTION);
	}

	@Override
	public String getSummary() {
		return values.get(TaskAttribute.SUMMARY);
	}

	public void setDescription(String value) {
		values.put(TaskAttribute.DESCRIPTION, value);
	}

	public void setSummary(String value) {
		values.put(TaskAttribute.SUMMARY, value);
	}

}
