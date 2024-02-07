/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.gitlab.core;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

public class GitlabTaskAttributeMapper extends TaskAttributeMapper {

	private static DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd[' ']['T'][H:mm[:ss[.SSS]['Z']]]"); //$NON-NLS-1$

	public GitlabTaskAttributeMapper(TaskRepository taskRepository) {
		super(taskRepository);
	}

	@Override
	public Date getDateValue(TaskAttribute attribute) {
		if (attribute == null || attribute.getValue().isBlank()) {
			return null;
		}
		Date parsedDate = parseDate(attribute.getValue());
		if (parsedDate != null) {
			return parsedDate;
		}
		return super.getDateValue(attribute);
	}

	/**
	 * Note: Date formatter constructed within method for thread safety
	 */
	public static final Date parseDate(String dateString) {
		if (dateString.matches("[0-9]+")) { //$NON-NLS-1$
			return new Date(Long.parseLong(dateString));
		} else {
			return Date.from(LocalDateTime.parse(dateString, formatter).atZone(ZoneId.systemDefault()).toInstant());
		}
	}

	@Override
	public void setRepositoryPerson(@NonNull TaskAttribute taskAttribute, @NonNull IRepositoryPerson person) {
		super.setRepositoryPerson(taskAttribute, person);
		if (person.getAttributes().size() > 0) {
			for (Entry<String, String> entry : person.getAttributes().entrySet()) {
				TaskAttribute taskAttrib = taskAttribute.getAttribute(entry.getKey());
				if (taskAttrib == null) {
					taskAttrib = taskAttribute.createAttribute(entry.getKey());
				}
				taskAttrib.setValue(entry.getValue());
			}
		}
	}
}
