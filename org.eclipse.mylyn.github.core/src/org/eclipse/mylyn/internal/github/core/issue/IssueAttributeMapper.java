/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.core.issue;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

/**
 * GitHub task attribute mapper class.
 */
public class IssueAttributeMapper extends TaskAttributeMapper {

	private DateFormat format = DateFormat.getDateTimeInstance(
			DateFormat.MEDIUM, DateFormat.SHORT);

	/**
	 * @param taskRepository
	 */
	public IssueAttributeMapper(TaskRepository taskRepository) {
		super(taskRepository);
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper#getValueLabels(org.eclipse.mylyn.tasks.core.data.TaskAttribute)
	 */
	public List<String> getValueLabels(TaskAttribute taskAttribute) {
		String type = taskAttribute.getMetaData().getType();
		if (TaskAttribute.TYPE_DATE.equals(type)
				|| TaskAttribute.TYPE_DATETIME.equals(type)) {
			String value = taskAttribute.getValue();
			if (value.length() > 0) {
				Date date = new Date(Long.parseLong(value));
				synchronized (this.format) {
					value = this.format.format(date);
				}
				return Collections.singletonList(value);
			}
		}
		return super.getValueLabels(taskAttribute);
	}
}
