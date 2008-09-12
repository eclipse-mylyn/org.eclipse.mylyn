/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.eclipse.mylyn.internal.tasks.core.deprecated.ITaskAttachment;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;

/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
public class LocalAttachment implements Serializable, ITaskAttachment {

	private static final long serialVersionUID = -4477699536552617389L;

	/** The report to which this attachment will be attached */
	private RepositoryTaskData repositoryTaskData;

	private String filePath;

	private String comment = "";

	private String description = "";

	private String contentType = "";

	private boolean isPatch = false;

	private String filename;

	private byte[] content;

	private File file;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPatch() {
		return isPatch;
	}

	public void setPatch(boolean isPatch) {
		this.isPatch = isPatch;
	}

	public RepositoryTaskData getReport() {
		return repositoryTaskData;
	}

	public void setReport(RepositoryTaskData report) {
		this.repositoryTaskData = report;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public InputStream createInputStream() throws IOException {
		assert file != null || content != null;
		return (file != null) ? new FileInputStream(file) : new ByteArrayInputStream(content);
	}

	public long getLength() {
		assert file != null || content != null;
		return (file != null) ? file.length() : content.length;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

}
