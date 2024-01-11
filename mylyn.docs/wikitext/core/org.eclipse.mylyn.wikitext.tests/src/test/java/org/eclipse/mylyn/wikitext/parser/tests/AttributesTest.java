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

package org.eclipse.mylyn.wikitext.parser.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.junit.Test;

public class AttributesTest {

	@Test
	public void testClone() {
		Attributes original = createAttributesWithPopulatedValues();
		Attributes copy = original.clone();
		assertNotNull(copy);
		assertEquals(original.getId(), copy.getId());
		assertEquals(original.getCssClass(), copy.getCssClass());
		assertEquals(original.getCssStyle(), copy.getCssStyle());
		assertEquals(original.getLanguage(), copy.getLanguage());
	}

	@Test
	public void copyInto() {
		Attributes original = createAttributesWithPopulatedValues();
		Attributes other = new Attributes();
		original.copyInto(other);
		assertValuesEqual(original, other);
	}

	private void assertValuesEqual(Attributes original, Attributes other) {
		assertEquals(original.getId(), other.getId());
		assertEquals(original.getCssClass(), other.getCssClass());
		assertEquals(original.getCssStyle(), other.getCssStyle());
		assertEquals(original.getLanguage(), other.getLanguage());
		assertEquals(original.getTitle(), other.getTitle());
	}

	private Attributes createAttributesWithPopulatedValues() {
		return new Attributes("1", "class", "style", "lang");
	}
}
