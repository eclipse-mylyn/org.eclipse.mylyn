/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.builder.event;

import static org.eclipse.mylyn.internal.wikitext.core.test.EqualityAsserts.assertEquality;
import static org.eclipse.mylyn.internal.wikitext.core.test.EqualityAsserts.assertInequality;
import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.junit.Test;

public class BeginSpanEventTest {

	@Test
	public void testToString() {
		assertEquals("beginSpan(CODE)", new BeginSpanEvent(SpanType.CODE, new Attributes()).toString());
	}

	@Test
	public void equals() {
		assertEquality(new BeginSpanEvent(SpanType.CODE, new Attributes()), new BeginSpanEvent(SpanType.CODE,
				new Attributes()));
		assertInequality(new BeginSpanEvent(SpanType.CODE, new Attributes()), new BeginSpanEvent(SpanType.BOLD,
				new Attributes()));
	}
}
