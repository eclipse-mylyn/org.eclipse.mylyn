/*******************************************************************************
 * Copyright (c) 2010, 2013 Peter Stibrany and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
