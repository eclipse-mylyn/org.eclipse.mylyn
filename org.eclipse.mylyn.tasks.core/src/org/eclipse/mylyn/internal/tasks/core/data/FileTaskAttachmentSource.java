/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;

/**
 * @author Steffen Pingel
 */
public class FileTaskAttachmentSource extends AbstractTaskAttachmentSource {

	private static final String CONTENT_TYPE_BINARY = "application/octet-stream";

	private String contentType = CONTENT_TYPE_BINARY;

	private String description;

	private final File file;

	private String name;

	public FileTaskAttachmentSource(File file) {
		this.file = file;
		this.name = file.getName();
	}

	@Override
	public InputStream createInputStream(IProgressMonitor monitor) throws CoreException {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, e.getMessage(), e));
		}
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public long getLength() {
		return file.length();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

}