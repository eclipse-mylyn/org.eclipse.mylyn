/*******************************************************************************
 * Copyright (c) 2018, 2024 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Kevin de Vlaming - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.creole.tests.documentbuilder;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.creole.internal.CreoleDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.junit.Before;

/**
 * @author Kevin de Vlaming
 */
public abstract class AbstractCreoleDocumentBuilderTest {

	protected DocumentBuilder builder;

	protected StringWriter out;

	@Before
	public void setUp() throws Exception {
		out = new StringWriter();
		builder = new CreoleDocumentBuilder(out);
	}

	protected void assertMarkup(String expected) {
		String markup = out.toString();

		assertEquals(expected, markup);
	}

}