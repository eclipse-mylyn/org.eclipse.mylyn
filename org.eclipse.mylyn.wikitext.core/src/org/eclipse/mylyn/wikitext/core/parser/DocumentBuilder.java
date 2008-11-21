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
package org.eclipse.mylyn.wikitext.core.parser;

/**
 * The 'Builder' design pattern, for documents. Implementations can build a specific kind of document (such as HTML, or
 * DocBook).
 * <p>
 * Note that many methods take {@link Attributes} to specify attributes of the element, however most of these methods
 * may take a more specific subclass of {@link Attributes}.
 * </p>
 * 
 * 
 * @author David Green
 * 
 */
public abstract class DocumentBuilder {

	protected Locator locator;

	public enum BlockType {
		PARAGRAPH,
		/**
		 * A text box that contains a helpful tip
		 */
		TIP,
		/**
		 * A text box that contains a warning
		 */
		WARNING,
		/**
		 * A text box that contains information
		 */
		INFORMATION,
		/**
		 * A text box that contains a note
		 */
		NOTE,
		/**
		 * A text box
		 */
		PANEL, FOOTNOTE, QUOTE, CODE, PREFORMATTED, NUMERIC_LIST, BULLETED_LIST, LIST_ITEM, TABLE, TABLE_ROW, TABLE_CELL_HEADER, TABLE_CELL_NORMAL, DEFINITION_LIST, DEFINITION_TERM, DEFINITION_ITEM;

	}

	public enum SpanType {
		EMPHASIS, STRONG, ITALIC, BOLD, CITATION, DELETED, INSERTED, SUPERSCRIPT, SUBSCRIPT, SPAN, CODE, MONOSPACE, UNDERLINED
	}

	/**
	 * Begin a document. Calling this method is optional for some builders, however if called then it must be matched by
	 * a corresponding call to {@link #endDocument()}.
	 * 
	 * @see #endDocument()
	 */
	public abstract void beginDocument();

	/**
	 * End a document.
	 * 
	 * @see #endDocument()
	 */
	public abstract void endDocument();

	/**
	 * Begin a block of the specified type.
	 * 
	 * Builder implementations may do a best-effort application of the provided attributes. Note that the provided
	 * attributes *may* be a subclass of the {@link Attributes} class, in which case the builder may attempt to apply
	 * the attributes specified. Builders may choose to ignore attributes, and should fail silently if the given
	 * attributes are not as expected.
	 * 
	 * Each call to this method must be matched by a corresponding call to {@link #endBlock()}.
	 * 
	 * @param type
	 * @param attributes
	 *            the attributes to apply to the block. Callers may choose to specify a more specialized set of
	 *            attributes by providing a subclass instance.
	 * 
	 * @see #endBlock()
	 */
	public abstract void beginBlock(BlockType type, Attributes attributes);

	/**
	 * End a block that was {@link #beginBlock(org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType, Attributes)
	 * started}.
	 */
	public abstract void endBlock();

	/**
	 * Begin a span of the specified type.
	 * 
	 * Builder implementations may do a best-effort application of the provided attributes.
	 * 
	 * Each call to this method must be matched by a corresponding call to {@link #endSpan()}.
	 * 
	 * @param type
	 * @param attributes
	 *            the attributes to apply to the span
	 * 
	 * @see #endSpan()
	 */
	public abstract void beginSpan(SpanType type, Attributes attributes);

	/**
	 * End a span that was {@link #beginSpan(org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType, Attributes)
	 * started}.
	 * 
	 * @see #beginSpan(org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType, Attributes)
	 */
	public abstract void endSpan();

	/**
	 * Begin a heading of the specified level (usually 1-6).
	 * 
	 * Builder implementations may do a best-effort application of the provided attributes.
	 * 
	 * Each call to this method must be matched by a corresponding call to {@link #endHeading()}.
	 * 
	 * @param level
	 *            the level of the heading, usually 1-6
	 * @param attributes
	 *            the attributes to apply to the heading
	 * 
	 * @see #endHeading()
	 */
	public abstract void beginHeading(int level, Attributes attributes);

