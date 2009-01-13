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

package org.eclipse.mylyn.internal.tasks.core.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

	public static final String APPLICATION_OCTET_STREAM = "application/octet-stream"; //$NON-NLS-1$

	private static Map<String, String> extensions2Types;

	static {
		extensions2Types = new HashMap<String, String>();
		extensions2Types.put("txt", "text/plain"); //$NON-NLS-1$ //$NON-NLS-2$
		extensions2Types.put("html", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		extensions2Types.put("htm", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		extensions2Types.put("jpg", "image/jpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		extensions2Types.put("jpeg", "image/jpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		extensions2Types.put("gif", "image/gif"); //$NON-NLS-1$ //$NON-NLS-2$
		extensions2Types.put("png", "image/png"); //$NON-NLS-1$ //$NON-NLS-2$
		extensions2Types.put("xml", "application/xml"); //$NON-NLS-1$ //$NON-NLS-2$
		extensions2Types.put("zip", APPLICATION_OCTET_STREAM); //$NON-NLS-1$
		extensions2Types.put("tar", APPLICATION_OCTET_STREAM); //$NON-NLS-1$
		extensions2Types.put("gz", APPLICATION_OCTET_STREAM); //$NON-NLS-1$
	}

	public static String getContentTypeFromFilename(String fileName) {
		int index = fileName.lastIndexOf("."); //$NON-NLS-1$
		if (index > 0 && index < fileName.length()) {
			String ext = fileName.substring(index + 1);
			String type = extensions2Types.get(ext.toLowerCase(Locale.ENGLISH));
			if (type != null) {
				return type;
			}
		}
		return APPLICATION_OCTET_STREAM;
	}

	private String contentType;

	private String description;

	private final File file;

	private String name;

	public FileTaskAttachmentSource(File file) {
		this.file = file;
		this.name = file.getName();
		this.contentType = getContentTypeFromFilename(name);
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