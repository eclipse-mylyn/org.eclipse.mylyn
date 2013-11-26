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

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;

public class HtmlLanguage extends MarkupLanguage {

	public HtmlLanguage() {
		setName("HTML"); //$NON-NLS-1$
		throw new UnsupportedOperationException();
	}

	@Override
	public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
		throw new UnsupportedOperationException();
	}

}
