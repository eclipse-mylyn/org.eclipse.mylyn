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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

public class GitHubTaskAttributeMapper extends TaskAttributeMapper {

	private DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
	
	public GitHubTaskAttributeMapper(TaskRepository taskRepository) {
		super(taskRepository);
	}

	@Override
	public String mapToRepositoryKey(TaskAttribute parent, String key) {
		return key;
	}

	@Override
	public Date getDateValue(TaskAttribute attribute) {
		String value = attribute.getValue();
		if (value != null) {
			try {
				return dateFormat.parse(value);
			} catch (ParseException e) {
				return super.getDateValue(attribute);
			}
		}
		return null;
	}
}
