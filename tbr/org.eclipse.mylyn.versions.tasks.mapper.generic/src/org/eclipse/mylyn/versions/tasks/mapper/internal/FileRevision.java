/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.versions.tasks.mapper.internal;

import java.net.URI;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.ITag;

/**
 * 
 * @author Kilian Matt
 *
 */
public class FileRevision implements IFileRevision {
	private String contentIdentifier;

	public FileRevision(String contentIdentifier) {
		this.contentIdentifier = contentIdentifier;
	}

	public IStorage getStorage(IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public String getName() {
		throw new UnsupportedOperationException();
	}

	public URI getURI() {
		throw new UnsupportedOperationException();
	}

	public long getTimestamp() {
		throw new UnsupportedOperationException();
	}

	public boolean exists() {
		throw new UnsupportedOperationException();
	}

	public String getContentIdentifier() {
		return contentIdentifier;
	}

	public String getAuthor() {
		throw new UnsupportedOperationException();
	}

	public String getComment() {
		throw new UnsupportedOperationException();
	}

	public ITag[] getBranches() {
		throw new UnsupportedOperationException();
	}

	public ITag[] getTags() {
		throw new UnsupportedOperationException();
	}

	public boolean isPropertyMissing() {
		throw new UnsupportedOperationException();
	}

	public IFileRevision withAllProperties(IProgressMonitor monitor)
			throws CoreException {
		throw new UnsupportedOperationException();
	}
}