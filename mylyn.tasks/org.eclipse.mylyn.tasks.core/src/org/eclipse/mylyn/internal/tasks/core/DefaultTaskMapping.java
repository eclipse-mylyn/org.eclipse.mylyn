/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.TaskInitializationData;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Steffen Pingel
 * @deprecated use {@link TaskInitializationData} instead
 */
@Deprecated
public class DefaultTaskMapping extends TaskMapping {

	@Deprecated
	public Map<String, String> values = new HashMap<>();

	@Deprecated
	@Override
	public String getDescription() {
		return values.get(TaskAttribute.DESCRIPTION);
	}

	@Deprecated
	@Override
	public String getSummary() {
		return values.get(TaskAttribute.SUMMARY);
	}

	@Deprecated
	public void setDescription(String value) {
		values.put(TaskAttribute.DESCRIPTION, value);
	}

	@Deprecated
	public void setSummary(String value) {
		values.put(TaskAttribute.SUMMARY, value);
	}

}
