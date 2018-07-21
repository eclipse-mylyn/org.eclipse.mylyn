/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentPartSource;

public class BugzillaFilePart extends FilePart {

	private final String filename;

	public BugzillaFilePart(AbstractTaskAttachmentSource source, String filename, String contentType, String charset) {
		super(IBugzillaConstants.POST_INPUT_DATA, new TaskAttachmentPartSource(source, null), contentType, charset);
		this.filename = filename;
	}

	@Override
	protected void sendDispositionHeader(OutputStream out) throws IOException {
		super.sendDispositionHeader(out);
		if (filename != null) {
			out.write(EncodingUtil.getAsciiBytes(FILE_NAME));
			out.write(QUOTE_BYTES);
			out.write(EncodingUtil.getBytes(filename, getCharSet()));
			out.write(QUOTE_BYTES);
		}
	}
}
