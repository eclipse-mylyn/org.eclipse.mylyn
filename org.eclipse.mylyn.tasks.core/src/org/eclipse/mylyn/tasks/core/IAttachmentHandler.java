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

package org.eclipse.mylar.tasks.core;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public interface IAttachmentHandler {

	public void uploadAttachment(TaskRepository repository, AbstractRepositoryTask task, String comment,
			String description, File file, String contentType, boolean isPatch, IProgressMonitor monitor) throws CoreException;

	public void downloadAttachment(TaskRepository taskRepository, RepositoryAttachment attachment, File file, IProgressMonitor monitor) throws CoreException;

	public byte[] getAttachmentData(TaskRepository repository, RepositoryAttachment attachment) throws CoreException;
	
	//public InputStream getAttachmentInputStream(TaskRepository repository, String taskId) throws CoreException;
	
	public boolean canUploadAttachment(TaskRepository repository, AbstractRepositoryTask task);
	
	public boolean canDownloadAttachment(TaskRepository repository, AbstractRepositoryTask task);

	public boolean canDeprecate(TaskRepository repository, RepositoryAttachment attachment);

	/** To deprecate, change the attribute on the RepositoryAttachment and pass to this method */	
	public void updateAttachment(TaskRepository repository, RepositoryAttachment attachment) throws CoreException;

}
