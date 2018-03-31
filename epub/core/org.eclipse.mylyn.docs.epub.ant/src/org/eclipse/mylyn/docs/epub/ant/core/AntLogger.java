/*******************************************************************************
 * Copyright (c) 2012 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.ant.core;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.eclipse.mylyn.docs.epub.core.ILogger;

/**
 * @author Torkild U. Resheim
 */
public class AntLogger implements ILogger {

	Task task;

	public AntLogger(Task task) {
		this.task = task;
	}

	public void log(String message) {
		task.log(message);
	}

	public void log(String message, Severity severity) {
		switch (severity) {
		case ERROR:
			task.log(message, Project.MSG_ERR);
			break;
		case WARNING:
			task.log(message, Project.MSG_WARN);
			break;
		case INFO:
			task.log(message, Project.MSG_INFO);
			break;
		case VERBOSE:
			task.log(message, Project.MSG_VERBOSE);
			break;
		case DEBUG:
			task.log(message, Project.MSG_DEBUG);
			break;
		default:
			break;
		}
	}

}
