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

import java.io.Writer;
import java.net.URI;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.util.DefaultXmlStreamWriter;
import org.eclipse.mylyn.wikitext.core.util.XmlStreamWriter;

/**
 *
 *
 * @author David Green
 */
public abstract class AbstractXmlDocumentBuilder extends DocumentBuilder {
	private static final Pattern ABSOLUTE_URL_PATTERN = Pattern.compile("[a-zA-Z]{3,8}://?.*");

	protected final XmlStreamWriter writer;
	protected URI base;
	protected boolean baseInHead = false;

	public AbstractXmlDocumentBuilder(Writer out) {
		writer = createXmlStreamWriter(out);
	}

	public AbstractXmlDocumentBuilder(XmlStreamWriter writer) {
		this.writer = writer;

	}

	protected XmlStreamWriter createXmlStreamWriter(Writer out) {
		XmlStreamWriter writer = new DefaultXmlStreamWriter(out);
		return writer;
	}

	@Override
	public void characters(String text) {
		writer.writeCharacters(text);
	}
	protected String makeUrlAbsolute(String url) {
		if (base == null || baseInHead || url == null) {
			return url;
		}
		if (ABSOLUTE_URL_PATTERN.matcher(url).matches()) {
			return url;
		}
		if (url.startsWith("#")) {
			return url;
		}
		String absoluteUrl = base.toString();
		if (!absoluteUrl.endsWith("/") && !url.startsWith("/")) {
			absoluteUrl = absoluteUrl+'/';
		}
		absoluteUrl = absoluteUrl+url;
		return absoluteUrl;
	}

	/**
	 * indicate if the given URL is a link to an external source
	 * 
	 * @param url the URL
	 * 
	 * @return true if the given URL links to an external source
	 */
	protected boolean isExternalLink(String url) {
		if (url == null) {
			return false;
		}
		if (ABSOLUTE_URL_PATTERN.matcher(url).matches()) {
			if (base == null || !url.startsWith(base.toString())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Set the base URI of the HTML document.  Causes all relative URLs to be prefixed with the base URI.
	 * The base URI is assumed to refer to a foler-like resource.
	 * 
	 * @param uri the URI, or null
	 */
	public void setBase(URI uri) {
		base = uri;
	}

	/**
	 * Get the base URI of the HTML document.  A not-null value causes all relative URLs to be prefixed with the base URI.
	 * The base URI is assumed to refer to a foler-like resource.
	 */
	public URI getBase() {
		return base;
	}

	/**
	 * Indicate if the {@link #getBase() base URI} should be emitted into the &lt;head&gt; of the document.
	 * The default value is false.
	 * Ignored unless {@link #isEmitAsDocument()}
	 */
	public boolean isBaseInHead() {
		return baseInHead;
	}

	/**
	 * Indicate if the {@link #getBase() base URI} should be emitted into the &lt;head&gt; of the document.
	 * The default value is false.
	 * Ignored unless {@link #isEmitAsDocument()}
	 */
	public void setBaseInHead(boolean baseInHead) {
		this.baseInHead = baseInHead;
	}
}
