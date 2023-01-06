/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.egit.github.core.CommitUser;
import org.eclipse.egit.github.core.Tag;
import org.eclipse.egit.github.core.TypedResource;
import org.junit.Test;

/**
 * Unit tests of {@link Tag}
 */
public class TagTest {

	/**
	 * Test default state of tag
	 */
	@Test
	public void defaultState() {
		Tag tag = new Tag();
		assertNull(tag.getMessage());
		assertNull(tag.getObject());
		assertNull(tag.getSha());
		assertNull(tag.getTag());
		assertNull(tag.getTagger());
		assertNull(tag.getUrl());
	}

	/**
	 * Test updating tag fields
	 */
	@Test
	public void updateFields() {
		Tag tag = new Tag();
		assertEquals("msg", tag.setMessage("msg").getMessage());
		TypedResource obj = new TypedResource();
		obj.setSha("abc");
		assertEquals(obj, tag.setObject(obj).getObject());
		assertEquals("0a0a", tag.setSha("0a0a").getSha());
		assertEquals("v1", tag.setTag("v1").getTag());
		CommitUser tagger = new CommitUser().setName("tag er");
		assertEquals(tagger, tag.setTagger(tagger).getTagger());
		assertEquals("url:", tag.setUrl("url:").getUrl());
	}
}
