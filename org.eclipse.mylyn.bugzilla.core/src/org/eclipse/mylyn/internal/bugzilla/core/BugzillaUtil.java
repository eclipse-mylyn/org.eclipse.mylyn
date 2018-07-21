/*******************************************************************************
 * Copyright (c) 2010, 2013 Frank Becker and others.
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

package org.eclipse.mylyn.internal.bugzilla.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.osgi.util.NLS;

public class BugzillaUtil {

	private static boolean getParamValue(TaskRepository taskRepository, String propertyName, boolean trueIfUndefined) {
		boolean result;
		String useParam = taskRepository.getProperty(propertyName);
		result = trueIfUndefined ? (useParam == null || (useParam != null && useParam.equals("true"))) //$NON-NLS-1$
				: (useParam != null && useParam.equals("true")); //$NON-NLS-1$
		return result;
	}

	public static void addAttributeIfUsed(BugzillaAttribute constant, String propertyName,
			TaskRepository taskRepository, TaskData existingReport, boolean createWhenNull) {
		if (getParamValue(taskRepository, propertyName, createWhenNull)) {
			BugzillaTaskDataHandler.createAttribute(existingReport, constant);
		}
	}

	public static void createAttributeWithKindDefaultIfUsed(String parsedText, BugzillaAttribute tag,
			TaskData repositoryTaskData, String propertyName, boolean defaultWhenNull) {

		TaskAttribute attribute = repositoryTaskData.getRoot().getMappedAttribute(tag.getKey());
		if (attribute == null) {
			attribute = BugzillaTaskDataHandler.createAttribute(repositoryTaskData, tag);
			attribute.setValue(parsedText);
		} else {
			attribute.addValue(parsedText);
		}
		if (BugzillaAttribute.QA_CONTACT.equals(tag)) {
			attribute.getMetaData().setKind(null);
		} else {
			if (getParamValue(repositoryTaskData.getAttributeMapper().getTaskRepository(), propertyName,
					defaultWhenNull)) {
				attribute.getMetaData().setKind(TaskAttribute.KIND_DEFAULT);
			} else {
				attribute.getMetaData().setKind(null);
			}
		}
	}

	/**
	 * Call this method if you did not know if an property exists
	 * 
	 * @param taskRepository
	 * @param property
	 * @return true if the property is undefined or if the property is true
	 */
	public static boolean getTaskPropertyWithDefaultTrue(TaskRepository taskRepository, String property) {
		String useParam = taskRepository.getProperty(property);
		return (useParam == null || (useParam != null && useParam.equals("true"))); //$NON-NLS-1$
	}

	private static final Pattern TIME_STAMP_PATTERN = Pattern.compile(
			"[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

	public static String removeTimezone(String timeWithTimezone) throws CoreException {
		Matcher matcher = TIME_STAMP_PATTERN.matcher(timeWithTimezone);
		if (matcher.find()) {
			return matcher.group();
		}
		throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN, NLS.bind(
				"{0} is not a valid time", timeWithTimezone))); //$NON-NLS-1$
	}
}
