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

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.commons.ui.CommonsUiPlugin;
import org.eclipse.ui.PlatformUI;

/**
 * Retrieves a title for a web page.
 * 
 * @author Wesley Coelho
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public abstract class AbstractRetrieveTitleFromUrlJob extends Job {

	public static final String LABEL_TITLE = "Retrieving summary from URL";

	private volatile String pageTitle;

	private final String url;

	public AbstractRetrieveTitleFromUrlJob(String url) {
		super(LABEL_TITLE);
		this.url = url;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			pageTitle = WebUtil.getTitleFromUrl(new WebLocation(getUrl()), monitor);
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					titleRetrieved(pageTitle);
				}
			});
		} catch (IOException e) {
			return new Status(IStatus.ERROR, CommonsUiPlugin.ID_PLUGIN, "Retrieving summary from URL failed", e);
		}
		return Status.OK_STATUS;
	}

	protected void titleRetrieved(String pageTitle) {
	}

}
