/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.tasklist.ui.editors;

import org.eclipse.mylar.internal.tasklist.RepositoryTaskData;

public class OutlineTools {

	/** The default string used for locally created bugs. */
	public static final String OFFLINE_SERVER_DEFAULT = "[local]";

	/**
	 * Returns a unique handle for the bugzilla selection. Contains the bug id,
	 * the bug server, and (if applicable) the comment number.
	 * 
	 * @param bugSel
	 *            The bugzilla selection.
	 * @return The handle for the bugzilla selection.
	 */
	public static String getHandle(IRepositoryTaskSelection bugSel) {
		String handle = bugSel.getServer() + ";" + bugSel.getId();
		if (bugSel.hasComment()) {
			int number = bugSel.getComment().getNumber() + 1;
			handle += ";" + number;
		} else if (bugSel.isCommentHeader()) {
			handle += ";1";
		} else if (bugSel.isDescription()) {
			handle += ";0";
		}
		return handle;
	}

	public static String getName(IRepositoryTaskSelection bugSel) {
		String name = bugSel.getServer() + ": Bug#: " + bugSel.getId() + ": " + bugSel.getBugSummary();
		if (bugSel.hasComment()) {
			name += " : Comment#: " + bugSel.getComment().getNumber();
		} else if (bugSel.isCommentHeader()) {
			name += " : Comment Header";
		} else if (bugSel.isDescription()) {
			name += ": Description";
		}
		return name;
	}

	public static String getHandle(RepositoryTaskData bug) {
		return getHandle(bug.getRepositoryUrl(), bug.getId());
	}

	public static String getHandle(String server, int id) {
		return server + ";" + id;
	}

	public static String getName(RepositoryTaskData bug) {
		return bug.getRepositoryUrl() + ": Bug#: " + bug.getId() + ": " + bug.getSummary();
	}

}
