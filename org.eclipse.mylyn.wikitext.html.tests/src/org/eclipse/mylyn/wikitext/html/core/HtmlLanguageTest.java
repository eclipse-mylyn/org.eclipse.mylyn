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

package org.eclipse.mylyn.wikitext.html.core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HtmlLanguageTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void isStub() {
		thrown.expect(UnsupportedOperationException.class);
		new HtmlLanguage();
	}
}
