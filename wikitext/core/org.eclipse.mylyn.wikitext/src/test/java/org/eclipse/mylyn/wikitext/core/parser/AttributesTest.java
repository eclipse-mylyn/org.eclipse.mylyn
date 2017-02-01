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

package org.eclipse.mylyn.wikitext.core.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class AttributesTest {

	@Test
	public void testClone() {
		Attributes original = new Attributes("1", "class", "style", "lang");
		Attributes copy = original.clone();
		assertNotNull(copy);
		assertEquals(original.getId(), copy.getId());
		assertEquals(original.getCssClass(), copy.getCssClass());
		assertEquals(original.getCssStyle(), copy.getCssStyle());
		assertEquals(original.getLanguage(), copy.getLanguage());
	}
}
