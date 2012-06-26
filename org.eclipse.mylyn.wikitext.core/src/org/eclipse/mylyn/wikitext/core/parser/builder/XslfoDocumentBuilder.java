/*******************************************************************************
 * Copyright (c) 2009, 2012 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Torkild U. Resheim - bugs 337405, 336592, 336683, 336813 
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.builder;

import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.eclipse.mylyn.internal.wikitext.core.util.css.CssParser;
import org.eclipse.mylyn.internal.wikitext.core.util.css.CssRule;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.core.parser.ListAttributes;
import org.eclipse.mylyn.wikitext.core.parser.TableAttributes;
import org.eclipse.mylyn.wikitext.core.parser.TableCellAttributes;
import org.eclipse.mylyn.wikitext.core.parser.TableRowAttributes;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.core.util.XmlStreamWriter;

/**
 * A document builder that produces XSL-FO output. XSL-FO is suitable for conversion to other formats such as PDF.
 * 
 * @see XslfoDocumentBuilder.Configuration Configuration for configurable settings
 * @see <a href="http://www.w3.org/TR/2001/REC-xsl-20011015/">XSL-FO 1.0 specification</a>
 * @see <a href="http://en.wikipedia.org/wiki/XSL_Formatting_Objects">XSL-FO (WikiPedia)</a>
 * @see <a href="http://www.w3schools.com/xslfo/default.asp">XSL-FO Tutorial</a>
 * @author David Green
 * @author Torkild U. Resheim
 * @since 1.1
 */
public class XslfoDocumentBuilder extends AbstractXmlDocumentBuilder {

	private static final String CSS_RULE_BORDER_STYLE = "border-style"; //$NON-NLS-1$

	private static final String CSS_RULE_BORDER_WIDTH = "border-width";; //$NON-NLS-1$

	private static final String CSS_RULE_BORDER_COLOR = "border-color";; //$NON-NLS-1$

	private static final String CSS_RULE_BACKGROUND_COLOR = "background-color"; //$NON-NLS-1$

	private static final String CSS_RULE_COLOR = "color"; //$NON-NLS-1$

	private static final String CSS_RULE_VERTICAL_ALIGN = "vertical-align"; //$NON-NLS-1$

	private static final String CSS_RULE_TEXT_ALIGN = "text-align"; //$NON-NLS-1$

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

	private boolean pageOpen = false;

	private int h1Count = 0;

	private final Stack<ElementInfo> elementInfos = new Stack<ElementInfo>();

	private Configuration configuration = new Configuration();

	private OutlineItem outline;

	public XslfoDocumentBuilder(Writer out) {
		super(out);
	}

	public XslfoDocumentBuilder(XmlStreamWriter writer) {
		super(writer);
	}

	@Override
	public void acronym(String text, String definition) {
		characters(text);
	}

	/**
	 * @since 1.6
	 */
	public OutlineItem getOutline() {
		return outline;
	}

	/**
	 * If an outline item is set, the document builder will output XSL:FO bookmarks at the beginning of the resulting
	 * document.
	 * 
	 * @param outline
	 *            the root outline item.
	 * @since 1.6
	 */
	public void setOutline(OutlineItem outline) {
		this.outline = outline;
	}

	private static class ElementInfo {
		int size = 1;

	}

