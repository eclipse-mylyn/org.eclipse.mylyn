/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.builder.event;

import static org.eclipse.mylyn.internal.wikitext.test.EqualityAsserts.assertEquality;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EndSpanEventTest {

	@Test
	public void testToString() {
		assertEquals("endSpan()", new EndSpanEvent().toString());
	}

	@Test
	public void equals() {
		assertEquality(new EndSpanEvent(), new EndSpanEvent());
	}
}
