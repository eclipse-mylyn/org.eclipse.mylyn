/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.AbstractSearchHandler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractDuplicateDetector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Gail Murphy
 * @author Steffen Pingel
 */
public class StackTraceDuplicateDetector extends AbstractDuplicateDetector {

	@Override
	public boolean canQuery(TaskData taskData) {
		return TasksUiPlugin.getDefault().getSearchHandler(taskData.getConnectorKind()) != null;
	}

	private String getDescription(TaskData taskData) {
		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(TaskAttribute.DESCRIPTION);
		if (attribute == null) {
			attribute = taskData.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW);
		}
		return (attribute != null) ? attribute.getTaskData().getAttributeMapper().getValueLabel(attribute) : "";
	}

	@Override
	public IRepositoryQuery getDuplicatesQuery(TaskRepository taskRepository, TaskData taskData) throws CoreException {
		String description = getDescription(taskData);
		String searchString = getStackTraceFromDescription(description);
		if (searchString == null) {
			throw new CoreException(new Status(IStatus.INFO, TasksUiPlugin.ID_PLUGIN,
					"Unable to locate a stack trace in the description text."));
		}

		IRepositoryQuery query = TasksUi.getTasksModel().createQuery(taskRepository);
		AbstractSearchHandler searchHandler = TasksUiPlugin.getDefault().getSearchHandler(
				taskRepository.getConnectorKind());
		if (searchHandler.queryForText(taskRepository, query, taskData, searchString)) {
			return query;
		}
		return null;
	}

	public static String getStackTraceFromDescription(String description) {
		String stackTrace = null;

		if (description == null) {
			return null;
		}

		String punct = "!\"#$%&'\\(\\)*+,-./:;\\<=\\>?@\\[\\]^_`\\{|\\}~\n";
		String lineRegex = " *at\\s+[\\w" + punct + "]+ ?\\(.*\\) *\n?";
		Pattern tracePattern = Pattern.compile(lineRegex);
		Matcher match = tracePattern.matcher(description);

		if (match.find()) {
			// record the index of the first stack trace line
			int start = match.start();
			int lastEnd = match.end();

			// find the last stack trace line
			while (match.find()) {
				lastEnd = match.end();
			}

			// make sure there's still room to find the exception
			if (start <= 0) {
				return null;
			}

			// count back to the line before the stack trace to find the
			// exception
			int stackStart = 0;
			int index = start - 1;
			while (index > 1 && description.charAt(index) == ' ') {
				index--;
			}

			// locate the exception line index
			stackStart = description.substring(0, index - 1).lastIndexOf("\n");
			stackStart = (stackStart == -1) ? 0 : stackStart + 1;

			stackTrace = description.substring(stackStart, lastEnd);
		}

		return stackTrace;
	}

}
