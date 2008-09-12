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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;

public class TextTaskAttachmentSource extends AbstractTaskAttachmentSource {

	private final String contents;

	public TextTaskAttachmentSource(String contents) {
		this.contents = contents;
	}

	@Override
	public InputStream createInputStream(IProgressMonitor monitor) throws CoreException {
		return new ByteArrayInputStream(contents.getBytes());
	}

	@Override
	public String getContentType() {
		return "text/plain";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public long getLength() {
		return contents.getBytes().length;
	}

	@Override
	public String getName() {
		return "clipboard.txt";
	}

	@Override
	public boolean isLocal() {
		return true;
	}

}