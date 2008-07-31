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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.core.parser.ListAttributes;
import org.eclipse.mylyn.wikitext.core.parser.QuoteAttributes;
import org.eclipse.mylyn.wikitext.core.parser.TableAttributes;
import org.eclipse.mylyn.wikitext.core.parser.TableCellAttributes;
import org.eclipse.mylyn.wikitext.core.parser.TableRowAttributes;
import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes.Align;
import org.eclipse.mylyn.wikitext.core.util.DefaultXmlStreamWriter;
import org.eclipse.mylyn.wikitext.core.util.FormattingXMLStreamWriter;
import org.eclipse.mylyn.wikitext.core.util.XmlStreamWriter;

/**
 *
 *
 * @author David Green
 */
public class HtmlDocumentBuilder extends AbstractXmlDocumentBuilder {


	private static final Map<SpanType,String> spanTypeToElementName = new HashMap<SpanType,String>();
	static {
		spanTypeToElementName.put(SpanType.BOLD,"b");
		spanTypeToElementName.put(SpanType.CITATION,"cite");
		spanTypeToElementName.put(SpanType.ITALIC,"i");
		spanTypeToElementName.put(SpanType.EMPHASIS,"em");
		spanTypeToElementName.put(SpanType.STRONG,"strong");
		spanTypeToElementName.put(SpanType.DELETED,"del");
		spanTypeToElementName.put(SpanType.INSERTED,"ins");
		spanTypeToElementName.put(SpanType.UNDERLINED,"u");
		spanTypeToElementName.put(SpanType.SUPERSCRIPT,"sup");
		spanTypeToElementName.put(SpanType.SUBSCRIPT,"sub");
		spanTypeToElementName.put(SpanType.SPAN,"span");
		spanTypeToElementName.put(SpanType.CODE,"code");
		spanTypeToElementName.put(SpanType.MONOSPACE,"tt");
	}

	private static final Map<BlockType,ElementInfo> blockTypeToElementInfo = new HashMap<BlockType,ElementInfo>();
	static {
		blockTypeToElementInfo.put(BlockType.BULLETED_LIST,new ElementInfo("ul"));
		blockTypeToElementInfo.put(BlockType.CODE,new ElementInfo("code" ));
		blockTypeToElementInfo.put(BlockType.FOOTNOTE,new ElementInfo("footnote" ));
		blockTypeToElementInfo.put(BlockType.LIST_ITEM,new ElementInfo("li"));
		blockTypeToElementInfo.put(BlockType.NUMERIC_LIST,new ElementInfo("ol"));
		blockTypeToElementInfo.put(BlockType.DEFINITION_LIST,new ElementInfo("dl"));
		blockTypeToElementInfo.put(BlockType.DEFINITION_TERM,new ElementInfo("dt"));
		blockTypeToElementInfo.put(BlockType.DEFINITION_ITEM,new ElementInfo("dd"));
		blockTypeToElementInfo.put(BlockType.PARAGRAPH,new ElementInfo("p" ));
		blockTypeToElementInfo.put(BlockType.PREFORMATTED,new ElementInfo("pre" ));
		blockTypeToElementInfo.put(BlockType.QUOTE,new ElementInfo("blockquote" ));
		blockTypeToElementInfo.put(BlockType.TABLE,new ElementInfo("table" ));
		blockTypeToElementInfo.put(BlockType.TABLE_CELL_HEADER,new ElementInfo("th" ));
		blockTypeToElementInfo.put(BlockType.TABLE_CELL_NORMAL,new ElementInfo("td" ));
		blockTypeToElementInfo.put(BlockType.TABLE_ROW,new ElementInfo("tr"));
		blockTypeToElementInfo.put(BlockType.TIP,new ElementInfo("div","tip","border: 1px solid #090;background-color: #dfd;margin: 20px;padding: 0px 6px 0px 6px;"));
		blockTypeToElementInfo.put(BlockType.WARNING,new ElementInfo("div","warning","border: 1px solid #c00;background-color: #fcc;margin: 20px;padding: 0px 6px 0px 6px;"));
		blockTypeToElementInfo.put(BlockType.INFORMATION,new ElementInfo("div","info","border: 1px solid #3c78b5;background-color: #D8E4F1;margin: 20px;padding: 0px 6px 0px 6px;"));
		blockTypeToElementInfo.put(BlockType.NOTE,new ElementInfo("div","note","border: 1px solid #F0C000;background-color: #FFFFCE;margin: 20px;padding: 0px 6px 0px 6px;"));
		blockTypeToElementInfo.put(BlockType.PANEL,new ElementInfo("div","panel","border: 1px solid #ccc;background-color: #FFFFCE;margin: 10px;padding: 0px 6px 0px 6px;"));

	}


