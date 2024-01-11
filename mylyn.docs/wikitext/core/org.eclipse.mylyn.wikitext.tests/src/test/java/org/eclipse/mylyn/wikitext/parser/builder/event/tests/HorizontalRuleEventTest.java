/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
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
import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.wikitext.parser.builder.event.HorizontalRuleEvent;
import org.junit.Test;

public class HorizontalRuleEventTest {

	@Test
	public void testToString() {
		assertEquals("horizontalRule()", new HorizontalRuleEvent().toString());
	}

	@Test
	public void equals() {
		assertEquality(new HorizontalRuleEvent(), new HorizontalRuleEvent());
	}
}
