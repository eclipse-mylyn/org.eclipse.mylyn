/*******************************************************************************
 * Copyright (c) 2016 David Green and others.
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

package org.eclipse.mylyn.wikitext.parser.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class EntityReferencesTest {
	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void instance() {
		assertNotNull(EntityReferences.instance());
		assertSame(EntityReferences.instance(), EntityReferences.instance());
	}

	@Test
	public void equivalentStringNull() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must provide an entityReference");
		EntityReferences.instance().equivalentString(null);
	}

	@Test
	public void equivalentStringNamed() {
		assertEquivalentString("'", "apos");
		assertEquivalentString("\"", "quot");
		assertEquivalentString("Ö", "Ouml");
		assertEquivalentString("ö", "ouml");
		assertEquivalentString("⊠", "boxtimes");
	}

	@Test
	public void equivalentStringNumeric() {
		assertEquivalentString("'", "#39");
		assertEquivalentString("'", "#x00027");
		assertEquivalentString("'", "#x27");
		assertEquivalentString("핏", "#120143");
	}

	@Test
	public void equivalentStringMultiCharacter() {
		assertEquivalentString("𝒞", "Cscr");
		assertEquivalentString("𝔸", "Aopf");
	}

	@Test
	public void equivalentStringUnknown() {
		assertNull(EntityReferences.instance().equivalentString(""));
		assertNull(EntityReferences.instance().equivalentString("&;"));
		assertNull(EntityReferences.instance().equivalentString("&#;"));
		assertNull(EntityReferences.instance().equivalentString("&#x;"));
		assertNull(EntityReferences.instance().equivalentString("?"));
		assertNull(
				EntityReferences.instance().equivalentString("&#xffffffffffffffffffffffffffffffffffffffffffffffffff;"));
		assertNull(EntityReferences.instance().equivalentString("&#999999999999999999999999999999999999999;"));
	}

	private void assertEquivalentString(String expected, String entity) {
		assertEquals(expected, EntityReferences.instance().equivalentString(entity));
		assertEquals(expected, EntityReferences.instance().equivalentString("&" + entity + ";"));
	}
}