	private String htmlNsUri = "http://www.w3.org/1999/xhtml";
	private String htmlDtd = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
	
	
	private boolean xhtmlStrict = false;
	private boolean emitAsDocument = true;
	private boolean emitDtd = false;
	private String title;

	private String defaultAbsoluteLinkTarget;

	private List<Stylesheet> stylesheets = null;

	private boolean useInlineStyles = true;
	private boolean suppressBuiltInStyles = false;
	
	public HtmlDocumentBuilder(Writer out) {
		this(out,false);
	}
	
	public HtmlDocumentBuilder(Writer out,boolean formatting) {
		super(formatting?createFormattingXmlStreamWriter(out):new DefaultXmlStreamWriter(out));
	}

	public HtmlDocumentBuilder(XmlStreamWriter writer) {
		super(writer);
	}

	
	public void copyConfiguration(HtmlDocumentBuilder other) {
		other.setBase(getBase());
		other.setBaseInHead(isBaseInHead());
		other.setDefaultAbsoluteLinkTarget(getDefaultAbsoluteLinkTarget());
		other.setEmitAsDocument(isEmitAsDocument());
		other.setEmitDtd(isEmitDtd());
		other.setHtmlDtd(getHtmlDtd());
		other.setHtmlNsUri(getHtmlNsUri());
		other.setTitle(getTitle());
		other.setUseInlineStyles(isUseInlineStyles());
		other.setSuppressBuiltInStyles(isSuppressBuiltInStyles());
		if (stylesheets != null) {
			for (Stylesheet stylesheet: stylesheets) {
				other.stylesheets.add(stylesheet);
			}
		}
	}
	
	protected static XmlStreamWriter createFormattingXmlStreamWriter(Writer out) {
		return new FormattingXMLStreamWriter(new DefaultXmlStreamWriter(out)) {
			@Override
			protected boolean preserveWhitespace(String elementName) {
				return elementName.equals("pre") || elementName.equals("code");
			}
		};
	}

	/**
	 * The XML Namespace URI of the HTML elements, only used if {@link #isEmitAsDocument()}.
	 * The default value is "<code>http://www.w3.org/1999/xhtml</code>".
	 */
	public String getHtmlNsUri() {
		return htmlNsUri;
	}

	/**
	 * The XML Namespace URI of the HTML elements, only used if {@link #isEmitAsDocument()}.
	 * The default value is "<code>http://www.w3.org/1999/xhtml</code>".
	 */
	public void setHtmlNsUri(String htmlNsUri) {
		this.htmlNsUri = htmlNsUri;
	}

	/**
	 * The DTD to emit, if {@link #isEmitDtd()} and {@link #isEmitAsDocument()}.
	 * The default value is <code>&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;</code>
	 */
	public String getHtmlDtd() {
		return htmlDtd;
	}

	/**
	 * The DTD to emit, if {@link #isEmitDtd()} and {@link #isEmitAsDocument()}.
	 * @see #getHtmlDtd()
	 */
	public void setHtmlDtd(String htmlDtd) {
		this.htmlDtd = htmlDtd;
	}

	/**
	 * Indicate if the resulting HTML should be emitted as a document.  If false, the html and body tags are not included
	 * in the output.
	 * Default value is true.
	 */
	public boolean isEmitAsDocument() {
		return emitAsDocument;
	}

	/**
	 * Indicate if the resulting HTML should be emitted as a document.  If false, the html and body tags are not included
	 * in the output.
	 * Default value is true.
	 */
	public void setEmitAsDocument(boolean emitAsDocument) {
		this.emitAsDocument = emitAsDocument;
	}

	/**
	 * Indicate if the resulting HTML should include a DTD.  Ignored unless {@link #isEmitAsDocument()}.
	 * Default value is false.
	 */
	public boolean isEmitDtd() {
		return emitDtd;
	}

