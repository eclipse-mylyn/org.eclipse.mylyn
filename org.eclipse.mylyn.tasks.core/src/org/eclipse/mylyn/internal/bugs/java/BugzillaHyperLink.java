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
package org.eclipse.mylar.internal.bugs.java;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskRepository;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Mik Kersten
 */
public class BugzillaHyperLink implements IHyperlink {

	private IRegion region;

	private int id;

	public BugzillaHyperLink(IRegion nlsKeyRegion, int id) {
		this.region = nlsKeyRegion;
		this.id = id;
	}

	public IRegion getHyperlinkRegion() {
		return region;
	}

	public String getTypeLabel() {
		return null;
	}

	public String getHyperlinkText() {
		return null;
	}

	public void open() {
		// TaskRepository repository =
		// MylarTaskListPlugin.getRepositoryManager().getRepositoryForActiveTask(BugzillaPlugin.REPOSITORY_KIND);
		TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getDefaultRepository(
				BugzillaPlugin.REPOSITORY_KIND);
		if (repository != null) {
			OpenBugzillaReportJob job = new OpenBugzillaReportJob(repository.getUrl().toExternalForm(), id);
			IProgressService service = PlatformUI.getWorkbench().getProgressService();
			try {
				service.run(true, false, job);
			} catch (Exception e) {
				MylarStatusHandler.fail(e, "Could not open report", true);
			}
		} else {
			MessageDialog.openError(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG,
					"Could not determine repository for report");
		}
	}
}
