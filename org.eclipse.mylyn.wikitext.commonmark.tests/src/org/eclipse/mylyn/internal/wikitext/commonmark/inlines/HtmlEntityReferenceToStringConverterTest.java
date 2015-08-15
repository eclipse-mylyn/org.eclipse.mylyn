/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.commonmark.inlines;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HtmlEntityReferenceToStringConverterTest {

	@Test
	public void foo() {
		assertEntity("&", "amp");
		assertEntity("ö", "ouml");
		assertEntity("!", "#33");
		assertEntity("0", "#x30");
		assertEntity("0", "#X30");
		assertEntity("", "#x0");
		assertEntity("", "#X0");
		assertEntity("", "#0");
		assertEntity("", "notathing");
	}

	private void assertEntity(String expected, String entity) {
		assertEquals(expected, HtmlEntityReferenceToStringConverter.toString(entity));
	}
}
