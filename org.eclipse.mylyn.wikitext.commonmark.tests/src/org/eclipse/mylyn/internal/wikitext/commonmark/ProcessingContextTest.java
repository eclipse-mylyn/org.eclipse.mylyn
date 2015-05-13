/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.commonmark;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContext.NamedUriWithTitle;
import org.junit.Test;

public class ProcessingContextTest {

	@Test
	public void empty() {
		ProcessingContext context = ProcessingContext.empty();
		assertNotNull(context);
		assertTrue(context.isEmpty());
		assertSame(context, ProcessingContext.empty());
	}

	@Test
	public void withReferenceDefinition() {
		ProcessingContext context = ProcessingContext.withReferenceDefinition("onE", "/uri", "a title");
		assertNotNull(context);
		assertFalse(context.isEmpty());
		assertNotNull(context.namedUriWithTitle("one"));
		assertNotNull(context.namedUriWithTitle("One"));
		NamedUriWithTitle link = context.namedUriWithTitle("ONE");
		assertEquals("onE", link.getName());
		assertEquals("/uri", link.getUri());
		assertEquals("a title", link.getTitle());
		assertNull(context.namedUriWithTitle("Unknown"));
		assertSame(ProcessingContext.empty(), ProcessingContext.withReferenceDefinition("", "one", "two"));
	}

	@Test
	public void merge() {
		ProcessingContext empty1 = ProcessingContext.empty();
		ProcessingContext empty2 = ProcessingContext.empty();
		assertSame(empty1, empty1.merge(empty2));
		ProcessingContext other = ProcessingContext.withReferenceDefinition("one", "/uri", "a title");
		assertSame(other, other.merge(empty1));
		assertSame(other, empty1.merge(other));
		ProcessingContext other2 = ProcessingContext.withReferenceDefinition("two", "/uri2", "a title");
		ProcessingContext merged = other.merge(other2);
		assertNotSame(other2, merged);
		assertNotSame(other, merged);
		assertNotNull(merged.namedUriWithTitle("two"));
		assertNotNull(merged.namedUriWithTitle("one"));
	}

	@Test
	public void precedence() {
		ProcessingContext one = ProcessingContext.withReferenceDefinition("one", "1", "a title");
		ProcessingContext one2 = ProcessingContext.withReferenceDefinition("one", "2", "a title");
		ProcessingContext merged = one.merge(one2);
		assertEquals("1", merged.namedUriWithTitle("one").getUri());
	}
}
