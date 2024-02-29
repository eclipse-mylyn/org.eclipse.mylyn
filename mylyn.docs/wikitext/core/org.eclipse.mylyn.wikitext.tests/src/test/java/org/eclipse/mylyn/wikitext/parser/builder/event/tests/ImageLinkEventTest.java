/*******************************************************************************
 * Copyright (c) 2013, 2024 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.builder.event.tests;

import static org.eclipse.mylyn.wikitext.test.EqualityAsserts.assertEquality;
import static org.eclipse.mylyn.wikitext.test.EqualityAsserts.assertInequality;
import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.wikitext.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.builder.event.ImageLinkEvent;
import org.junit.Test;

@SuppressWarnings({ "nls", "restriction" })
public class ImageLinkEventTest {

	@Test
	public void testToString() {
		assertEquals("imageLink(http://example.com,http://example.com/img.png)",
				new ImageLinkEvent(new LinkAttributes(), new ImageAttributes(), "http://example.com",
						"http://example.com/img.png").toString());
	}

	@Test
	public void equals() {
		assertEquality(
				new ImageLinkEvent(new LinkAttributes(), new ImageAttributes(), "http://example.com",
						"http://example.com/img.png"),
				new ImageLinkEvent(new LinkAttributes(), new ImageAttributes(), "http://example.com",
						"http://example.com/img.png"));
		assertInequality(
				new ImageLinkEvent(new LinkAttributes(), new ImageAttributes(), "http://example.com",
						"http://example.com/img.png"),
				new ImageLinkEvent(new LinkAttributes(), new ImageAttributes(), "http://example.com",
						"http://example.com/img2.png"));
		assertInequality(
				new ImageLinkEvent(new LinkAttributes(), new ImageAttributes(), "http://example.com",
						"http://example.com/img.png"),
				new ImageLinkEvent(new LinkAttributes(), new ImageAttributes(), "http://example.com/2",
						"http://example.com/img.png"));
	}
}
