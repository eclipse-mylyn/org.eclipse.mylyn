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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.ui.IWorkbenchPage;

/**
 * Simple interface for attachment 'viewers'. Most viewers are based on existing eclipse editors.
 * 
 * @author Peter Stibrany
 */
public interface ITaskAttachmentViewer {
	/**
	 * @return arbitrary string, used to remember preferred viewer
	 */
	public String getId();

	/**
	 * @return name of the editor, displayed to user
	 */
	public String getLabel();

	public void openAttachment(IWorkbenchPage page, ITaskAttachment attachment) throws CoreException;

	public boolean isWorkbenchDefault();
}