/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.reviews.core.ReviewConstants;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;

/*
 * @author Kilian Matt
 */
public class ReviewCommentTaskAttachmentSource extends
		AbstractTaskAttachmentSource {

	private byte[] source;

	public ReviewCommentTaskAttachmentSource(byte[] source) {
		this.source = source;
	}

	@Override
	public InputStream createInputStream(IProgressMonitor monitor)
			throws CoreException {
		return new ByteArrayInputStream(source);
	}

	@Override
	public String getContentType() {
		return "application/octet-stream"; //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return Messages.ReviewCommentTaskAttachmentSource_Description;
	}

	@Override
	public long getLength() {
		return source.length;
	}

	@Override
	public String getName() {
		return ReviewConstants.REVIEW_DATA_CONTAINER;
	}

	@Override
	public boolean isLocal() {
		return true;
	}

}
