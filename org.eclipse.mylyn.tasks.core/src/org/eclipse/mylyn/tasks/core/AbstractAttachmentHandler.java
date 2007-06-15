/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractAttachmentHandler implements IAttachmentHandler {

	protected static final int BUFFER_SIZE = 1024;

	public void downloadAttachment(TaskRepository repository, RepositoryAttachment attachment, OutputStream out,
			IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Downloading attachment", IProgressMonitor.UNKNOWN);
		try {
			InputStream in = new BufferedInputStream(getAttachmentAsStream(repository, attachment, new SubProgressMonitor(monitor, 1)));
			try {
				byte[] buffer = new byte[BUFFER_SIZE]; 
				while (true) {
					int count = in.read(buffer);
					if (count == -1) {
						return;
					}
					if (monitor.isCanceled()) { 
						throw new OperationCanceledException();
					}
					out.write(buffer, 0, count);
					if (monitor.isCanceled()) { 
						throw new OperationCanceledException();
					}
				}
			} catch (IOException e) {
				throw new CoreException(RepositoryStatus.createStatus(repository, IStatus.ERROR, "org.eclipse.mylyn.tasks.core", "IO error reading attachment: " + e.getMessage()));
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					StatusManager.fail(e, "Error closing attachment stream", false);
				}
			}
		} finally {
			monitor.done();
		}
	}

}
