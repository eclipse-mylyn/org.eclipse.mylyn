/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;

/**
 * @author Steffen Pingel
 * @author David Green fix for 267960 guess the mime type of attachments
 */
public class FileTaskAttachmentSource extends AbstractTaskAttachmentSource {
	/**
	 * mime type for text/plain
	 */
	private static final String TEXT_PLAIN = "text/plain"; //$NON-NLS-1$

	/**
	 * mime type for application/xml
	 */
	private static final String APPLICATION_XML = "application/xml"; //$NON-NLS-1$

	public static final String APPLICATION_OCTET_STREAM = "application/octet-stream"; //$NON-NLS-1$

	private static Map<String, String> extensions2Types;

	static {
		// see http://www.iana.org/assignments/media-types/
		extensions2Types = new HashMap<String, String>();
		extensions2Types.put("txt", TEXT_PLAIN); //$NON-NLS-1$ 
		extensions2Types.put("html", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		extensions2Types.put("htm", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		extensions2Types.put("xhtml", "application/xhtml+xml"); //$NON-NLS-1$//$NON-NLS-2$
		extensions2Types.put("jpg", "image/jpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		extensions2Types.put("jpeg", "image/jpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		extensions2Types.put("gif", "image/gif"); //$NON-NLS-1$ //$NON-NLS-2$
		extensions2Types.put("png", "image/png"); //$NON-NLS-1$ //$NON-NLS-2$
		extensions2Types.put("xml", APPLICATION_XML); //$NON-NLS-1$
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
			// bug 267960 attempt to detect the mime type from the content type
			IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
			// platform may not be available when running standalone
			if (contentTypeManager != null) {
				IContentType contentType = contentTypeManager.findContentTypeFor(fileName);
				while (contentType != null) {
					if (IContentTypeManager.CT_TEXT.equals(contentType.getId())) {
						return TEXT_PLAIN;
					} else if ("org.eclipse.core.runtime.xml".equals(contentType.getId())) { //$NON-NLS-1$
						return APPLICATION_XML;
					}
					contentType = contentType.getBaseType();
				}
			}
		}

		// fall back to a safe mime type
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