	/**
	 * Indicate if the resulting HTML should include a DTD.  Ignored unless {@link #isEmitAsDocument()}.
	 * Default value is false.
	 */
	public void setEmitDtd(boolean emitDtd) {
		this.emitDtd = emitDtd;
	}


	/**
	 * Set the document title, which will be emitted into the &lt;title&gt; element.
	 * Ignored unless {@link #isEmitAsDocument()}
	 * 
	 * @return the title or null if there is none
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the document title, which will be emitted into the &lt;title&gt; element.
	 * Ignored unless {@link #isEmitAsDocument()}
	 * 
	 * @param title the title or null if there is none
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * A default target attribute for links that have absolute (not relative) urls.
	 * By default this value is null.  Setting this value will cause all HTML anchors
	 * to have their target attribute set if it's not explicitly specified in a {@link LinkAttributes}.
	 */
	public String getDefaultAbsoluteLinkTarget() {
		return defaultAbsoluteLinkTarget;
	}

	/**
	 * A default target attribute for links that have absolute (not relative) urls.
	 * By default this value is null.  Setting this value will cause all HTML anchors
	 * to have their target attribute set if it's not explicitly specified in a {@link LinkAttributes}.
	 */
	public void setDefaultAbsoluteLinkTarget(String defaultAbsoluteLinkTarget) {
		this.defaultAbsoluteLinkTarget = defaultAbsoluteLinkTarget;
	}


	/**
	 * indicate if the builder should attempt to conform to strict XHTML rules.
	 * The default is false.
	 */
	public boolean isXhtmlStrict() {
		return xhtmlStrict;
	}

	/**
	 * indicate if the builder should attempt to conform to strict XHTML rules.
	 * The default is false.
	 */
	public void setXhtmlStrict(boolean xhtmlStrict) {
		this.xhtmlStrict = xhtmlStrict;
	}

	
	/**
	 * Add a CSS stylesheet to the output document as an URL, where the CSS stylesheet
	 * is referenced as an HTML link.
	 * Calling this method after {@link #beginDocument() starting the document}
	 * has no effect.
	 * 
	 * Generates code similar to the following:
	 * <code>
	 *   &lt;link type="text/css" rel="stylesheet" href="url"/>
	 * </code>
	 * 
	 * @param url the CSS url to use, which may be relative or absolute
	 * 
	 * @see #addCssStylesheet(File)
	 */
	public void addCssStylesheet(String url) {
		addStylesheet(new Stylesheet(url));
	}

	private void addStylesheet(Stylesheet stylesheet) {
		if (stylesheets == null) {
			stylesheets = new ArrayList<Stylesheet>();
		}
		stylesheets.add(stylesheet);
	}

	/**
	 * Add a CSS stylesheet to the output document, where the contents of the CSS stylesheet
	 * are embedded in the HTML.
	 * Calling this method after {@link #beginDocument() starting the document}
	 * has no effect.
	 * 
	 * Generates code similar to the following:
	 * <pre><code>
	 *   &lt;style type="text/css">
	 *   ... contents of the file ...
	 *   &lt;/style>
	 * </code></pre>
	 * 
	 * 
	 * @param file
	 * 
	 * @see #addCssStylesheet(String)
	 */
	public void addCssStylesheet(File file) {
		if (file == null) {
			throw new IllegalArgumentException();
		}
		if (!file.exists()) {
			throw new IllegalArgumentException(String.format("File does not exist: %s",file));
		}
		if (!file.isFile()) {
			throw new IllegalArgumentException(String.format("Not a file: %s",file));
		}
		if (!file.canRead()) {
			throw new IllegalArgumentException(String.format("File cannot be read: %s",file));
		}
		addStylesheet(new Stylesheet(file));
	}
	

	/**
	 * Indicate if inline styles should be used when creating output such as text boxes.
	 * When disabled inline styles are suppressed and CSS classes are used instead, with the default styles emitted as a
	 * stylesheet in the document head.  If disabled and {@link #isEmitAsDocument()} is false, this option has the same effect as {@link #isSuppressBuiltInStyles()}.
	 * 
	 * The default is true.
	 * 
	 * @see #isSuppressBuiltInStyles()
	 */
	public boolean isUseInlineStyles() {
		return useInlineStyles;
	}

