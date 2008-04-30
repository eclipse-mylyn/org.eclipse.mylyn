/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import java.util.Date;

/**
 * @since 3.0
 */
public interface ITaskAttachment2 {

	public abstract String getAttachmentId();

	public abstract RepositoryPerson getAuthor();

	public abstract String getComment();

	public abstract String getConnectorKind();

	public abstract String getContentType();

	public abstract Date getCreationDate();

	public abstract String getDescription();

	public abstract String getFileName();

	public abstract long getLength();

	public abstract String getRepositoryUrl();

	public abstract String getTaskId();

	public abstract String getUrl();

	public abstract boolean isDeprecated();

	public abstract boolean isPatch();

}