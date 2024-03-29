/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
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
package org.eclipse.mylyn.wikitext.parser.markup;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;

/**
 * @author David Green
 * @since 3.0
 */
public class Processor implements Cloneable {
	protected AbstractMarkupLanguage markupLanguage;

	protected DocumentBuilder builder;

	protected MarkupParser parser;

	protected ContentState state;

	public Processor() {
	}

	/**
	 * The builder that is the target for output
	 */
	public DocumentBuilder getBuilder() {
		return builder;
	}

	/**
	 * The parser that is actively using this processor
	 */
	public MarkupParser getParser() {
		return parser;
	}

	public void setParser(MarkupParser parser) {
		if (parser != null && parser.getMarkupLanguage() != markupLanguage) {
			if (markupLanguage == null) {
				markupLanguage = (AbstractMarkupLanguage) parser.getMarkupLanguage();
			} else {
				throw new IllegalStateException();
			}
		}
		this.parser = parser;
		builder = parser == null ? null : parser.getBuilder();

	}

	void setMarkupLanguage(AbstractMarkupLanguage markupLanguage) {
		this.markupLanguage = markupLanguage;
	}

	/**
	 *
	 */
	public AbstractMarkupLanguage getMarkupLanguage() {
		return markupLanguage;
	}

	public ContentState getState() {
		return state;
	}

	public void setState(ContentState state) {
		this.state = state;
	}

	@Override
	public Processor clone() {
		try {
			Processor copy = (Processor) super.clone();
			copy.parser = null;
			copy.state = null;
			copy.builder = null;
			copy.markupLanguage = null;
			return copy;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}
}
