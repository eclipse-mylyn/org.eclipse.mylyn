/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.parser.builder;

import java.util.logging.Logger;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

/**
 * 
 * 
 * @author David Green
 * @since 1.0
 */
public class EventLoggingDocumentBuilder extends DocumentBuilder {

	private final Logger logger = Logger.getLogger(EventLoggingDocumentBuilder.class.getName());

	private int blockDepth = 0;

	@Override
	public void acronym(String text, String definition) {
		logger.info("ACRONYM:" + text + "," + definition); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		++blockDepth;
		logger.info("BLOCK START[" + blockDepth + "]:" + type); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void beginDocument() {
		logger.info("DOCUMENT START"); //$NON-NLS-1$
	}

	@Override
	public void beginHeading(int level, Attributes attributes) {
		logger.info("HEADING START:" + level); //$NON-NLS-1$
	}

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
		logger.info("SPAN START:" + type); //$NON-NLS-1$
	}

	@Override
	public void characters(String text) {
		logger.info("CHARACTERS:" + text); //$NON-NLS-1$
	}

	@Override
	public void charactersUnescaped(String text) {
		logger.info("HTML LITERAL:" + text); //$NON-NLS-1$
	}

	@Override
	public void endBlock() {
		logger.info("END BLOCK[" + blockDepth + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		--blockDepth;
	}

	@Override
	public void endDocument() {
		logger.info("END DOCUMENT"); //$NON-NLS-1$
	}

	@Override
	public void endHeading() {
		logger.info("END HEADING"); //$NON-NLS-1$
	}

	@Override
	public void endSpan() {
		logger.info("END SPAN"); //$NON-NLS-1$
	}

	@Override
	public void entityReference(String entity) {
		logger.info("ENTITY: " + entity); //$NON-NLS-1$
	}

	@Override
	public void image(Attributes attributes, String url) {
		logger.info("IMAGE: " + url); //$NON-NLS-1$
	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		logger.info("IMAGE LINK: " + href + ", " + imageUrl); //$NON-NLS-1$ //$NON-NLS-2$

	}

	@Override
	public void lineBreak() {
		logger.info("LINE BREAK"); //$NON-NLS-1$
	}

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		logger.info("LINK: " + hrefOrHashName + ", " + text); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
