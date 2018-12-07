/*******************************************************************************
 * Copyright (c) 2018 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Kevin de Vlaming - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.creole.tests.documentbuilder;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.creole.internal.CreoleDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;

import junit.framework.TestCase;

/**
 * @author Kevin de Vlaming
 */
public abstract class AbstractCreoleDocumentBuilderTest extends TestCase {

	protected DocumentBuilder builder;

	protected StringWriter out;

	@Override
	protected void setUp() throws Exception {
		out = new StringWriter();
		builder = new CreoleDocumentBuilder(out);
	}

	protected void assertMarkup(String expected) {
		String markup = out.toString();

		assertEquals(expected, markup);
	}

}