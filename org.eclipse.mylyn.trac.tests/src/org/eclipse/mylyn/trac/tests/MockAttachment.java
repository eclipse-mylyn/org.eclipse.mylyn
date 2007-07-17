/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.mylyn.tasks.core.ITaskAttachment;

public class MockAttachment implements ITaskAttachment {

	private byte[] data;

	public MockAttachment(byte[] data) {
		this.data = data;
	}
	
	public InputStream createInputStream() throws IOException {
		return new ByteArrayInputStream(data);
	}

	public String getContentType() {
		return "application/binary";
	}

	public String getDescription() {
		return "description";
	}

	public String getFilename() {
		return "filename.txt";
	}

	public long getLength() {
		return data.length;
	}

	public boolean isPatch() {
		return false;
	}

}
