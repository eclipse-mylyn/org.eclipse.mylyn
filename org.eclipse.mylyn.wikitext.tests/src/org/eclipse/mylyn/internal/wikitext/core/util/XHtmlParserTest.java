/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.util;

import org.eclipse.mylyn.internal.wikitext.core.parser.builder.AbstractSaxHtmlParser;
import org.eclipse.mylyn.internal.wikitext.core.parser.builder.XHtmlParser;

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
