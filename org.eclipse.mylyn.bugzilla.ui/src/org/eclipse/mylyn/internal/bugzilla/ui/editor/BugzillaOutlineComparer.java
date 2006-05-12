/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.bugzilla.ui.editor;

import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaTools;
import org.eclipse.mylar.internal.bugzilla.ui.IBugzillaReportSelection;

/**
 * This class is used to compare two <code>IBugzillaReportSelection</code>
 * objects.
 * 
 * @see IElementComparer
 * @see IBugzillaReportSelection
 */
public class BugzillaOutlineComparer implements IElementComparer {

	public boolean equals(Object a, Object b) {
		if ((a instanceof IBugzillaReportSelection) && (b instanceof IBugzillaReportSelection)) {
			IBugzillaReportSelection s1 = (IBugzillaReportSelection) a;
			IBugzillaReportSelection s2 = (IBugzillaReportSelection) b;

			// An IBugzillaReportSelection is uniquely defined by its handle and
			// its contents
			return ((BugzillaTools.getHandle(s1).equals(BugzillaTools.getHandle(s2))) && ((s1.getContents() == null) ? (s2
					.getContents() == null)
					: s1.getContents().equals(s2.getContents())));
		}
		return a.equals(b);
	}

	public int hashCode(Object element) {
		if (element instanceof IBugzillaReportSelection) {
			IBugzillaReportSelection sel = (IBugzillaReportSelection) element;

			// An IBugzillaReportSelection is uniquely defined by its handle and
			// its contents
			return (BugzillaTools.getHandle(sel) + sel.getContents()).hashCode();
		}
		return element.hashCode();
	}
}
