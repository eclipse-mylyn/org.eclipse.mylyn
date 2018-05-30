/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.core;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

/**
 * Core task attribute mapper
 */
public class GitHubTaskAttributeMapper extends TaskAttributeMapper {

	private DateFormat format = DateFormat.getDateTimeInstance(
			DateFormat.MEDIUM, DateFormat.SHORT);

	/**
	 * @param taskRepository
	 */
	public GitHubTaskAttributeMapper(TaskRepository taskRepository) {
		super(taskRepository);
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper#getValueLabels(org.eclipse.mylyn.tasks.core.data.TaskAttribute)
	 */
	public List<String> getValueLabels(final TaskAttribute taskAttribute) {
		final String type = taskAttribute.getMetaData().getType();
		if (TaskAttribute.TYPE_DATE.equals(type)
				|| TaskAttribute.TYPE_DATETIME.equals(type)) {
			String value = taskAttribute.getValue();
			if (value.length() > 0) {
				final Date date = new Date(Long.parseLong(value));
				synchronized (format) {
					value = format.format(date);
				}
				return Collections.singletonList(value);
			}
		}
		return super.getValueLabels(taskAttribute);
	}

}
