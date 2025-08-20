/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.tasks.ui.Messages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.PlatformUI;

/**
 * Retrieves a title for a web page.
 *
 * @author Wesley Coelho
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public abstract class AbstractRetrieveTitleFromUrlJob extends Job {

	private volatile String pageTitle;

	private final String url;

	public AbstractRetrieveTitleFromUrlJob(String url) {
		super(Messages.AbstractRetrieveTitleFromUrlJob_Retrieving_summary_from_URL);
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
			if (pageTitle != null) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(() -> titleRetrieved(pageTitle));
			}
		} catch (IOException e) {
			return new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Retrieving summary from URL failed", e); //$NON-NLS-1$
		}
		return Status.OK_STATUS;
	}

	protected void titleRetrieved(String pageTitle) {
	}

}
