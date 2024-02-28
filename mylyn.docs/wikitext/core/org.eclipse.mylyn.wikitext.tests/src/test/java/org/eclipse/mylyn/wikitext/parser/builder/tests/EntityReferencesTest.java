/*******************************************************************************
 * Copyright (c) 2016, 2024 David Green and others.
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

package org.eclipse.mylyn.wikitext.parser.builder.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.wikitext.parser.builder.EntityReferences;
import org.junit.Test;

@SuppressWarnings("nls")
public class EntityReferencesTest {

	@Test
	public void instance() {
		assertNotNull(EntityReferences.instance());
		assertSame(EntityReferences.instance(), EntityReferences.instance());
	}

	@Test
	public void equivalentStringNull() {
		NullPointerException e = assertThrows(NullPointerException.class,
				() -> EntityReferences.instance().equivalentString(null));
		assertTrue(e.getMessage().contains("Must provide an entityReference"));
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
