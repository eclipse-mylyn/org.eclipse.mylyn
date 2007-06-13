/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.connector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.IAttachmentHandler;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class MockAttachmentHandler implements IAttachmentHandler {

	private byte[] data;

	public boolean canDeprecate(TaskRepository repository, RepositoryAttachment attachment) {
		// ignore
		return false;
	}

	public boolean canDownloadAttachment(TaskRepository repository, AbstractTask task) {
		// ignore
		return true;
	}

	public boolean canUploadAttachment(TaskRepository repository, AbstractTask task) {
		// ignore
		return true;
	}

	public void downloadAttachment(TaskRepository repository, RepositoryAttachment attachment, OutputStream target,
			IProgressMonitor monitor) throws CoreException {
		try {
			target.write(data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public InputStream getAttachmentAsStream(TaskRepository repository, RepositoryAttachment attachment,
			IProgressMonitor monitor) throws CoreException {
		// ignore
		return new ByteArrayInputStream(data);
	}

	public void updateAttachment(TaskRepository repository, RepositoryAttachment attachment) throws CoreException {
		// ignore
	}

	public void uploadAttachment(TaskRepository repository, AbstractTask task, ITaskAttachment attachment,
			String comment, IProgressMonitor monitor) throws CoreException {
		// ignore
	}

	public void setAttachmentData(byte[] data) {
		this.data = data;
	}
	
}
