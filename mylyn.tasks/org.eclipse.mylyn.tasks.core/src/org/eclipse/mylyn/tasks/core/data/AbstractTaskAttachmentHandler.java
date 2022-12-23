/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core.data;

import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Subclass to provide facility for uploading and downloading files from task repositories.
 *
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class AbstractTaskAttachmentHandler {

	public abstract boolean canGetContent(@NonNull TaskRepository repository, @NonNull ITask task);

	public abstract boolean canPostContent(@NonNull TaskRepository repository, @NonNull ITask task);

	public abstract InputStream getContent(@NonNull TaskRepository repository, @NonNull ITask task,
			@NonNull TaskAttribute attachmentAttribute, @Nullable IProgressMonitor monitor) throws CoreException;

	public abstract void postContent(@NonNull TaskRepository repository, @NonNull ITask task,
			@NonNull AbstractTaskAttachmentSource source, @Nullable String comment,
			@Nullable TaskAttribute attachmentAttribute, @Nullable IProgressMonitor monitor) throws CoreException;

}