	private static class SpanInfo extends ElementInfo {
		@SuppressWarnings("unused")
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
			if (attributes.getTitle() != null || configuration.panelText) {
				writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
				if (configuration.panelText) {
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
			applyTableCellAttributes(attributes);
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
			applyTableRowAttributes(attributes);
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
		writer.writeAttribute("font-size", String.format("%spt", configuration.fontSizes[level])); //$NON-NLS-1$ //$NON-NLS-2$
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

	private void applyTableCellAttributes(Attributes attributes) {
		if (attributes instanceof TableCellAttributes) {
			TableCellAttributes cellAttributes = (TableCellAttributes) attributes;
			if (cellAttributes.getBgcolor() != null) {
				writer.writeAttribute(CSS_RULE_BACKGROUND_COLOR, cellAttributes.getBgcolor());
			}
			if (cellAttributes.getColspan() != null) {
				writer.writeAttribute("number-columns-spanned", cellAttributes.getColspan()); //$NON-NLS-1$
			}
			if (cellAttributes.getRowspan() != null) {
				writer.writeAttribute("number-rows-spanned", cellAttributes.getRowspan()); //$NON-NLS-1$
			}
			// Expecting values left, right, center, justify or inherit
			if (cellAttributes.getAlign() != null) {
				writer.writeAttribute("text-align", cellAttributes.getAlign()); //$NON-NLS-1$				
			}
			// Vertical align may be top, middle and bottom
			if (cellAttributes.getValign() != null) {
				String value = cellAttributes.getAlign();
				if (cellAttributes.getValign().equals("top")) { //$NON-NLS-1$
					value = "before"; //$NON-NLS-1$
				} else if (cellAttributes.getValign().equals("middle")) { //$NON-NLS-1$
					value = "center"; //$NON-NLS-1$
				} else if (cellAttributes.getValign().equals("bottom")) { //$NON-NLS-1$
					value = "after"; //$NON-NLS-1$
				}
				if (value != null) {
					writer.writeAttribute("display-align", value); //$NON-NLS-1$									
				}
			}
		}
	}

	private void applyTableRowAttributes(Attributes attributes) {
		if (attributes instanceof TableRowAttributes) {
			TableRowAttributes rowAttributes = (TableRowAttributes) attributes;
			if (rowAttributes.getBgcolor() != null) {
				writer.writeAttribute(CSS_RULE_BACKGROUND_COLOR, rowAttributes.getBgcolor());
			}
			// Vertical align may be top, middle and bottom
			if (rowAttributes.getValign() != null) {
				String value = rowAttributes.getAlign();
				if (rowAttributes.getValign().equals("top")) { //$NON-NLS-1$
					value = "before"; //$NON-NLS-1$
				} else if (rowAttributes.getValign().equals("middle")) { //$NON-NLS-1$
					value = "center"; //$NON-NLS-1$
				} else if (rowAttributes.getValign().equals("bottom")) { //$NON-NLS-1$
					value = "after"; //$NON-NLS-1$
				}
				if (value != null) {
					writer.writeAttribute("display-align", value); //$NON-NLS-1$									
				}
			}
		}
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

	private void writeMargins(Margins margins, XmlStreamWriter writer) {
		if (margins == null) {
			return;
		}
		writer.writeAttribute("margin-top", String.format("%scm", margins.marginTop)); //$NON-NLS-1$ //$NON-NLS-2$
		writer.writeAttribute("margin-bottom", String.format("%scm", margins.marginBottom)); //$NON-NLS-1$ //$NON-NLS-2$
		writer.writeAttribute("margin-left", String.format("%scm", margins.marginLeft)); //$NON-NLS-1$ //$NON-NLS-2$
		writer.writeAttribute("margin-right", String.format("%scm", margins.marginRight)); //$NON-NLS-1$ //$NON-NLS-2$		
	}

	private void writeRegion(Region region, XmlStreamWriter writer) {
		if (region == null) {
			return;
		}
		writer.writeEmptyElement(foNamespaceUri, "region-" + region.location); //$NON-NLS-1$
		writer.writeAttribute("extent", String.format("%scm", configuration.pageHeight)); //$NON-NLS-1$//$NON-NLS-2$
		writer.writeAttribute("precedence", Boolean.toString(region.precedence)); //$NON-NLS-1$
		if (region.name != null) {
			writer.writeAttribute("region-name", region.name); //$NON-NLS-1$
		}
	}

	@Override
	public void beginDocument() {
		writer.setDefaultNamespace(foNamespaceUri);

		writer.writeStartElement(foNamespaceUri, "root"); //$NON-NLS-1$
		writer.writeNamespace("", foNamespaceUri); //$NON-NLS-1$

		writer.writeStartElement(foNamespaceUri, "layout-master-set"); //$NON-NLS-1$
		writer.writeStartElement(foNamespaceUri, "simple-page-master"); //$NON-NLS-1$
		writer.writeAttribute("master-name", "page-layout"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.writeAttribute("page-height", String.format("%scm", configuration.pageHeight)); //$NON-NLS-1$ //$NON-NLS-2$
		writer.writeAttribute("page-width", String.format("%scm", configuration.pageWidth)); //$NON-NLS-1$ //$NON-NLS-2$

		if (configuration.getPageMargins() == null) {
			writer.writeAttribute("margin", String.format("%scm", configuration.pageMargin)); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			writeMargins(configuration.getPageMargins(), writer);
		}

		writer.writeEmptyElement(foNamespaceUri, "region-body"); //$NON-NLS-1$

		if (configuration.getBodyMargins() == null) {
			if (hasPageFooter()) {
				writer.writeAttribute("margin-bottom", "3cm"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			writeMargins(configuration.getBodyMargins(), writer);
		}
		if (configuration.bodyAfterRegion == null) {
			if (hasPageFooter()) {
				writeRegion(new Region("after", "footer", 2, false), writer); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			writeRegion(configuration.getBodyAfterRegion(), writer);
		}

		writeRegion(configuration.getBodyBeforeRegion(), writer);
		writeRegion(configuration.getBodyEndRegion(), writer);
		writeRegion(configuration.getBodyStartRegion(), writer);

		writer.writeEndElement(); // simple-page-master
		writer.writeEndElement(); // layout-master-set

		if (outline != null && !outline.getChildren().isEmpty()) {
			writer.writeStartElement("bookmark-tree"); //$NON-NLS-1$
			emitToc(writer, outline.getChildren());
			writer.writeEndElement(); // bookmark-tree
		}

		if (configuration.getTitle() != null) {
			emitTitlePage();
		}

		openPage();
		openFlow(false);
	}

	private boolean hasPageFooter() {
		return configuration.copyright != null || configuration.pageNumbering;
	}

	private void emitTitlePage() {
		openPage();
		openFlow(true);
		writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$

		if (configuration.title != null) {
			writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
			writer.writeAttribute("font-weight", "bold"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("font-size", "25pt"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("text-align", "center"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("space-before", "19pt"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeCharacters(configuration.title);
			writer.writeEndElement(); // block
		}

		if (configuration.subTitle != null) {
			writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
			writer.writeAttribute("font-weight", "bold"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("font-size", "18pt"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("text-align", "center"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("space-before", "15pt"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeCharacters(configuration.subTitle);
			writer.writeEndElement(); // block
		}

		if (configuration.version != null) {
			writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
			writer.writeAttribute("font-weight", "bold"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("font-size", "14pt"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("text-align", "center"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("space-before", "13pt"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeCharacters(configuration.version);
			writer.writeEndElement(); // block
		}

		if (configuration.date != null) {
			writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
			writer.writeAttribute("font-weight", "bold"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("font-size", "14pt"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("text-align", "center"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeAttribute("space-before", "13pt"); //$NON-NLS-1$//$NON-NLS-2$
			writer.writeCharacters(configuration.date);
			writer.writeEndElement(); // block
		}

		writer.writeEmptyElement(foNamespaceUri, "block"); //$NON-NLS-1$
		writer.writeAttribute("break-after", "page"); //$NON-NLS-1$//$NON-NLS-2$
		writer.writeEndElement(); // block

		closeFlow();
		closePage();
	}

	private void openFlow(boolean titlePage) {
		if (hasPageFooter()) {
			final boolean hasCopyrightText = configuration.copyright != null
					&& configuration.copyright.trim().length() > 0;
			final boolean hasPageNumber = configuration.pageNumbering && !titlePage;
			if (hasCopyrightText || hasPageNumber) {
				writer.writeStartElement(foNamespaceUri, "static-content"); //$NON-NLS-1$
				writer.writeAttribute("flow-name", "footer"); //$NON-NLS-1$//$NON-NLS-2$

				if (hasCopyrightText) {
					writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
					configureFontSize(0);
					writer.writeAttribute("text-align", "center"); //$NON-NLS-1$//$NON-NLS-2$

					writer.writeCharacters(configuration.copyright);

					writer.writeEndElement(); // block
				}

				if (hasPageNumber) {
					writer.writeStartElement(foNamespaceUri, "block"); //$NON-NLS-1$
					configureFontSize(0);
					writer.writeAttribute("text-align", "outside"); //$NON-NLS-1$//$NON-NLS-2$
					//
					//				// output the section header into the footer using retrieve-marker
					//				writer.writeEmptyElement(foNamespaceUri, "retrieve-marker"); //$NON-NLS-1$
					//				writer.writeAttribute("retrieve-boundary", "page-sequence"); //$NON-NLS-1$//$NON-NLS-2$
					//				writer.writeAttribute("retrieve-position", "first-starting-within-page"); //$NON-NLS-1$//$NON-NLS-2$
					//				writer.writeAttribute("retrieve-class-name", "section-title"); //$NON-NLS-1$//$NON-NLS-2$

					writer.writeEmptyElement(foNamespaceUri, "page-number"); //$NON-NLS-1$

					writer.writeEndElement(); // block
				}
				writer.writeEndElement(); // static-content
			}
		}
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

	private void emitToc(XmlStreamWriter writer, List<OutlineItem> children) {
		for (OutlineItem item : children) {
			writer.writeStartElement("bookmark"); //$NON-NLS-1$
			writer.writeAttribute("internal-destination", item.getId()); //$NON-NLS-1$

			writer.writeStartElement("bookmark-title"); //$NON-NLS-1$
			writer.writeCharacters(item.getLabel());
			writer.writeEndElement();

			if (!item.getChildren().isEmpty()) {
				emitToc(writer, item.getChildren());
			}

			writer.writeEndElement(); // bookmark
		}
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
		if (level == 1 && ++h1Count > 1 && configuration.pageBreakOnHeading1) {
			if (pageOpen) {
				closeFlow();
				closePage();
			}
			openPage();
			openFlow(false);
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
				String cssValue = rule.value;
				if (cssValue.equals("super")) { //$NON-NLS-1$
					mapping.put("font-size", "75%"); //$NON-NLS-1$ //$NON-NLS-2$
					mapping.put("baseline-shift", "super"); //$NON-NLS-1$ //$NON-NLS-2$
				} else if (cssValue.equals("sub")) { //$NON-NLS-1$
					mapping.put("font-size", "75%"); //$NON-NLS-1$ //$NON-NLS-2$
					mapping.put("baseline-shift", "sub"); //$NON-NLS-1$ //$NON-NLS-2$
				} else if (cssValue.equals("top")) { //$NON-NLS-1$
					mapping.put("display-align", "before"); //$NON-NLS-1$ //$NON-NLS-2$									
				} else if (cssValue.equals("middle")) { //$NON-NLS-1$
					mapping.put("display-align", "center"); //$NON-NLS-1$ //$NON-NLS-2$									
				} else if (cssValue.equals("bottom")) { //$NON-NLS-1$
					mapping.put("display-align", "after"); //$NON-NLS-1$ //$NON-NLS-2$									
				}
			} else if (CSS_RULE_TEXT_DECORATION.equals(rule.name) || // 
					CSS_RULE_FONT_FAMILY.equals(rule.name) || // 
					CSS_RULE_FONT_SIZE.equals(rule.name) || // 
					CSS_RULE_FONT_WEIGHT.equals(rule.name) || // 
					CSS_RULE_FONT_STYLE.equals(rule.name) || //
					CSS_RULE_BACKGROUND_COLOR.equals(rule.name) || // 
					CSS_RULE_COLOR.equals(rule.name)) {
				mapping.put(rule.name, rule.value);
			} else if (CSS_RULE_BORDER_STYLE.equals(rule.name)) {
				mapping.put(rule.name, rule.value);
			} else if (CSS_RULE_BORDER_WIDTH.equals(rule.name)) {
				mapping.put(rule.name, rule.value);
			} else if (CSS_RULE_BORDER_COLOR.equals(rule.name)) {
				mapping.put(rule.name, rule.value);
			} else if (CSS_RULE_TEXT_ALIGN.equals(rule.name)) {
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
		boolean sizeSpecified = false;
		boolean scaleToFit = true;
		if (attributes instanceof ImageAttributes) {
			ImageAttributes imageAttributes = (ImageAttributes) attributes;
			if (imageAttributes.getWidth() > 0) {
				sizeSpecified = true;
				emitImageSize("width", imageAttributes.getWidth(), imageAttributes.isWidthPercentage()); //$NON-NLS-1$
			}
			if (imageAttributes.getHeight() > 0) {
				sizeSpecified = true;
				emitImageSize("height", imageAttributes.getHeight(), imageAttributes.isHeightPercentage()); //$NON-NLS-1$
			}
		}
		if (!sizeSpecified) {
			writer.writeAttribute("width", "100%"); //$NON-NLS-1$ //$NON-NLS-2$
			writer.writeAttribute("content-height", "100%"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (scaleToFit) {
			writer.writeAttribute("content-width", "scale-to-fit"); //$NON-NLS-1$ //$NON-NLS-2$
			writer.writeAttribute("scaling", "uniform"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void emitImageSize(String attributeName, int units, boolean isPercentage) {
		writer.writeAttribute(attributeName, String.format("%s%s", units, isPercentage ? "%" : "px")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
		if (configuration.underlineLinks) {
			writer.writeAttribute("text-decoration", "underline"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (internal) {
			writer.writeAttribute("internal-destination", destinationUrl.substring(1)); //$NON-NLS-1$
		} else {
			writer.writeAttribute("external-destination", String.format("url(%s)", destinationUrl)); //$NON-NLS-1$//$NON-NLS-2$
		}
		characters(text);
		writer.writeEndElement();// basic-link
		if (configuration.showExternalLinks && !internal) {
			characters(Messages.getString("XslfoDocumentBuilder.beforeLink")); //$NON-NLS-1$
			writer.writeStartElement(foNamespaceUri, "basic-link"); //$NON-NLS-1$
			writer.writeAttribute("external-destination", String.format("url(%s)", destinationUrl)); //$NON-NLS-1$//$NON-NLS-2$
			characters(destinationUrl);
			characters(Messages.getString("XslfoDocumentBuilder.afterLink")); //$NON-NLS-1$
			writer.writeEndElement(); // basic-link
		}
	}

	/**
	 * The current configuration of this builder. The returned value is mutable and changes to it affect this builder's
	 * configuration.
	 * 
	 * @see Configuration Configuration class for configurable settings
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * The current configuration of this builder.
	 * 
	 * @see Configuration Configuration class for configurable settings
	 */
	public void setConfiguration(Configuration configuration) {
		if (configuration == null) {
			throw new IllegalArgumentException();
		}
		this.configuration = configuration;
	}

	/**
	 * This type represents a XSL:FO page region.
	 * 
	 * @author Torkild U. Resheim, MARINTEK
	 * @since 1.6
	 */
	public static class Region implements Cloneable {

		private float extent = 3.0f;

		private boolean precedence = false;

		private String location = null;

		private String name = null;

		/**
		 * Creates a new region with default values.
		 */
		public Region() {
			// Only use default values here
		}

		public Region(String name, float extent, boolean precedence) {
			this.name = name;
			this.extent = extent;
			this.precedence = precedence;
		}

		public Region(String location, String name, float extent, boolean precedence) {
			this.location = location;
			this.extent = extent;
			this.precedence = precedence;
			this.name = name;
		}

		/**
		 * Sets the extent of the region in cm.
		 * 
		 * @param extent
		 *            the region extent in cm
		 */
		public void setExtent(float extent) {
			this.extent = extent;
		}

		/**
		 * Return the region extent in cm. Defaults to 3cm.
		 * 
		 * @return value of the region extent in cms
		 */
		public float getExtent() {
			return extent;
		}

		/**
		 * Sets the precedence of the region. Defaults to <code>false</code>
		 * 
		 * @param precedence
		 *            the new precedence value
		 */
		public void setPrecedence(boolean precedence) {
			this.precedence = precedence;
		}

		/**
		 * Returns whether or not the region has precedence. Defaults to <code>false</code>.
		 * 
		 * @return the region precedence
		 */
		public boolean isPrecedence() {
			return precedence;
		}

		/**
		 * Assigns a name to the region. The default is to have no name.
		 * 
		 * @param name
		 *            the region name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Returns the name of the region. The default is to have no name.
		 * 
		 * @return the region name
		 */
		public String getName() {
			return name;
		}

	}

	/**
	 * This type represents a XSL:FO page or body margin.
	 * 
	 * @author Torkild U. Resheim, MARINTEK
	 * @since 1.6
	 */
	public static class Margins implements Cloneable {

		private float marginTop = 1.5f;

		private float marginBottom = 1.5f;

		private float marginLeft = 1.5f;

		private float marginRight = 1.5f;

		/**
		 * Creates a new set of margins with the default values.
		 */
		public Margins() {
			// Use default values
		}

		/**
		 * Creates a new set of margins with the specified values.
		 * 
		 * @param top
		 *            value of the top margin in cm
		 * @param bottom
		 *            value of the bottom margin in cm
		 * @param left
		 *            value of the left margin in cm
		 * @param right
		 *            value of the right margin in cm
		 */
		public Margins(float top, float bottom, float left, float right) {
			this.marginTop = top;
			this.marginBottom = bottom;
			this.marginLeft = left;
			this.marginRight = right;
		}

		/**
		 * Sets the <b>margin-top</b> property in cm. Defaults to 1.5cm.
		 * 
		 * @param marginTop
		 *            new value of the top margin in cm
		 */
		public void setMarginTop(float marginTop) {
			this.marginTop = marginTop;
		}

		/**
		 * Returns the <b>margin-top</b> propert in cm. Defaults to 1.5cm.
		 * 
		 * @return value of the top margin in cm.
		 */
		public float getMarginTop() {
			return marginTop;
		}

		/**
		 * Sets the <b>margin-bottom</b> property in cm. Defaults to 1.5cm.
		 * 
		 * @param marginBottom
		 *            new value of the top margin in cm
		 */
		public void setMarginBottom(float marginBottom) {
			this.marginBottom = marginBottom;
		}

		/**
		 * Returns the <b>margin-bottom</b> property in cm. Defaults to 1.5cm.
		 * 
		 * @return value of the bottom margin in cm.
		 */
		public float getMarginBottom() {
			return marginBottom;
		}

		/**
		 * Sets the <b>margin-left</b> property in cm. Defaults to 1.5cm.
		 * 
		 * @param marginLeft
		 *            new value of the left margin in cm
		 */
		public void setMarginLeft(float marginLeft) {
			this.marginLeft = marginLeft;
		}

		/**
		 * Returns the <b>margin-left</b> property in cm. Defaults to 1.5cm.
		 * 
		 * @return value of the left margin in cm.
		 */
		public float getMarginLeft() {
			return marginLeft;
		}

		/**
		 * Sets the <b>margin-right</b> property in cm. Defaults to 1.5cm.
		 * 
		 * @param marginRight
		 *            new value of the right margin in cm
		 */
		public void setMarginRight(float marginRight) {
			this.marginRight = marginRight;
		}

		/**
		 * Returns the <b>margin-right</b> property of the master page in cm. Defaults to 1.5cm.
		 * 
		 * @return the master page right margin in cm.
		 */
		public float getMarginRight() {
			return marginRight;
		}

	}

	/**
	 * A class that encapsulates all configurable settings of the {@link XslfoDocumentBuilder}. This class implements
	 * the template design pattern via {@link Configuration#clone()}.
	 * 
	 * @author David Green
	 * @author Torkild U. Resheim, MARINTEK
	 */
	public static class Configuration implements Cloneable {

		private float[] fontSizes = new float[] { 12.0f, 18.0f, 15.0f, 13.2f, 12.0f, 10.4f, 8.0f };

		private final float[] fontSizeMultipliers = new float[] { 1.0f, 1.5f, 1.25f, 1.1f, 1.0f, 0.83f, 0.67f };

		private boolean pageBreakOnHeading1 = true;

		private boolean showExternalLinks = true;

		private boolean underlineLinks = false;

		private boolean panelText = true;

		private String title;

		private String subTitle;

		private String version;

		private String date;

		private String author;

		private String copyright;

		private boolean pageNumbering = true;

		private float pageMargin = 1.5f;

		private float pageHeight = 29.7f;

		private float pageWidth = 21.0f;

		private Margins pageMargins = null;

		private Margins bodyMargins = null;

		private Region bodyBeforeRegion = null;

		private Region bodyAfterRegion = null;

		private Region bodyStartRegion = null;

		private Region bodyEndRegion = null;

		private float referenceOrientation = 90f;

		public Configuration() {
			setFontSize(10.0f);
		}

		@Override
		public Configuration clone() {
			try {
				return (Configuration) super.clone();
			} catch (CloneNotSupportedException e) {
				throw new IllegalStateException(e);
			}
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
			for (float fontSizeMultiplier : fontSizeMultipliers) {
				if (fontSizeMultiplier < 0.2) {
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
			System.arraycopy(fontSizeMultipliers, 0, values, 0, 7);
			return values;
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
		 * indicate if the text 'Note: ', 'Tip: ', and 'Warning: ' should be added to blocks of type
		 * {@link BlockType#NOTE}, {@link BlockType#TIP}, and {@link BlockType#WARNING} respectively.
		 */
		public boolean isPanelText() {
			return panelText;
		}

		/**
		 * indicate if the text 'Note: ', 'Tip: ', and 'Warning: ' should be added to blocks of type
		 * {@link BlockType#NOTE}, {@link BlockType#TIP}, and {@link BlockType#WARNING} respectively.
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

		/**
		 * a date to emit on the title page
		 */
		public String getDate() {
			return date;
		}

		/**
		 * a date to emit on the title page
		 */
		public void setDate(String date) {
			this.date = date;
		}

		/**
		 * an author to emit on the title page
		 */
		public String getAuthor() {
			return author;
		}

		/**
		 * an author to emit on the title page
		 */
		public void setAuthor(String author) {
			this.author = author;
		}

		/**
		 * a copyright to emit in the document page footer
		 */
		public String getCopyright() {
			return copyright;
		}

		/**
		 * a copyright to emit in the document page footer
		 */
		public void setCopyright(String copyright) {
			this.copyright = copyright;
		}

		/**
		 * indicate if pages should be numbered
		 */
		public boolean isPageNumbering() {
			return pageNumbering;
		}

		/**
		 * indicate if pages should be numbered
		 */
		public void setPageNumbering(boolean pageNumbering) {
			this.pageNumbering = pageNumbering;
		}

		/**
		 * The page margin in cm. Defaults to 1.5cm.
		 */
		public float getPageMargin() {
			return pageMargin;
		}

		/**
		 * The page margin in cm. Defaults to 1.5cm.
		 */
		public void setPageMargin(float pageMargin) {
			this.pageMargin = pageMargin;
		}

		/**
		 * The page height in cm. Defaults to A4 sizing (29.7cm)
		 */
		public float getPageHeight() {
			return pageHeight;
		}

		/**
		 * The page height in cm. Defaults to A4 sizing (29.7cm)
		 */
		public void setPageHeight(float pageHeight) {
			this.pageHeight = pageHeight;
		}

		/**
		 * The page width in cm. Defaults to A4 sizing (21.0cm)
		 */
		public float getPageWidth() {
			return pageWidth;
		}

		/**
		 * The page width in cm. Defaults to A4 sizing (21.0cm)
		 */
		public void setPageWidth(float pageWidth) {
			this.pageWidth = pageWidth;
		}

		/**
		 * Sets the <b>reference-orientation</b> property of the master page in degrees. Defaults to 90 degrees.
		 * 
		 * @param referenceOrientation
		 *            the master page orientation in degrees.
		 * @since 1.6
		 */
		public void setReferenceOrientation(float referenceOrientation) {
			this.referenceOrientation = referenceOrientation;
		}

		/**
		 * The <b>reference-orientation</b> property of the master page in degrees. Defaults to 90 degrees.
		 * 
		 * @return the master page orientation in degrees.
		 * @since 1.6
		 */
		public float getReferenceOrientation() {
			return referenceOrientation;
		}

		/**
		 * Returns the margins of the master page.
		 * 
		 * @return master page margins
		 * @since 1.6
		 */
		public Margins getPageMargins() {
			return pageMargins;
		}

		/**
		 * Returns the body margins.
		 * 
		 * @return body margins
		 * @since 1.6
		 */
		public Margins getBodyMargins() {
			return bodyMargins;
		}

		/**
		 * Sets the page margins. This method allows each margin to be specified individually.
		 * 
		 * @param pageMargins
		 *            the page margins.
		 * @see #setPageMargin(float)
		 * @since 1.6
		 */
		public void setPageMargins(Margins pageMargins) {
			this.pageMargins = pageMargins;
		}

		/**
		 * Sets the body margins. This method allows each margin to be specified individually.
		 * 
		 * @param boduMargins
		 *            the page margins.
		 * @since 1.6
		 */
		public void setBodyMargins(Margins bodyMargins) {
			this.bodyMargins = bodyMargins;
		}

		/**
		 * @since 1.6
		 */
		public void setBodyBeforeRegion(Region region) {
			region.setName("before"); //$NON-NLS-1$
			this.bodyBeforeRegion = region;
		}

		/**
		 * @since 1.6
		 */
		public Region getBodyBeforeRegion() {
			return bodyBeforeRegion;
		}

		/**
		 * @since 1.6
		 */
		public void setBodyAfterRegion(Region region) {
			region.setName("after"); //$NON-NLS-1$
			this.bodyAfterRegion = region;
		}

		/**
		 * @since 1.6
		 */
		public Region getBodyAfterRegion() {
			return bodyAfterRegion;
		}

		/**
		 * @since 1.6
		 */
		public void setBodyStartRegion(Region region) {
			region.setName("start"); //$NON-NLS-1$
			this.bodyStartRegion = region;
		}

		/**
		 * @since 1.6
		 */
		public Region getBodyStartRegion() {
			return bodyStartRegion;
		}

		/**
		 * @since 1.6
		 */
		public void setBodyEndRegion(Region region) {
			region.setName("end"); //$NON-NLS-1$
			this.bodyEndRegion = region;
		}

		/**
		 * @since 1.6
		 */
		public Region getBodyEndRegion() {
			return bodyEndRegion;
		}

	}
}