	/**
	 * End a span that was {@link #beginHeading(int, Attributes) started}.
	 * 
	 * @see #beginHeading(int, Attributes)
	 */
	public abstract void endHeading();

	/**
	 * Emit the given text as characters where special characters are encoded according to the output format rules.
	 * 
	 * @param text
	 *            the text to emit.
	 */
	public abstract void characters(String text);

	/**
	 * An XML entity reference.
	 * 
	 * @param entity
	 *            the entity
	 */
	public abstract void entityReference(String entity);

	/**
	 * Build the image with the given attributes
	 * 
	 * @param attributes
	 *            the attributes, which may be an {@link ImageAttributes}.
	 * @param url
	 *            the URL to the image, possibly relative
	 */
	public abstract void image(Attributes attributes, String url);

	/**
	 * Create a hyperlink to the given url
	 * 
	 * @param attributes
	 *            the attributes of the link
	 * @param hrefOrHashName
	 *            the url (which may be internal to the page if prefixed with a hash '#')
	 * @param text
	 *            the text of the hyperlink
	 */
	public abstract void link(Attributes attributes, String hrefOrHashName, String text);

	/**
	 * Create a hyperlink whose visual representation is an image. Implementations must apply the attributes to the
	 * image tag. For example, if the builder constructs HTML, the builder would emit
	 * <code>&lt;a href="...">&lt;img src="..."/>&lt;/a></code>. In this case if the attributes define a css class then
	 * the resulting HTML should look like this: <code>&lt;a href="...">&lt;img src="..." class="..."/>&lt;/a></code>
	 * 
	 * @param linkAttributes
	 *            the attributes of the link, which may be {@link LinkAttributes}
	 * @param imageAttributes
	 *            the attributes of the image , which may be {@link ImageAttributes}
	 * @param href
	 *            the url (which may be internal to the page if prefixed with a hash '#')
	 * @param imageUrl
	 *            the url of the image, which may be relative
	 */
	public abstract void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl);

	/**
	 * Create a hyperlink whose visual representation is an image. Implementations must apply the attributes to the
	 * image tag. For example, if the builder constructs HTML, the builder would emit
	 * <code>&lt;a href="...">&lt;img src="..."/>&lt;/a></code>. In this case if the attributes define a css class then
	 * the resulting HTML should look like this: <code>&lt;a href="...">&lt;img src="..." class="..."/>&lt;/a></code>
	 * 
	 * @param attributes
	 *            the attributes of the image, which may be {@link ImageAttributes}
	 * @param href
	 *            the url (which may be internal to the page if prefixed with a hash '#')
	 * @param imageUrl
	 *            the url of the image, which may be relative
	 * 
	 * @see #imageLink(Attributes, Attributes, String, String)
	 */
	public final void imageLink(Attributes attributes, String href, String imageUrl) {
		imageLink(new LinkAttributes(), attributes, href, imageUrl);
	}

	/**
	 * @see #link(Attributes, String, String)
	 */
	public final void link(String hrefOrHashName, String text) {
		link(new LinkAttributes(), hrefOrHashName, text);
	}

	/**
	 * @see #imageLink(Attributes, String, String)
	 */
	public final void imageLink(String href, String imageUrl) {
		imageLink(new LinkAttributes(), new ImageAttributes(), href, imageUrl);
	}

	/**
	 * Emit an acronym
	 * 
	 * @param text
	 *            the acronym to emit
	 * @param definition
	 *            the definition of the acronym, which is typically displayed on mouse hover
	 */
	public abstract void acronym(String text, String definition);

	/**
	 * Create a line break (eg: br in html). Not all builders need support line breaks.
	 */
	public abstract void lineBreak();

	/**
	 * Create unescaped characters, usually with some embedded HTML markup. Note that using this method causes the
	 * output to be HTML-centric
	 * 
	 * @param literal
	 *            the literal characters to emit
	 */
	public abstract void charactersUnescaped(String literal);

	/**
	 * Set the locator for the current session
	 * 
	 * @param locator
	 *            the locator that provides information about the current location in the markup
	 */
	public void setLocator(Locator locator) {
		this.locator = locator;
	}

	/**
	 * The locator for the current session
	 */
	public Locator getLocator() {
		return locator;
	}

}
