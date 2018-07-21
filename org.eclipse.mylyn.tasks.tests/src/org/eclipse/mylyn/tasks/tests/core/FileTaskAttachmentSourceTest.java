/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
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

	public void testGetContentTypeFromFilename() {
		assertEquals("text/plain", FileTaskAttachmentSource.getContentTypeFromFilename("a.txt"));
		assertEquals("text/plain", FileTaskAttachmentSource.getContentTypeFromFilename("foo.mylyn-test-text"));
		assertEquals("application/xml", FileTaskAttachmentSource.getContentTypeFromFilename("a.xml"));
		assertEquals("application/xml", FileTaskAttachmentSource.getContentTypeFromFilename("foo.mylyn-test-xml"));
	}

}
