/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.eclipse.mylyn.internal.wikitext.core.util.css.CssParser;
import org.eclipse.mylyn.internal.wikitext.core.util.css.CssRule;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.ListAttributes;
import org.eclipse.mylyn.wikitext.core.parser.TableAttributes;
import org.eclipse.mylyn.wikitext.core.util.XmlStreamWriter;

/**
 * A document builder that produces XSL-FO output. XSL-FO is suitable for conversion to other formats such as PDF.
 * 
 * 
 * @see <a href="http://www.w3.org/TR/2001/REC-xsl-20011015/">XSL-FO 1.0 specification</a>
 * @see <a href="http://en.wikipedia.org/wiki/XSL_Formatting_Objects">XSL-FO (WikiPedia)</a>
 * @see <a href="http://www.w3schools.com/xslfo/default.asp">XSL-FO Tutorial</a>
 * 
 * @author David Green
 * 
 * @since 1.1
 */
public class XslfoDocumentBuilder extends AbstractXmlDocumentBuilder {

	private static final String CSS_RULE_BACKGROUND_COLOR = "background-color"; //$NON-NLS-1$

	private static final String CSS_RULE_COLOR = "color"; //$NON-NLS-1$

	private static final String CSS_RULE_VERTICAL_ALIGN = "vertical-align"; //$NON-NLS-1$

	private static final String CSS_RULE_TEXT_DECORATION = "text-decoration"; //$NON-NLS-1$

	private static final String CSS_RULE_FONT_FAMILY = "font-family"; //$NON-NLS-1$

	private static final String CSS_RULE_FONT_SIZE = "font-size"; //$NON-NLS-1$

	private static final String CSS_RULE_FONT_WEIGHT = "font-weight"; //$NON-NLS-1$

	private static final String CSS_RULE_FONT_STYLE = "font-style"; //$NON-NLS-1$

	private static final char[] BULLET_CHARS = new char[] { '\u2022' };

	private static Map<BlockType, String> blockTypeToCssStyles = new HashMap<BlockType, String>();
	static {
		blockTypeToCssStyles.put(BlockType.CODE, "font-family: monospace;"); //$NON-NLS-1$
		blockTypeToCssStyles.put(BlockType.PREFORMATTED, "font-family: monospace;"); //$NON-NLS-1$
		blockTypeToCssStyles.put(BlockType.TABLE_CELL_HEADER, "font-weight: bold;"); //$NON-NLS-1$
	}

	private static Map<SpanType, String> spanTypeToCssStyles = new HashMap<SpanType, String>();
	static {
		spanTypeToCssStyles.put(SpanType.STRONG, "font-weight: bold;"); //$NON-NLS-1$
		spanTypeToCssStyles.put(SpanType.BOLD, "font-weight: bold;"); //$NON-NLS-1$
		spanTypeToCssStyles.put(SpanType.MONOSPACE, "font-family: monospace;"); //$NON-NLS-1$
		spanTypeToCssStyles.put(SpanType.CODE, "font-family: monospace;"); //$NON-NLS-1$
		spanTypeToCssStyles.put(SpanType.CITATION, "font-style: italic;"); //$NON-NLS-1$
		spanTypeToCssStyles.put(SpanType.EMPHASIS, "font-style: italic;"); //$NON-NLS-1$
		spanTypeToCssStyles.put(SpanType.ITALIC, "font-style: italic;"); //$NON-NLS-1$
		spanTypeToCssStyles.put(SpanType.DELETED, "text-decoration: line-through;"); //$NON-NLS-1$
		spanTypeToCssStyles.put(SpanType.INSERTED, "text-decoration: underline;"); //$NON-NLS-1$
		spanTypeToCssStyles.put(SpanType.UNDERLINED, "text-decoration: underline;"); //$NON-NLS-1$
		spanTypeToCssStyles.put(SpanType.SUBSCRIPT, "vertical-align: sub;"); //$NON-NLS-1$
		spanTypeToCssStyles.put(SpanType.SUPERSCRIPT, "vertical-align: super;"); //$NON-NLS-1$
	}

	private final String foNamespaceUri = "http://www.w3.org/1999/XSL/Format"; //$NON-NLS-1$

