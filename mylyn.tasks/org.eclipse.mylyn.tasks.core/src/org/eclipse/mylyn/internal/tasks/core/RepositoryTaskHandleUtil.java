/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

/**
 * @author Mik Kersten
 */
public class RepositoryTaskHandleUtil {

	public static final String HANDLE_DELIM = "-"; //$NON-NLS-1$

	private static final String MISSING_REPOSITORY = "norepository"; //$NON-NLS-1$

	public static String getHandle(String repositoryUrl, String taskId) {
		if (!isValidTaskId(taskId)) {
			throw new RuntimeException(
					"invalid handle for task, can not contain: " + HANDLE_DELIM + ", was: " + taskId); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (repositoryUrl == null) {
			return MISSING_REPOSITORY + HANDLE_DELIM + taskId;
		} else {
			return (repositoryUrl + HANDLE_DELIM + taskId).intern();
		}
	}

	public static String getRepositoryUrl(String taskHandle) {
		int index = taskHandle.lastIndexOf(RepositoryTaskHandleUtil.HANDLE_DELIM);
		String url = null;
		if (index != -1) {
			url = taskHandle.substring(0, index);
		}
		return url;
	}

	public static String getTaskId(String taskHandle) {
		int index = taskHandle.lastIndexOf(HANDLE_DELIM);
		if (index != -1) {
			String id = taskHandle.substring(index + 1);
			return id;
		}
		return null;
	}

	public static boolean isValidTaskId(String taskId) {
		return !taskId.contains(HANDLE_DELIM);
	}

}
