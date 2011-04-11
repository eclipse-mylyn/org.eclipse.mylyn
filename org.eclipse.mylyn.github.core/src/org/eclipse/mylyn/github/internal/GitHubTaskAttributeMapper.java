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
package org.eclipse.mylyn.github.internal;

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
	public List<String> getValueLabels(TaskAttribute taskAttribute) {
		if (TaskAttribute.TYPE_DATE.equals(taskAttribute.getMetaData()
				.getType())) {
			String date = taskAttribute.getValue();
			if (date.length() > 0) {
				synchronized (this.format) {
					return Collections.singletonList(this.format
							.format(new Date(Long.parseLong(date))));
				}
			}
		}
		return super.getValueLabels(taskAttribute);
	}
}
