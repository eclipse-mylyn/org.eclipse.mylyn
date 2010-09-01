/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

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
}
