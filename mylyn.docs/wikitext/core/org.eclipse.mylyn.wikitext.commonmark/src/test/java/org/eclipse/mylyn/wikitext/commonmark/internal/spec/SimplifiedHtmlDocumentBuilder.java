/*******************************************************************************
 * Copyright (c) 2015 David Green.
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

package org.eclipse.mylyn.wikitext.commonmark.internal.spec;

import static com.google.common.base.MoreObjects.firstNonNull;

import java.io.Writer;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;

public class SimplifiedHtmlDocumentBuilder extends HtmlDocumentBuilder {

	public SimplifiedHtmlDocumentBuilder(Writer out) {
		super(out, false);
		setFilterEntityReferences(true);
	}

	@Override
	public void image(Attributes attributes, String url) {
		writer.writeEmptyElement(getHtmlNsUri(), "img"); //$NON-NLS-1$
		writer.writeAttribute("src", makeUrlAbsolute(url)); //$NON-NLS-1$
		if (attributes instanceof ImageAttributes) {
			ImageAttributes imageAttributes = (ImageAttributes) attributes;
			writer.writeAttribute(getHtmlNsUri(), "alt", firstNonNull(imageAttributes.getAlt(), ""));
			if (imageAttributes.getTitle() != null) {
				writer.writeAttribute(getHtmlNsUri(), "title", imageAttributes.getTitle());
			}
		}
	}

	@Override
	public void entityReference(String entity) {
		// TODO Auto-generated method stub
		super.entityReference(entity);
	}
}
