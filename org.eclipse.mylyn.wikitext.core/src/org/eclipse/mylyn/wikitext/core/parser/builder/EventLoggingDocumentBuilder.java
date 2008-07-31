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
 */
public class EventLoggingDocumentBuilder extends DocumentBuilder {

	private Logger logger = Logger.getLogger(EventLoggingDocumentBuilder.class.getName());

	private int blockDepth = 0;

	@Override
	public void acronym(String text, String definition) {
		logger.info("ACRONYM:"+text+","+definition);
	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		++blockDepth;
		logger.info("BLOCK START["+blockDepth+"]:"+type);
	}

	@Override
	public void beginDocument() {
		logger.info("DOCUMENT START");
	}

	@Override
	public void beginHeading(int level, Attributes attributes) {
		logger.info("HEADING START:"+level);
	}

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
		logger.info("SPAN START:"+type);
	}

	@Override
	public void characters(String text) {
		logger.info("CHARACTERS:"+text);
	}

	@Override
	public void charactersUnescaped(String text) {
		logger.info("HTML LITERAL:"+text);
	}

	@Override
	public void endBlock() {
		logger.info("END BLOCK["+blockDepth+"]");
		--blockDepth;
	}

	@Override
	public void endDocument() {
		logger.info("END DOCUMENT");
	}

	@Override
	public void endHeading() {
		logger.info("END HEADING");
	}

	@Override
	public void endSpan() {
		logger.info("END SPAN");
	}

	@Override
	public void entityReference(String entity) {
		logger.info("ENTITY: "+entity);
	}

	@Override
	public void image(Attributes attributes, String url) {
		logger.info("IMAGE: "+url);
	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes imageAttributes,String href, String imageUrl) {
		logger.info("IMAGE LINK: "+href+", "+imageUrl);

	}

	@Override
	public void lineBreak() {
		logger.info("LINE BREAK");
	}

	@Override
	public void link(Attributes attributes,String hrefOrHashName, String text) {
		logger.info("LINK: "+hrefOrHashName+", "+text);
	}


}
