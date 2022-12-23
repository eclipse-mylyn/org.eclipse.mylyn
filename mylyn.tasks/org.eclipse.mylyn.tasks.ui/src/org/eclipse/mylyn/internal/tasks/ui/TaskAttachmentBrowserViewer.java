/*******************************************************************************
 * Copyright (c) 2010, 2013 Peter Stibrany and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Peter Stibrany - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Peter Stibrany
 */
public class TaskAttachmentBrowserViewer implements ITaskAttachmentViewer {

	public String getId() {
		return "inBrowserViewer"; //$NON-NLS-1$
	}

	public String getLabel() {
		return Messages.TaskAttachmentViewerBrowser_browser;
	}

	public void openAttachment(IWorkbenchPage page, ITaskAttachment attachment) {
		TasksUiUtil.openUrl(attachment.getUrl());
	}

	public boolean isWorkbenchDefault() {
		return false;
	}

}
