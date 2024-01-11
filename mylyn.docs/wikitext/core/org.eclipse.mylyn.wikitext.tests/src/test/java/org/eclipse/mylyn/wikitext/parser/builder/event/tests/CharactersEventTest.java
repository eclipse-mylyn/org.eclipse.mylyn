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

import org.eclipse.mylyn.wikitext.parser.builder.event.CharactersEvent;
import org.junit.Test;

public class CharactersEventTest {

	@Test
	public void testToString() {
		assertEquals("characters(\"test it\")", new CharactersEvent("test it").toString());
	}

	@Test
	public void equals() {
		assertEquality(new CharactersEvent("test it"), new CharactersEvent("test it"));
		assertInequality(new CharactersEvent("test it"), new CharactersEvent("test it again"));
	}

	@Test
	public void getText() {
		assertEquals("the value", new CharactersEvent("the value").getText());
	}
}
