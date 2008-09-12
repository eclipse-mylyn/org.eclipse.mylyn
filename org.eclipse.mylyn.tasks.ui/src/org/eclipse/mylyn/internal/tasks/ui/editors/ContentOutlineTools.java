/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
public class ContentOutlineTools {

	/** The default string used for locally created bugs. */
	public static final String OFFLINE_SERVER_DEFAULT = "[local]";

	/**
	 * Returns a unique handle for the bugzilla selection. Contains the bug taskId, the bug server, and (if applicable)
	 * the comment number.
	 * 
	 * @param taskSelection
	 *            The bugzilla selection.
	 * @return The handle for the bugzilla selection.
	 */
	public static String getHandle(IRepositoryTaskSelection taskSelection) {
		String handle = taskSelection.getRepositoryUrl() + ";" + taskSelection.getId();
		if (taskSelection.hasComment()) {
			int number = taskSelection.getComment().getNumber() + 1;
			handle += ";" + number;
		} else if (taskSelection.isCommentHeader()) {
			handle += ";1";
		} else if (taskSelection.isDescription()) {
			handle += ";0";
		}
		return handle;
	}

	public static String getName(IRepositoryTaskSelection taskSelection) {
		String name = taskSelection.getRepositoryUrl() + ": Bug#: " + taskSelection.getId() + ": "
				+ taskSelection.getBugSummary();
		if (taskSelection.hasComment()) {
			name += " : Comment#: " + taskSelection.getComment().getNumber();
		} else if (taskSelection.isCommentHeader()) {
			name += " : Comment Header";
		} else if (taskSelection.isDescription()) {
			name += ": Description";
		}
		return name;
	}

//	public static String getHandle(RepositoryTaskData taskData) {
//		return getHandle(taskData.getRepositoryUrl(), taskData.getId());
//	}
//
//	public static String getHandle(String server, String taskId) {
//		return server + ";" + taskId;
//	}

//	public static String getName(RepositoryTaskData taskData) {
//		return taskData.getRepositoryUrl() + ": Bug#: " + taskData.getId() + ": " + taskData.getSummary();
//	}

}
