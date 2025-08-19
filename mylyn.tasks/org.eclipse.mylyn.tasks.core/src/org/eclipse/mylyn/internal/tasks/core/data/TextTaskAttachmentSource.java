/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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
		return "text/plain"; //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return ""; //$NON-NLS-1$
	}

	@Override
	public long getLength() {
		return contents.getBytes().length;
	}

	@Override
	public String getName() {
		return "clipboard.txt"; //$NON-NLS-1$
	}

	@Override
	public boolean isLocal() {
		return true;
	}

}