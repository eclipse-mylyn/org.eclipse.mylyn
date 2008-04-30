/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Extend to provide facility for uploading and downloading files from task repositories.
 * 
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class AbstractTaskAttachmentHandler {

	public abstract void postContent(TaskRepository repository, AbstractTask task, AbstractTaskAttachmentSource source,
			String comment, TaskAttribute attachmentAttribute, IProgressMonitor monitor) throws CoreException;

	public abstract InputStream getContent(TaskRepository repository, AbstractTask task,
			TaskAttribute attachmentAttribute, IProgressMonitor monitor) throws CoreException;

	public abstract boolean canPostContent(TaskRepository repository, AbstractTask task);

	public abstract boolean canGetContent(TaskRepository repository, AbstractTask task);

}
