/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.core;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;

/**
 * @author David Green
 */
public class FileTaskAttachmentSourceTest extends TestCase {
	public void getContentTypeFromFilename() {
		assertEquals("text/plain", FileTaskAttachmentSource.getContentTypeFromFilename("a.txt"));
		assertEquals("text/plain", FileTaskAttachmentSource.getContentTypeFromFilename("foo.mylyn-test-text"));
		assertEquals("application/xml", FileTaskAttachmentSource.getContentTypeFromFilename("a.xml"));
		assertEquals("application/xml", FileTaskAttachmentSource.getContentTypeFromFilename("foo.mylyn-test-xml"));
	}
}