	/**
	 * Indicate if inline styles should be used when creating output such as text boxes.
	 * When disabled inline styles are suppressed and CSS classes are used instead, with the default styles emitted as a
	 * stylesheet in the document head.  If disabled and {@link #isEmitAsDocument()} is false, this option has the same effect as {@link #isSuppressBuiltInStyles()}.
	 * 
	 * The default is true.
	 */
	public void setUseInlineStyles(boolean useInlineStyles) {
		this.useInlineStyles = useInlineStyles;
	}

	/**
	 * indicate if default built-in CSS styles should be suppressed.  Built-in styles are styles that are emitted by this builder
	 * to create the desired visual effect when rendering certain types of elements, such as warnings or infos.
	 * the default is false.
	 * 
	 * @see #isUseInlineStyles()
	 */
	public boolean isSuppressBuiltInStyles() {
		return suppressBuiltInStyles;
	}

	/**
	 * indicate if default built-in CSS styles should be suppressed.  Built-in styles are styles that are emitted by this builder
	 * to create the desired visual effect when rendering certain types of elements, such as warnings or infos.
	 * the default is false.
	 */
	public void setSuppressBuiltInStyles(boolean suppressBuiltInStyles) {
		this.suppressBuiltInStyles = suppressBuiltInStyles;
	}

	@Override
	public void beginDocument() {
		writer.setDefaultNamespace(htmlNsUri);

		if (emitAsDocument) {
			writer.writeStartDocument();

			if (emitDtd && htmlDtd != null) {
				writer.writeDTD(htmlDtd);
			}

			writer.writeStartElement(htmlNsUri,"html");
			writer.writeDefaultNamespace(htmlNsUri);

			writer.writeStartElement(htmlNsUri,"head");
			if (base != null && baseInHead) {
				writer.writeEmptyElement(htmlNsUri,"base");
				writer.writeAttribute("href", base.toString());
			}
			if (title != null) {
				writer.writeStartElement(htmlNsUri,"title");
				writer.writeCharacters(title);
				writer.writeEndElement(); // title
			}
			if (!useInlineStyles && !suppressBuiltInStyles) {
				writer.writeStartElement(htmlNsUri,"style");
				writer.writeAttribute("type", "text/css");
				writer.writeCharacters("\n");
				for (Entry<BlockType, ElementInfo> ent: blockTypeToElementInfo.entrySet()) {
					ElementInfo elementInfo = ent.getValue();
					if (elementInfo.cssStyles != null && elementInfo.cssClass != null) {
						String[] classes = elementInfo.cssClass.split("\\s+");
						for (String cssClass: classes) {
							writer.writeCharacters(".");
							writer.writeCharacters(cssClass);
							writer.writeCharacters(" ");
						}
						writer.writeCharacters("{");
						writer.writeCharacters(elementInfo.cssStyles);
						writer.writeCharacters("}\n");
					}
				}
				writer.writeEndElement();
			}
			if (stylesheets != null) {
				for (Stylesheet stylesheet: stylesheets) {
					if (stylesheet.url != null) {
						// <link type="text/css" rel="stylesheet" href="url"/>
						writer.writeEmptyElement(htmlNsUri,"link");
						writer.writeAttribute("type", "text/css");
						writer.writeAttribute("rel", "stylesheet");
						writer.writeAttribute("href", makeUrlAbsolute(stylesheet.url));
					} else {
						//						 <style type="text/css">
						//						   ... contents of the file ...
						//						 </style>
						writer.writeStartElement(htmlNsUri,"style");
						writer.writeAttribute("type", "text/css");
						String css;
						try {
							css = readFully(stylesheet.file);
						} catch (IOException e) {
							throw new IllegalStateException(String.format("Cannot read file: %s",stylesheet.file),e);
						}
						writer.writeCharacters(css);
						writer.writeEndElement();
					}
				}
			}
			writer.writeEndElement(); // head

			writer.writeStartElement(htmlNsUri,"body");
		} else {
			// sanity check
			if (stylesheets != null && !stylesheets.isEmpty()) {
				throw new IllegalStateException("CSS stylesheets are specified but the HTML output is not a document");
			}
		}
	}


