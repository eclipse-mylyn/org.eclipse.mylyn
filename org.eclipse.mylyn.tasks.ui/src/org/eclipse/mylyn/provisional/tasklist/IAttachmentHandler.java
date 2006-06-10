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

package org.eclipse.mylar.provisional.tasklist;

import java.io.File;
import java.net.Proxy;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public interface IAttachmentHandler {

	public void uploadAttachment(TaskRepository repository, AbstractRepositoryTask task, String comment,
			String description, File file, String contentType, boolean isPatch, Proxy proxySettings) throws CoreException;

	public void downloadAttachment(TaskRepository taskRepository, AbstractRepositoryTask task,
			int attachmentId, File file, Proxy proxySettings) throws CoreException;

}
