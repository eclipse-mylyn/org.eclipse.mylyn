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
 *******************************************************************************/

package org.eclipse.mylyn.gitlab.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

public class GitlabTaskAttributeMapper extends TaskAttributeMapper {

    private final GitlabRepositoryConnector connector;

    private static final String dateFormat_1 = "yyyy-MM-dd HH:mm:ss.sss"; //$NON-NLS-1$

    private static final String dateFormat_2 = "yyyy-MM-dd HH:mm:ss"; //$NON-NLS-1$

    private static final String dateFormat_3 = "yyyy-MM-dd HH:mm"; //$NON-NLS-1$

    private static final String dateFormat_4 = "yyyy-MM-dd"; //$NON-NLS-1$

    private static final String dateFormat_1_TimeZone = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"; //$NON-NLS-1$

    private static final String dateFormat_2_TimeZone = "yyyy-MM-dd'T'HH:mm:ss'Z'"; //$NON-NLS-1$

    private static final String dateFormat_3_TimeZone = "yyyy-MM-dd'T'HH:mm'Z'"; //$NON-NLS-1$

    private static final String dateFormat_4_TimeZone = "yyyy-MM-dd'Z'"; //$NON-NLS-1$

    // Order is significant
    private static final String[] dateFormats = { dateFormat_1_TimeZone, dateFormat_1, dateFormat_2_TimeZone,
	    dateFormat_2, dateFormat_3_TimeZone, dateFormat_3, dateFormat_4_TimeZone, dateFormat_4 };

    public GitlabTaskAttributeMapper(TaskRepository taskRepository, GitlabRepositoryConnector connector) {
	super(taskRepository);
	this.connector = connector;
    }

    @Override
    public Date getDateValue(TaskAttribute attribute) {
	if (attribute == null) {
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
	for (String format : dateFormats) {
	    try {
		SimpleDateFormat simpleFormatter = new SimpleDateFormat(format);
		return simpleFormatter.parse(dateString);
	    } catch (ParseException e) {
	    } catch (NumberFormatException e) {
	    }
	}
	return null;
    }

    @Override
    public void setRepositoryPerson(@NonNull TaskAttribute taskAttribute, @NonNull IRepositoryPerson person) {
	super.setRepositoryPerson(taskAttribute, person);
	if (person.getAttributes().size()>0) {
	    for ( Entry<String, String> entry : person.getAttributes().entrySet()) {
		TaskAttribute taskAttrib = taskAttribute.getAttribute(entry.getKey());
		if (taskAttrib==null)
		    taskAttrib = taskAttribute.createAttribute(entry.getKey());
		taskAttrib.setValue(entry.getValue());
	    }
	}
    }
}