	@Override
	public void endDocument() {
		if (emitAsDocument) {
			writer.writeEndElement(); // body
			writer.writeEndElement(); // html
			writer.writeEndDocument();
		}

		writer.close();
	}

	@Override
	public void entityReference(String entity) {
		writer.writeEntityRef(entity);
	}

	@Override
	public void acronym(String text, String definition) {
		writer.writeStartElement(htmlNsUri,"acronym");
		writer.writeAttribute("title", definition);
		writer.writeCharacters(text);
		writer.writeEndElement();
	}

	@Override
	public void link(Attributes attributes,String hrefOrHashName, String text) {
		writer.writeStartElement(htmlNsUri,"a");
		writer.writeAttribute("href", makeUrlAbsolute(hrefOrHashName));
		applyLinkAttributes(attributes,hrefOrHashName);
		characters(text);
		writer.writeEndElement(); // a
	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		ElementInfo elementInfo = blockTypeToElementInfo.get(type);
		if (elementInfo == null) {
			throw new IllegalStateException(type.name());
		}
		writer.writeStartElement(htmlNsUri,elementInfo.name);
		if (elementInfo.cssClass != null) {
			if (attributes.getCssClass() == null) {
				attributes.setCssClass(elementInfo.cssClass);
			} else {
				attributes.setCssClass(elementInfo.cssClass+' '+attributes.getCssClass());
			}
		}
		if (useInlineStyles && !suppressBuiltInStyles && elementInfo.cssStyles != null) {
			if (attributes.getCssStyle() == null) {
				attributes.setCssStyle(elementInfo.cssStyles);
			} else {
				attributes.setCssStyle(elementInfo.cssStyles+attributes.getCssStyle());
			}
		}
		if (type == BlockType.TABLE) {
			applyTableAttributes(attributes);
		} else if (type == BlockType.TABLE_ROW) {
			applyTableRowAttributes(attributes);
		} else if (type == BlockType.TABLE_CELL_HEADER || type == BlockType.TABLE_CELL_NORMAL) {
			applyCellAttributes(attributes);
		} else if (type == BlockType.BULLETED_LIST || type == BlockType.NUMERIC_LIST) {
			applyListAttributes(attributes);
		} else if (type == BlockType.QUOTE) {
			applyQuoteAttributes(attributes);
		} else {
			applyAttributes(attributes);

			// create the titled panel effect if a title is specified
			if (attributes.getTitle() != null) {
				beginBlock(BlockType.PARAGRAPH, new Attributes());
				beginSpan(SpanType.BOLD, new Attributes());
				characters(attributes.getTitle());
				endSpan();
				endBlock();
			}
		}
	}