	private boolean pageBreakOnHeading1 = true;

	private boolean pageOpen = false;

	private int h1Count = 0;

	private float[] fontSizes = new float[] { 12.0f, 18.0f, 15.0f, 13.2f, 12.0f, 10.4f, 8.0f };

	private final float[] fontSizeMultipliers = new float[] { 1.0f, 1.5f, 1.25f, 1.1f, 1.0f, 0.83f, 0.67f };

	private final Stack<ElementInfo> elementInfos = new Stack<ElementInfo>();

	private boolean showExternalLinks = true;

	private boolean underlineLinks = false;

	private boolean panelText = true;

	private String title;

	private String subTitle;

	private String version;

	public XslfoDocumentBuilder(Writer out) {
		super(out);
	}

	public XslfoDocumentBuilder(XmlStreamWriter writer) {
		super(writer);
		setFontSize(10.0f);
	}

	/**
	 * Set the base font size. The base font size is 10.0 by default
	 */
	public void setFontSize(float fontSize) {
		fontSizes = new float[fontSizeMultipliers.length];
		for (int x = 0; x < fontSizeMultipliers.length; ++x) {
			fontSizes[x] = fontSizeMultipliers[x] * fontSize;
		}
	}

	/**
	 * Get the base font size. The base font size is 10.0 by default
	 */
	public float getFontSize() {
		return fontSizes[0];
	}

	/**
	 * Set the font size multipliers. Multipliers are used to determine the actual size of fonts by multiplying the
	 * {@link #getFontSize() base font size} by the multiplier to determine the size of a font for a heading.
	 * 
	 * @param fontSizeMultipliers
	 *            an array of size 7, where position 1-6 correspond to headings h1 to h6
	 */
	public void setFontSizeMultipliers(float[] fontSizeMultipliers) {
		if (fontSizeMultipliers.length != 7) {
			throw new IllegalArgumentException();
		}
		for (int x = 0; x < fontSizeMultipliers.length; ++x) {
			if (fontSizeMultipliers[x] < 0.2) {
				throw new IllegalArgumentException();
			}
		}
		System.arraycopy(fontSizeMultipliers, 0, this.fontSizeMultipliers, 0, 7);
	}

	/**
	 * The font size multipliers. Multipliers are used to determine the actual size of fonts by multiplying the
	 * {@link #getFontSize() base font size} by the multiplier to determine the size of a font for a heading.
	 * 
	 * @return an array of size 7, where position 1-6 correspond to headings h1 to h6
	 */
	public float[] getFontSizeMultipliers() {
		float[] values = new float[7];
		System.arraycopy(this.fontSizeMultipliers, 0, values, 0, 7);
		return values;
	}

	@Override
	public void acronym(String text, String definition) {
		characters(text);
	}

	private static class ElementInfo {
		int size = 1;

	}

	private static class SpanInfo extends ElementInfo {
		final SpanType type;

		public SpanInfo(SpanType type) {
			super();
			this.type = type;
		}
	}

	private static class BlockInfo extends ElementInfo {
		final BlockType type;

		int listItemCount;

		BlockInfo previousChild;

		public BlockInfo(BlockType type) {
			this.type = type;
		}

	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		BlockInfo thisInfo = new BlockInfo(type);
		BlockInfo parentBlock = findCurrentBlock();

		String cssStyles = blockTypeToCssStyles.get(type);
		Map<String, String> attrs = cssStyles == null ? null : attributesFromCssStyles(cssStyles);
		if (attributes.getCssStyle() != null) {
			Map<String, String> otherAttrs = attributesFromCssStyles(attributes.getCssStyle());
			if (attrs == null) {
				attrs = otherAttrs;
			} else if (!otherAttrs.isEmpty()) {
				attrs.putAll(otherAttrs);
			}
		}

