/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugs.java;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

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
		OpenBugzillaReportJob job = new OpenBugzillaReportJob(id);
		IProgressService service = PlatformUI.getWorkbench().getProgressService();
		try {
			service.run(true, false, job);
		} catch (Exception e) {
			MylarPlugin.fail(e, "Could not open report", true);
		}
	}
}
