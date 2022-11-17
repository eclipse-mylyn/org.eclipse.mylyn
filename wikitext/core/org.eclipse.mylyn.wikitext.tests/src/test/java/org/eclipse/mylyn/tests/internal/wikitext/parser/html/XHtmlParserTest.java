/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies
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

package org.eclipse.mylyn.tests.internal.wikitext.parser.html;

import org.eclipse.mylyn.wikitext.internal.parser.html.AbstractSaxHtmlParser;
import org.eclipse.mylyn.wikitext.internal.parser.html.XHtmlParser;

/**
 * @author David Green
 * @see XHtmlParser
 */
public class XHtmlParserTest extends AbstractSaxParserTest {

	@Override
	protected AbstractSaxHtmlParser createParser() {
		return new XHtmlParser();
	}

}