		switch (type) {
		case DEFINITION_LIST:
		case BULLETED_LIST:
		case NUMERIC_LIST:
			writer.writeStartElement(foNamespaceUri, "list-block"); //$NON-NLS-1$
			writer.writeAttribute("provisional-label-separation", "0.2em"); //$NON-NLS-1$ //$NON-NLS-2$
			writer.writeAttribute("provisional-distance-between-starts", "1.2em"); //$NON-NLS-1$ //$NON-NLS-2$
			if (findBlockInfo(BlockType.LIST_ITEM) == null) {
				addSpaceBefore();
			}
			break;
		case DEFINITION_ITEM:
			if (parentBlock == null || parentBlock.type != BlockType.DEFINITION_LIST) {
				throw new IllegalStateException();
			}
			boolean firstItem = false;
			if (parentBlock.previousChild != null && parentBlock.previousChild.type == BlockType.DEFINITION_TERM) {
				firstItem = true;
				writer.writeEndElement(); // list-item-label
				--parentBlock.size;

				writer.writeStartElement(foNamespaceUri, "list-item-body"); //$NON-NLS-1$
				++parentBlock.size;
				writer.writeAttribute("start-indent", "body-start()"); //$NON-NLS-1$ //$NON-NLS-2$
				writer.writeEmptyElement(foNamespaceUri, "block"); //$NON-NLS-1$
			}
			writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
			if (attrs == null || !attrs.containsKey("font-size")) { //$NON-NLS-1$
				configureFontSize(0);
			}
			writer.writeAttribute("space-before", firstItem ? "1.2em" : "0.2em"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			break;
		case DEFINITION_TERM:
			if (parentBlock == null || parentBlock.type != BlockType.DEFINITION_LIST) {
				throw new IllegalStateException();
			}
			if (parentBlock.previousChild != null && parentBlock.previousChild.type == BlockType.DEFINITION_ITEM) {
				writer.writeEndElement(); // list-item-body
				--parentBlock.size;
				writer.writeEndElement(); // list-item
				--parentBlock.size;
			}
			if (parentBlock.previousChild == null || parentBlock.previousChild.type != BlockType.DEFINITION_TERM) {
				writer.writeStartElement(foNamespaceUri, "list-item"); //$NON-NLS-1$
				writer.writeAttribute("space-before", "0.2em"); //$NON-NLS-1$ //$NON-NLS-2$ 
				++parentBlock.size;
				writer.writeStartElement(foNamespaceUri, "list-item-label"); //$NON-NLS-1$
				writer.writeAttribute("end-indent", "label-end()"); //$NON-NLS-1$ //$NON-NLS-2$
				++parentBlock.size;
			}
			writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
			if (attrs == null || !attrs.containsKey("font-size")) { //$NON-NLS-1$
				configureFontSize(0);
			}
			if (attrs == null || !attrs.containsKey("font-weight")) { //$NON-NLS-1$
				writer.writeAttribute("font-weight", "bold"); //$NON-NLS-1$//$NON-NLS-2$
			}
			break;
		case LIST_ITEM:
			BlockInfo listInfo = getListBlockInfo();
			++listInfo.listItemCount;
			writer.writeStartElement(foNamespaceUri, "list-item"); //$NON-NLS-1$
//			addSpaceBefore();

			writer.writeStartElement(foNamespaceUri, "list-item-label"); //$NON-NLS-1$
			writer.writeAttribute("end-indent", "label-end()"); //$NON-NLS-1$ //$NON-NLS-2$
			writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
			configureFontSize(0);
			// FIXME: nested list numbering, list style
			if (listInfo.type == BlockType.NUMERIC_LIST) {
				if (attributes instanceof ListAttributes) {
					// start attribute
					ListAttributes listAttributes = (ListAttributes) attributes;
					if (listAttributes.getStart() != null) {
						try {
							thisInfo.listItemCount = Integer.parseInt(listAttributes.getStart(), 10) - 1;
						} catch (NumberFormatException e) {
							// ignore
						}
					}
				}
				writer.writeCharacters(String.format("%s.", listInfo.listItemCount)); //$NON-NLS-1$
			} else {
				writer.writeCharacters(BULLET_CHARS, 0, BULLET_CHARS.length);
			}
			writer.writeEndElement(); // block
			writer.writeEndElement(); // list-item-label

			writer.writeStartElement(foNamespaceUri, "list-item-body"); //$NON-NLS-1$
			++thisInfo.size;
			writer.writeAttribute("start-indent", "body-start()"); //$NON-NLS-1$ //$NON-NLS-2$
			writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
			configureFontSize(0);
			++thisInfo.size;

			break;
		case FOOTNOTE:
		case PARAGRAPH:
			writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
			if (attrs == null || !attrs.containsKey("font-size")) { //$NON-NLS-1$
				configureFontSize(0);
			}
			addSpaceBefore();
			break;
		case CODE:
		case PREFORMATTED:
			writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
			writer.writeAttribute("hyphenate", "false"); //$NON-NLS-1$ //$NON-NLS-2$
//			writer.writeAttribute("wrap-option", "no-wrap"); //$NON-NLS-1$ //$NON-NLS-2$
			writer.writeAttribute("white-space-collapse", "false"); //$NON-NLS-1$ //$NON-NLS-2$
			writer.writeAttribute("white-space-treatment", "preserve"); //$NON-NLS-1$ //$NON-NLS-2$
			writer.writeAttribute("linefeed-treatment", "preserve"); //$NON-NLS-1$ //$NON-NLS-2$
			writer.writeAttribute("text-align", "start"); //$NON-NLS-1$ //$NON-NLS-2$
			if (attrs == null || !attrs.containsKey("font-size")) { //$NON-NLS-1$
				configureFontSize(0);
			}
			addSpaceBefore();
			break;
		case QUOTE:
			writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
			if (attrs == null || !attrs.containsKey("font-size")) { //$NON-NLS-1$
				configureFontSize(0);
			}
			// indent
			indentLeftAndRight(attrs, "2em"); //$NON-NLS-1$
			addSpaceBefore();
			break;
		case DIV:
			writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
			if (attrs == null || !attrs.containsKey("font-size")) { //$NON-NLS-1$
				configureFontSize(0);
			}
			// no space before
			break;
		case INFORMATION:
		case NOTE:
		case TIP:
		case WARNING:
		case PANEL:
			writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
			if (attrs == null || !attrs.containsKey("font-size")) { //$NON-NLS-1$
				configureFontSize(0);
			}
			indentLeftAndRight(attrs, "2em"); //$NON-NLS-1$
			addSpaceBefore();

			// create the titled panel effect if a title is specified
			if (attributes.getTitle() != null || panelText) {
				writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
				if (panelText) {
					String text = null;
					switch (type) {
					case NOTE:
						text = Messages.getString("XslfoDocumentBuilder.Note"); //$NON-NLS-1$
						break;
					case TIP:
						text = Messages.getString("XslfoDocumentBuilder.Tip"); //$NON-NLS-1$
						break;
					case WARNING:
						text = Messages.getString("XslfoDocumentBuilder.Warning"); //$NON-NLS-1$
						break;
					}
					if (text != null) {
						writer.writeStartElement(foNamespaceUri, "inline"); //$NON-NLS-1$
						writer.writeAttribute("font-style", "italic"); //$NON-NLS-1$//$NON-NLS-2$
						characters(text);
						writer.writeEndElement(); // inline
					}
				}
				if (attributes.getTitle() != null) {
					writer.writeStartElement(foNamespaceUri, "inline"); //$NON-NLS-1$
					writer.writeAttribute("font-weight", "bold"); //$NON-NLS-1$//$NON-NLS-2$
					characters(attributes.getTitle());
					writer.writeEndElement(); // inline
				}
				writer.writeEndElement(); // block
			}

			break;
		case TABLE:
			writer.writeStartElement(foNamespaceUri, "table"); //$NON-NLS-1$
			applyTableAttributes(attributes);
			writer.writeStartElement(foNamespaceUri, "table-body"); //$NON-NLS-1$
			++thisInfo.size;
			break;
		case TABLE_CELL_HEADER:
		case TABLE_CELL_NORMAL:
			writer.writeStartElement(foNamespaceUri, "table-cell"); //$NON-NLS-1$
			writer.writeAttribute("padding-left", "2pt"); //$NON-NLS-1$ //$NON-NLS-2$
			writer.writeAttribute("padding-right", "2pt"); //$NON-NLS-1$ //$NON-NLS-2$
			writer.writeAttribute("padding-top", "2pt"); //$NON-NLS-1$ //$NON-NLS-2$
			writer.writeAttribute("padding-bottom", "2pt"); //$NON-NLS-1$ //$NON-NLS-2$
			writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
			if (attrs == null || !attrs.containsKey("font-size")) { //$NON-NLS-1$
				configureFontSize(0);
			}
			++thisInfo.size;
			break;
		case TABLE_ROW:
			writer.writeStartElement(foNamespaceUri, "table-row"); //$NON-NLS-1$
			break;
		default:
			throw new IllegalStateException(type.name());
		}

		if (attrs != null) {
			// output attributes with stable order
			for (Entry<String, String> ent : new TreeMap<String, String>(attrs).entrySet()) {
				writer.writeAttribute(ent.getKey(), ent.getValue());
			}
		}

		if (parentBlock != null) {
			parentBlock.previousChild = thisInfo;
		}
		elementInfos.push(thisInfo);
	}