	@Override
	public void beginHeading(int level, Attributes attributes) {
		if (level > 6) {
			level = 6;
		}
		writer.writeStartElement(htmlNsUri,"h"+level);
		applyAttributes(attributes);
	}

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
		String elementName = spanTypeToElementName.get(type);
		if (elementName == null) {
			throw new IllegalStateException(type.name());
		}
		writer.writeStartElement(htmlNsUri,elementName);
		applyAttributes(attributes);
	}


	@Override
	public void endBlock() {
		writer.writeEndElement();
	}

	@Override
	public void endHeading() {
		writer.writeEndElement();
	}

	@Override
	public void endSpan() {
		writer.writeEndElement();
	}

	@Override
	public void image(Attributes attributes, String url) {
		writer.writeEmptyElement(htmlNsUri,"img");
		applyImageAttributes(attributes);
		writer.writeAttribute("src", makeUrlAbsolute(url));
	}


	private void applyListAttributes(Attributes attributes) {
		applyAttributes(attributes);
		if (attributes instanceof ListAttributes) {
			ListAttributes listAttributes = (ListAttributes) attributes;
			if (listAttributes.getStart() != null) {
				writer.writeAttribute("start", listAttributes.getStart());
			}
		}
	}


	private void applyQuoteAttributes(Attributes attributes) {
		applyAttributes(attributes);
		if (attributes instanceof QuoteAttributes) {
			QuoteAttributes quoteAttributes = (QuoteAttributes) attributes;
			if (quoteAttributes.getCitation() != null) {
				writer.writeAttribute("cite", quoteAttributes.getCitation());
			}
		}
	}

	private void applyTableAttributes(Attributes attributes) {
		applyAttributes(attributes);
		if (attributes.getTitle() != null) {
			writer.writeAttribute("title", attributes.getTitle());
		}
		if (attributes instanceof TableAttributes) {
			TableAttributes tableAttributes = (TableAttributes) attributes;
			if (tableAttributes.getBgcolor() != null) {
				writer.writeAttribute("bgcolor", tableAttributes.getBgcolor());
			}
			if (tableAttributes.getBorder() != null) {
				writer.writeAttribute("border", tableAttributes.getBorder());
			}
			if (tableAttributes.getCellpadding() != null) {
				writer.writeAttribute("cellpadding", tableAttributes.getCellpadding());
			}
			if (tableAttributes.getCellspacing() != null) {
				writer.writeAttribute("cellspacing", tableAttributes.getCellspacing());
			}
			if (tableAttributes.getFrame() != null) {
				writer.writeAttribute("frame", tableAttributes.getFrame());
			}
			if (tableAttributes.getRules() != null) {
				writer.writeAttribute("rules", tableAttributes.getRules());
			}
			if (tableAttributes.getSummary() != null) {
				writer.writeAttribute("summary", tableAttributes.getSummary());
			}
			if (tableAttributes.getWidth() != null) {
				writer.writeAttribute("width", tableAttributes.getWidth());
			}
		}
	}
	private void applyTableRowAttributes(Attributes attributes) {
		applyAttributes(attributes);
		if (attributes.getTitle() != null) {
			writer.writeAttribute("title", attributes.getTitle());
		}
		if (attributes instanceof TableRowAttributes) {
			TableRowAttributes tableRowAttributes = (TableRowAttributes) attributes;
			if (tableRowAttributes.getBgcolor() != null) {
				writer.writeAttribute("bgcolor", tableRowAttributes.getBgcolor());
			}
			if (tableRowAttributes.getAlign() != null) {
				writer.writeAttribute("align", tableRowAttributes.getAlign());
			}
			if (tableRowAttributes.getValign() != null) {
				writer.writeAttribute("valign", tableRowAttributes.getValign());
			}
		}
	}


	private void applyCellAttributes(Attributes attributes) {
		applyAttributes(attributes);
		if (attributes.getTitle() != null) {
			writer.writeAttribute("title", attributes.getTitle());
		}

		if (attributes instanceof TableCellAttributes) {
			TableCellAttributes tableCellAttributes = (TableCellAttributes) attributes;
			if (tableCellAttributes.getBgcolor() != null) {
				writer.writeAttribute("bgcolor", tableCellAttributes.getBgcolor());
			}
			if (tableCellAttributes.getAlign() != null) {
				writer.writeAttribute("align", tableCellAttributes.getAlign());
			}
			if (tableCellAttributes.getValign() != null) {
				writer.writeAttribute("valign", tableCellAttributes.getValign());
			}
			if (tableCellAttributes.getRowspan() != null) {
				writer.writeAttribute("rowspan", tableCellAttributes.getRowspan());
			}
			if (tableCellAttributes.getColspan() != null) {
				writer.writeAttribute("colspan", tableCellAttributes.getColspan());
			}
		}
	}

	private void applyImageAttributes(Attributes attributes) {
		int border = 0;
		Align align = null;
		if (attributes instanceof ImageAttributes) {
			ImageAttributes imageAttributes = (ImageAttributes) attributes;
			border = imageAttributes.getBorder();
			align = imageAttributes.getAlign();
		}
		if (xhtmlStrict) {
			String borderStyle = String.format("border-width: %spx;",border);
			String alignStyle = null;
			if (align != null) {
				switch (align) {
				case Center:
				case Right:
				case Left:
					alignStyle = "text-align: "+align.name().toLowerCase()+";";
					break;
					// TODO: determine CSS equivalent to image align attributes
				}
			}
			String additionalStyles = borderStyle;
			if (alignStyle != null) {
				additionalStyles += alignStyle;
			}
			if (attributes.getCssStyle() == null || attributes.getCssStyle().length() == 0) {
				attributes.setCssStyle(additionalStyles);
			} else {
				attributes.setCssStyle(additionalStyles+attributes.getCssStyle());
			}
		}
		applyAttributes(attributes);
		boolean haveAlt = false;
		
		if (attributes instanceof ImageAttributes) {
			ImageAttributes imageAttributes = (ImageAttributes) attributes;
			if (imageAttributes.getHeight() != -1) {
				writer.writeAttribute("height", Integer.toString(imageAttributes.getHeight()));
			}
			if (imageAttributes.getWidth() != -1) {
				writer.writeAttribute("width", Integer.toString(imageAttributes.getWidth()));
			}
			if (!xhtmlStrict && align != null) {
				writer.writeAttribute("align", align.name().toLowerCase());
			}
			if (imageAttributes.getAlt() != null) {
				haveAlt = true;
				writer.writeAttribute("alt", imageAttributes.getAlt());
			}
		}
		if (attributes.getTitle() != null) {
			writer.writeAttribute("title", attributes.getTitle());
			if (!haveAlt) {
				haveAlt = true;
				writer.writeAttribute("alt", attributes.getTitle());
			}
		}
		if (xhtmlStrict) {
			if (!haveAlt) {
				// XHTML requires img/@alt
				writer.writeAttribute("alt", "");
			}
		} else {
			// only specify border attribute if it's not already specified in CSS
			writer.writeAttribute("border",Integer.toString(border));
		}
	}

	private void applyLinkAttributes(Attributes attributes, String href) {
		applyAttributes(attributes);
		boolean hasTarget = false;
		if (attributes instanceof LinkAttributes) {
			LinkAttributes linkAttributes = (LinkAttributes) attributes;
			if (linkAttributes.getTarget() != null) {
				hasTarget = true;
				writer.writeAttribute("target", linkAttributes.getTarget());
			}
		}
		if (attributes.getTitle() != null && attributes.getTitle().length() > 0) {
			writer.writeAttribute("title", attributes.getTitle());
		}
		if (!hasTarget && defaultAbsoluteLinkTarget != null && href != null) {
			if (isExternalLink(href)) {
				writer.writeAttribute("target", defaultAbsoluteLinkTarget);
			}
		}
	}

	/**
	 * Note: this method does not apply the {@link Attributes#getTitle() title}.
	 */
	private void applyAttributes(Attributes attributes) {
		if (attributes.getId() != null) {
			writer.writeAttribute("id", attributes.getId());
		}
		if (attributes.getCssClass() != null) {
			writer.writeAttribute("class", attributes.getCssClass());
		}
		if (attributes.getCssStyle() != null) {
			writer.writeAttribute("style", attributes.getCssStyle());
		}
		if (attributes.getLanguage() != null) {
			writer.writeAttribute("lang", attributes.getLanguage());
		}
	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		writer.writeStartElement(htmlNsUri,"a");
		writer.writeAttribute("href", makeUrlAbsolute(href));
		applyLinkAttributes(linkAttributes,href);
		writer.writeEmptyElement(htmlNsUri,"img");
		applyImageAttributes(imageAttributes);
		writer.writeAttribute("src", makeUrlAbsolute(imageUrl));
		writer.writeEndElement(); // a
	}

	@Override
	public void lineBreak() {
		writer.writeEmptyElement(htmlNsUri,"br");
	}

	@Override
	public void charactersUnescaped(String literal) {
		writer.writeLiteral(literal);
	}

	private static final class ElementInfo {
		final String name;
		final String cssClass;
		final String cssStyles;

		public ElementInfo(String name, String cssClass, String cssStyles) {
			this.name = name;
			this.cssClass = cssClass;
			this.cssStyles = cssStyles != null && !cssStyles.endsWith(";")?cssStyles+';':cssStyles;
		}

		public ElementInfo(String name) {
			this(name,null,null);
		}
	}

	private static class Stylesheet {
		String url;
		File file;
		public Stylesheet(File file) {
			this.file = file;
		}
		public Stylesheet(String url) {
			this.url = url;
		}
	}


	private static String readFully(File inputFile) throws IOException {
		int length = (int) inputFile.length();
		if (length <= 0) {
			length = 2048;
		}
		StringBuilder buf = new StringBuilder(length);
		Reader reader = new BufferedReader(new FileReader(inputFile));
		try {
			int c;
			while ((c = reader.read()) != -1) {
				buf.append((char)c);
			}
		} finally {
			reader.close();
		}
		return buf.toString();
	}


}
