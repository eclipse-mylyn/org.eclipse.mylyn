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

package org.eclipse.mylyn.tasks.core.data;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.eclipse.core.runtime.CoreException;

/**
 * @since 3.1
 * @author Steffen Pingel
 */
public class TaskAttachmentPartSource implements PartSource {

	private final AbstractTaskAttachmentSource attachment;

	private final String filename;

	public TaskAttachmentPartSource(AbstractTaskAttachmentSource attachment, String filename) {
		this.attachment = attachment;
		this.filename = filename;
	}

	public InputStream createInputStream() throws IOException {
		try {
			return attachment.createInputStream(null);
		} catch (CoreException e) {
			IOException exception = new IOException("Failed to create source stream"); //$NON-NLS-1$
			exception.initCause(e);
			throw exception;
		}
	}

	public String getFileName() {
		return filename;
	}

	public long getLength() {
		return attachment.getLength();
	}

}
