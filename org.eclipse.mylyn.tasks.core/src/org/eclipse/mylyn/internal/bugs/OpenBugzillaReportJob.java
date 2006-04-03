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

package org.eclipse.mylar.internal.bugs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaOpenStructure;
import org.eclipse.mylar.internal.bugzilla.ui.ViewBugzillaAction;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;

/**
 * @author Mik Kersten
 */
public class OpenBugzillaReportJob implements IRunnableWithProgress {

	private int id;

	private String serverUrl;

	public OpenBugzillaReportJob(String serverUrl, int id) {
		this.id = id;
		this.serverUrl = serverUrl;
	}

	public void run(IProgressMonitor monitor) {
		try {
			monitor.beginTask("Opening Bugzilla Report", 10);
			List<BugzillaOpenStructure> list = new ArrayList<BugzillaOpenStructure>(1);
			list.add(new BugzillaOpenStructure(serverUrl, id, -1));
			new ViewBugzillaAction("Open Bug " + id, list).run(monitor);
			monitor.done();
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Unable to open Bug report: " + id, true);
		}
	}
}
