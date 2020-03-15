/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
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
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class HtmlEntitiesTest {
	private final HtmlEntities instance = HtmlEntities.instance();

	@Test
	public void instance() {
		assertNotNull(instance);
		assertSame(instance, HtmlEntities.instance());
	}

	@Test
	public void nameToEntityReferencesUnknownName() {
		assertEquals(ImmutableList.of(), instance.nameToEntityReferences("asdf"));
		assertEquals(ImmutableList.of(), instance.nameToEntityReferences(null));
	}

	@Test
	public void nameToEntityReferences() {
		assertEquals(ImmutableList.of("#160"), instance.nameToEntityReferences("nbsp"));
		assertEquals(ImmutableList.of("#8807", "#824"), instance.nameToEntityReferences("ngE"));
		assertEquals(ImmutableList.of("#8817"), instance.nameToEntityReferences("nge"));
	}
}
