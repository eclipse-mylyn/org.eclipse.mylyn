/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
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

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Steffen Pingel
 * @since 3.4
 */
public class UnsubmittedTaskAttachment {

	private final TaskAttribute attribute;

	private String contentType;

	private String description;

	private String fileName;

	private boolean replaceExisting;

	private final AbstractTaskAttachmentSource source;

	public UnsubmittedTaskAttachment(AbstractTaskAttachmentSource source, TaskAttribute attribute) {
		Assert.isNotNull(source);
		this.source = source;
		this.attribute = attribute;

		contentType = source.getContentType();
		fileName = source.getName();
		description = source.getDescription();
		if (attribute != null) {
			TaskAttachmentMapper mapper = TaskAttachmentMapper.createFrom(attribute);
			if (mapper.getContentType() != null) {
				contentType = mapper.getContentType();
			}
			if (mapper.getFileName() != null) {
				fileName = mapper.getFileName();
			}
			if (mapper.getDescription() != null) {
				description = mapper.getDescription();
			}
			if (mapper.getReplaceExisting() != null) {
				replaceExisting = mapper.getReplaceExisting();
			}
		}
		if (description == null) {
			description = ""; //$NON-NLS-1$
		}
	}

	public InputStream createInputStream(IProgressMonitor monitor) throws CoreException {
		return source.createInputStream(monitor);
	}

	public TaskAttribute getAttribute() {
		return attribute;
	}

	public String getContentType() {
		return contentType;
	}

	public String getDescription() {
		return description;
	}

	public String getFileName() {
		return fileName;
	}

	public boolean getReplaceExisting() {
		return replaceExisting;
	}

	public AbstractTaskAttachmentSource getSource() {
		return source;
	}

}
