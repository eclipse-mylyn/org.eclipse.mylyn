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
