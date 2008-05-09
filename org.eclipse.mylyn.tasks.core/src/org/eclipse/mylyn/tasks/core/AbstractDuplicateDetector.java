/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * Extend to provide task duplicate detection facilities to the task editor (e.g. Java stack trace matching).
 * 
 * @author Gail Murphy
 * @author Robert Elves
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class AbstractDuplicateDetector {

	protected String name;

	protected String connectorKind;

	public abstract IRepositoryQuery getDuplicatesQuery(TaskRepository repository, TaskData taskData, String text);

	public void setName(String name) {
		this.name = name;
	}

	public void setConnectorKind(String kind) {
		this.connectorKind = kind;
	}

	public String getName() {
		return this.name;
	}

	public String getConnectorKind() {
		return this.connectorKind;
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