	private void indentLeftAndRight(Map<String, String> attrs, String indentSize) {
		if (attrs == null || !attrs.containsKey("margin-left")) { //$NON-NLS-1$
			writer.writeAttribute("margin-left", indentSize); //$NON-NLS-1$
		}
		if (attrs == null || !attrs.containsKey("margin-right")) { //$NON-NLS-1$
			writer.writeAttribute("margin-right", indentSize); //$NON-NLS-1$
		}
	}

	private void configureFontSize(int level) {
		writer.writeAttribute("font-size", String.format("%spt", fontSizes[level])); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void addSpaceBefore() {
		writer.writeAttribute("space-before.optimum", "1em"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.writeAttribute("space-before.minimum", "0.8em"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.writeAttribute("space-before.maximum", "1.2em"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void endBlock() {
		ElementInfo elementInfo = elementInfos.pop();
		if (!(elementInfo instanceof BlockInfo)) {
			throw new IllegalStateException();
		}
		close(elementInfo);
	}

	private void close(ElementInfo elementInfo) {
		while (elementInfo.size > 0) {
			--elementInfo.size;
			writer.writeEndElement();
		}
	}

	private void applyTableAttributes(Attributes attributes) {
		// applyAttributes(attributes);

		boolean haveWidth = false;
		if (attributes instanceof TableAttributes) {
			TableAttributes tableAttributes = (TableAttributes) attributes;
			if (tableAttributes.getBgcolor() != null) {
				writer.writeAttribute(CSS_RULE_BACKGROUND_COLOR, tableAttributes.getBgcolor());
			}

			// FIXME border
			// if (tableAttributes.getBorder() != null) {
			//				writer.writeAttribute("border", tableAttributes.getBorder()); //$NON-NLS-1$
			// }
			// if (tableAttributes.getCellpadding() != null) {
			//				writer.writeAttribute("cellpadding", tableAttributes.getCellpadding()); //$NON-NLS-1$
			// }
			// if (tableAttributes.getCellspacing() != null) {
			//				writer.writeAttribute("cellspacing", tableAttributes.getCellspacing()); //$NON-NLS-1$
			// }
			// if (tableAttributes.getFrame() != null) {
			//				writer.writeAttribute("frame", tableAttributes.getFrame()); //$NON-NLS-1$
			// }
			// if (tableAttributes.getRules() != null) {
			//				writer.writeAttribute("rules", tableAttributes.getRules()); //$NON-NLS-1$
			// }
			// if (tableAttributes.getSummary() != null) {
			//				writer.writeAttribute("summary", tableAttributes.getSummary()); //$NON-NLS-1$
			// }
			if (tableAttributes.getWidth() != null) {
				writer.writeAttribute("width", tableAttributes.getWidth()); //$NON-NLS-1$
				haveWidth = true;
			}
		}
		// FIXME default border
		if (!haveWidth) {
			writer.writeAttribute("width", "auto"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		writer.writeAttribute("border-collapse", "collapse"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private BlockInfo getListBlockInfo() {
		for (int x = elementInfos.size() - 1; x >= 0; --x) {
			ElementInfo elementInfo = elementInfos.get(x);
			if (elementInfo instanceof BlockInfo) {
				BlockInfo info = (BlockInfo) elementInfo;
				if (info.type == BlockType.BULLETED_LIST || info.type == BlockType.NUMERIC_LIST
						|| info.type == BlockType.DEFINITION_LIST) {
					return info;
				}
			}
		}
		return null;
	}

	private BlockInfo findBlockInfo(BlockType type) {
		for (int x = elementInfos.size() - 1; x >= 0; --x) {
			ElementInfo elementInfo = elementInfos.get(x);
			if (elementInfo instanceof BlockInfo) {
				BlockInfo info = (BlockInfo) elementInfo;
				if (info.type == type) {
					return info;
				}
			}
		}
		return null;
	}

	private BlockInfo findCurrentBlock() {
		for (int x = elementInfos.size() - 1; x >= 0; --x) {
			ElementInfo elementInfo = elementInfos.get(x);
			if (elementInfo instanceof BlockInfo) {
				return (BlockInfo) elementInfo;
			}
		}
		return null;
	}

	@Override
	public void beginDocument() {
		writer.setDefaultNamespace(foNamespaceUri);

		writer.writeStartElement(foNamespaceUri, "root"); //$NON-NLS-1$
		writer.writeNamespace("", foNamespaceUri); //$NON-NLS-1$

		writer.writeStartElement(foNamespaceUri, "layout-master-set"); //$NON-NLS-1$
		writer.writeStartElement(foNamespaceUri, "simple-page-master"); //$NON-NLS-1$
		writer.writeAttribute("master-name", "page-layout"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.writeAttribute("page-height", "29.7cm"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.writeAttribute("page-width", "21.0cm"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.writeAttribute("margin", "2cm"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.writeEmptyElement(foNamespaceUri, "region-body"); //$NON-NLS-1$
		writer.writeEndElement(); // simple-page-master
		writer.writeEndElement(); // layout-master-set

		if (title != null) {
			emitTitlePage();
		}

		openPage();
		openFlow();
	}

	private void emitTitlePage() {
		openPage();
		openFlow();
		writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$

		if (title != null) {
			writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
			writer.writeAttribute("font-weight", "bold"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("font-size", "25pt"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("text-align", "center"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("space-before", "19pt"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeCharacters(title);
			writer.writeEndElement(); // block
		}

		if (subTitle != null) {
			writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
			writer.writeAttribute("font-weight", "bold"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("font-size", "18pt"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("text-align", "center"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("space-before", "15pt"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeCharacters(subTitle);
			writer.writeEndElement(); // block
		}

		if (version != null) {
			writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
			writer.writeAttribute("font-weight", "bold"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("font-size", "14pt"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("text-align", "center"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("space-before", "13pt"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeCharacters(version);
			writer.writeEndElement(); // block
		}

		writer.writeEmptyElement(foNamespaceUri, "block"); //$NON-NLS-1$
		writer.writeAttribute("break-after", "page"); //$NON-NLS-1$//$NON-NLS-2$
		writer.writeEndElement(); // block

		closeFlow();
		closePage();
	}

	private void openFlow() {
		writer.writeStartElement(foNamespaceUri, "flow"); //$NON-NLS-1$
		writer.writeAttribute("flow-name", "xsl-region-body"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void closeFlow() {
		writer.writeEndElement(); // flow
	}

	private void openPage() {
		pageOpen = true;
		writer.writeStartElement(foNamespaceUri, "page-sequence"); //$NON-NLS-1$
		writer.writeAttribute("master-reference", "page-layout"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void closePage() {
		writer.writeEndElement(); // page-sequence
		pageOpen = false;
	}

	@Override
	public void endDocument() {
		if (pageOpen) {
			closeFlow();
			closePage();
		}
		writer.writeEndElement(); // root
		writer.close();
	}

	@Override
	public void beginHeading(int level, Attributes attributes) {
		if (level == 1 && ++h1Count > 1 && pageBreakOnHeading1) {
			if (pageOpen) {
				closeFlow();
				closePage();
			}
			openPage();
			openFlow();
		}
		writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
		writer.writeAttribute("keep-with-next.within-column", "always"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.writeAttribute("font-weight", "bold"); //$NON-NLS-1$ //$NON-NLS-2$
		configureFontSize(level);
		writer.writeAttribute("space-before.optimum", "10pt"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.writeAttribute("space-before.minimum", "10pt * 0.8"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.writeAttribute("space-before.maximum", "10pt * 1.2"); //$NON-NLS-1$ //$NON-NLS-2$
		if (attributes.getId() != null) {
			writer.writeAttribute("id", attributes.getId()); //$NON-NLS-1$
		}
	}

	@Override
	public void endHeading() {
		writer.writeEndElement(); // block
	}

	private Map<String, String> attributesFromCssStyles(String styles) {
		if (styles == null) {
			return Collections.emptyMap();
		}
		List<CssRule> rules = new CssParser().parseBlockContent(styles);
		if (rules.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, String> mapping = new HashMap<String, String>();
		for (CssRule rule : rules) {
			if (CSS_RULE_VERTICAL_ALIGN.equals(rule.name)) {
				if ("super".equals(rule.value)) { //$NON-NLS-1$
					mapping.put("font-size", "75%"); //$NON-NLS-1$ //$NON-NLS-2$
					mapping.put("baseline-shift", "super"); //$NON-NLS-1$ //$NON-NLS-2$
				} else if ("sub".equals(rule.value)) { //$NON-NLS-1$
					mapping.put("font-size", "75%"); //$NON-NLS-1$ //$NON-NLS-2$
					mapping.put("baseline-shift", "sub"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			} else if (CSS_RULE_TEXT_DECORATION.equals(rule.name) || // 
					CSS_RULE_FONT_FAMILY.equals(rule.name) || // 
					CSS_RULE_FONT_SIZE.equals(rule.name) || // 
					CSS_RULE_FONT_WEIGHT.equals(rule.name) || // 
					CSS_RULE_FONT_STYLE.equals(rule.name) || //
					CSS_RULE_BACKGROUND_COLOR.equals(rule.name) || // 
					CSS_RULE_COLOR.equals(rule.name)) {
				mapping.put(rule.name, rule.value);
			}
		}
		return mapping;
	}

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
		SpanInfo info = new SpanInfo(type);

		writer.writeStartElement(foNamespaceUri, "inline"); //$NON-NLS-1$

		String cssStyles = spanTypeToCssStyles.get(type);
		Map<String, String> attrs = cssStyles == null ? null : attributesFromCssStyles(cssStyles);
		if (attributes.getCssStyle() != null) {
			Map<String, String> otherAttrs = attributesFromCssStyles(attributes.getCssStyle());
			if (attrs == null) {
				attrs = otherAttrs;
			} else if (!otherAttrs.isEmpty()) {
				attrs.putAll(otherAttrs);
			}
		}
		if (attrs != null) {
			// output attributes with stable order
			for (Entry<String, String> ent : new TreeMap<String, String>(attrs).entrySet()) {
				writer.writeAttribute(ent.getKey(), ent.getValue());
			}
		}

		elementInfos.push(info);
	}

	@Override
	public void endSpan() {
		ElementInfo elementInfo = elementInfos.pop();
		if (!(elementInfo instanceof SpanInfo)) {
			throw new IllegalStateException();
		}
		close(elementInfo);
	}

	@Override
	public void characters(String text) {
		writer.writeCharacters(text);
	}

	@Override
	public void charactersUnescaped(String literal) {
		Logger.getLogger(XslfoDocumentBuilder.class.getName()).warning("escaping XML literal"); //$NON-NLS-1$
		writer.writeCharacters(literal);
	}

	@Override
	public void entityReference(String entity) {
		writer.writeEntityRef(entity);
	}

	@Override
	public void image(Attributes attributes, String url) {
		// <fo:external-graphic src="url(images/editor-command-help.png)" width="auto" height="auto" content-width="auto" content-height="auto"/>
		writer.writeEmptyElement(foNamespaceUri, "external-graphic"); //$NON-NLS-1$
		writer.writeAttribute("src", String.format("url(%s)", makeUrlAbsolute(url))); //$NON-NLS-1$//$NON-NLS-2$
		applyImageAttributes(attributes);
	}

	private void applyImageAttributes(Attributes attributes) {
		writer.writeAttribute("width", "100%"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.writeAttribute("content-height", "100%"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.writeAttribute("content-width", "scale-to-fit"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.writeAttribute("scaling", "uniform"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		writer.writeStartElement(foNamespaceUri, "basic-link"); //$NON-NLS-1$
		String destinationUrl = makeUrlAbsolute(href);
		if (destinationUrl.startsWith("#")) { //$NON-NLS-1$
			writer.writeAttribute("internal-destination", destinationUrl.substring(1)); //$NON-NLS-1$
		} else {
			writer.writeAttribute("external-destination", String.format("url(%s)", destinationUrl)); //$NON-NLS-1$//$NON-NLS-2$
		}
		image(imageAttributes, imageUrl);
		writer.writeEndElement();// basic-link
	}

	@Override
	public void lineBreak() {
		// an empty block does the trick
		writer.writeEmptyElement(foNamespaceUri, "block"); //$NON-NLS-1$
	}

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		writer.writeStartElement(foNamespaceUri, "basic-link"); //$NON-NLS-1$
		String destinationUrl = makeUrlAbsolute(hrefOrHashName);
		boolean internal = destinationUrl.startsWith("#"); //$NON-NLS-1$
		if (underlineLinks) {
			writer.writeAttribute("text-decoration", "underline"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (internal) {
			writer.writeAttribute("internal-destination", destinationUrl.substring(1)); //$NON-NLS-1$
		} else {
			writer.writeAttribute("external-destination", String.format("url(%s)", destinationUrl)); //$NON-NLS-1$//$NON-NLS-2$
		}
		characters(text);
		writer.writeEndElement();// basic-link
		if (showExternalLinks && !internal) {
			characters(Messages.getString("XslfoDocumentBuilder.beforeLink")); //$NON-NLS-1$
			writer.writeStartElement(foNamespaceUri, "basic-link"); //$NON-NLS-1$
			writer.writeAttribute("external-destination", String.format("url(%s)", destinationUrl)); //$NON-NLS-1$//$NON-NLS-2$
			characters(destinationUrl);
			characters(Messages.getString("XslfoDocumentBuilder.afterLink")); //$NON-NLS-1$
			writer.writeEndElement(); // basic-link
		}
	}

	/**
	 * indicate if external link URLs should be emitted in the text. The default is true.
	 */
	public boolean isShowExternalLinks() {
		return showExternalLinks;
	}

	/**
	 * indicate if external link URLs should be emitted in the text. The default is true.
	 */
	public void setShowExternalLinks(boolean showExternalLinks) {
		this.showExternalLinks = showExternalLinks;
	}

	/**
	 * Indicate if links should be underlined. The default is false.
	 */
	public boolean isUnderlineLinks() {
		return underlineLinks;
	}

	/**
	 * Indicate if links should be underlined. The default is false.
	 */
	public void setUnderlineLinks(boolean underlineLinks) {
		this.underlineLinks = underlineLinks;
	}

	/**
	 * Indicate if h1 headings should start a new page. The default is true.
	 */
	public boolean isPageBreakOnHeading1() {
		return pageBreakOnHeading1;
	}

	/**
	 * Indicate if h1 headings should start a new page. The default is true.
	 */
	public void setPageBreakOnHeading1(boolean pageBreakOnHeading1) {
		this.pageBreakOnHeading1 = pageBreakOnHeading1;
	}

	/**
	 * a title to be emitted on the title page
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * a title to be emitted on the title page
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * a sub-title to be emitted on the title page
	 */
	public String getSubTitle() {
		return subTitle;
	}

	/**
	 * a sub-title to be emitted on the title page
	 */
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	/**
	 * indicate if the text 'Note: ', 'Tip: ', and 'Warning: ' should be added to blocks of type {@link BlockType#NOTE},
	 * {@link BlockType#TIP}, and {@link BlockType#WARNING} respectively.
	 */
	public boolean isPanelText() {
		return panelText;
	}

	/**
	 * indicate if the text 'Note: ', 'Tip: ', and 'Warning: ' should be added to blocks of type {@link BlockType#NOTE},
	 * {@link BlockType#TIP}, and {@link BlockType#WARNING} respectively.
	 */
	public void setPanelText(boolean panelText) {
		this.panelText = panelText;
	}

	/**
	 * a document version number to emit on the title page
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * a document version number to emit on the title page
	 */
	public void setVersion(String version) {
		this.version = version;
	}

}
