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
package org.eclipse.mylyn.internal.wikitext.core.parser.builder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.core.parser.Locator;
import org.eclipse.mylyn.wikitext.core.parser.TableAttributes;
import org.eclipse.mylyn.wikitext.core.parser.TableCellAttributes;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;

public class SplittingHtmlDocumentBuilder extends DocumentBuilder {

	private SplitOutlineItem outline;

	private HtmlDocumentBuilder rootBuilder;

	private File rootFile;

	private boolean formatting;

	private HtmlDocumentBuilder out;

	private Writer writer;

	private File currentFile;

	private boolean navigationImages;

	public void setRootBuilder(HtmlDocumentBuilder rootBuilder) {
		this.rootBuilder = rootBuilder;
		out = rootBuilder;
	}

	public HtmlDocumentBuilder getRootBuilder() {
		return rootBuilder;
	}

	@Override
	public void beginDocument() {
		if (rootBuilder == null || out == null || rootFile == null) {
			throw new IllegalStateException();
		}
		currentFile = rootFile;
		out.beginDocument();
		documentHeader();
	}

	public void acronym(String text, String definition) {
		out.acronym(text, definition);
	}

	public void addCssStylesheet(File file) {
		out.addCssStylesheet(file);
	}

	public void addCssStylesheet(String url) {
		out.addCssStylesheet(url);
	}

	public void beginBlock(BlockType type, Attributes attributes) {
		out.beginBlock(type, attributes);
	}

	public void beginHeading(int level, Attributes attributes) {
		SplitOutlineItem item = outline.getOutlineItemById(attributes.getId());
		if (item != null && !currentFile.getName().equals(item.getSplitTarget())) {
			try {
				documentFooter();
				out.endDocument();
				if (writer != null) {
					writer.close();
					writer = null;
				}
				currentFile = new File(rootFile.getParent(), item.getSplitTarget());

				writer = new OutputStreamWriter(new FileOutputStream(currentFile), "UTF-8");
				HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer, formatting);
				rootBuilder.copyConfiguration(builder);
				if (item.getLabel() != null) {
					String title = rootBuilder.getTitle();
					if (title == null) {
						title = item.getLabel();
					} else {
						title += " - " + item.getLabel();
					}
					builder.setTitle(title);
				}
				out = builder;
				out.beginDocument();

				documentHeader();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		out.beginHeading(level, attributes);
	}

	private void documentFooter() {
		emitNavigation(false);
	}

	private void emitNavigation(boolean header) {

		String currentName = currentFile.getName();
		List<SplitOutlineItem> pageOrder = outline.getPageOrder();
		SplitOutlineItem previous = null;
		SplitOutlineItem next = null;
		SplitOutlineItem current = null;
		boolean foundPage = false;
		for (SplitOutlineItem page : pageOrder) {
			if (page.getSplitTarget().equals(currentName)) {
				foundPage = true;
				current = page;
			} else if (!foundPage) {
				previous = page;
			} else {
				next = page;
				break;
			}
		}

		boolean rootPage = rootFile.getName().equals(currentFile.getName());

		if (next == null && previous == null && rootPage) {
			return;
		}

		if (!header) {
			out.charactersUnescaped("<hr/>");
		}

		TableAttributes tableAttributes = new TableAttributes();
		tableAttributes.setCssClass("navigation");
		tableAttributes.setCssStyle("width: 100%;");
		tableAttributes.setBorder("0");
		tableAttributes.setSummary("navigation");
		out.beginBlock(BlockType.TABLE, tableAttributes);

		TableCellAttributes tableCellAttributes;
		if (header) {
			// header row, emit title of page
			out.beginBlock(BlockType.TABLE_ROW, new Attributes());

			tableCellAttributes = new TableCellAttributes();
			tableCellAttributes.setAlign("center");
			tableCellAttributes.setCssStyle("width: 100%");
			tableCellAttributes.setColspan("3");
			out.beginBlock(BlockType.TABLE_CELL_HEADER, tableCellAttributes);
			if (rootPage) {
				out.characters(rootBuilder.getTitle());
			} else {
				out.characters(current.getLabel());
			}
			out.endBlock();

			out.endBlock();
		}

		// navigation row
		out.beginBlock(BlockType.TABLE_ROW, new Attributes());

		LinkAttributes linkAttributes;

		tableCellAttributes = new TableCellAttributes();
		tableCellAttributes.setAlign("left");
		tableCellAttributes.setCssStyle("width: 20%");
		out.beginBlock(BlockType.TABLE_CELL_NORMAL, tableCellAttributes);
		if (previous != null) {
			linkAttributes = new LinkAttributes();
			linkAttributes.setTitle(previous.getLabel());
			if (navigationImages) {
				ImageAttributes imageAttributes = new ImageAttributes();
				imageAttributes.setAlt("Previous");
				out.imageLink(linkAttributes, imageAttributes, previous.getSplitTarget(), "images/prev.gif");
			} else {
				out.link(linkAttributes, previous.getSplitTarget(), "Previous");
			}
		}
		out.endBlock();

		tableCellAttributes = new TableCellAttributes();
		tableCellAttributes.setAlign("center");
		tableCellAttributes.setCssStyle("width: 60%");
		out.beginBlock(BlockType.TABLE_CELL_NORMAL, tableCellAttributes);
		if (!header && !rootPage) {
			linkAttributes = new LinkAttributes();
			linkAttributes.setTitle(rootBuilder.getTitle());
			if (navigationImages) {
				ImageAttributes imageAttributes = new ImageAttributes();
				imageAttributes.setAlt(rootBuilder.getTitle());
				out.imageLink(linkAttributes, imageAttributes, rootFile.getName(), "images/home.gif");
			} else {
				out.link(linkAttributes, rootFile.getName(), "Top");
			}
		}
		out.endBlock();

		tableCellAttributes = new TableCellAttributes();
		tableCellAttributes.setAlign("right");
		tableCellAttributes.setCssStyle("width: 20%");
		out.beginBlock(BlockType.TABLE_CELL_NORMAL, tableCellAttributes);
		if (next != null) {
			linkAttributes = new LinkAttributes();
			linkAttributes.setTitle(next.getLabel());
			if (navigationImages) {
				ImageAttributes imageAttributes = new ImageAttributes();
				imageAttributes.setAlt("Next");
				out.imageLink(linkAttributes, imageAttributes, next.getSplitTarget(), "images/next.gif");
			} else {
				out.link(linkAttributes, next.getSplitTarget(), "Next");
			}
		}
		out.endBlock();

		out.endBlock(); // navigation row

		// navigation title row
		out.beginBlock(BlockType.TABLE_ROW, new Attributes());

		tableCellAttributes = new TableCellAttributes();
		tableCellAttributes.setAlign("left");
		tableCellAttributes.setValign("top");
		tableCellAttributes.setCssStyle("width: 20%");
		out.beginBlock(BlockType.TABLE_CELL_NORMAL, tableCellAttributes);
		if (previous != null) {
			out.characters(previous.getLabel());
		}
		out.endBlock();

		tableCellAttributes = new TableCellAttributes();
		tableCellAttributes.setAlign("center");
		tableCellAttributes.setCssStyle("width: 60%");
		out.beginBlock(BlockType.TABLE_CELL_NORMAL, tableCellAttributes);

		out.endBlock();

		tableCellAttributes = new TableCellAttributes();
		tableCellAttributes.setAlign("right");
		tableCellAttributes.setValign("top");
		tableCellAttributes.setCssStyle("width: 20%");
		out.beginBlock(BlockType.TABLE_CELL_NORMAL, tableCellAttributes);
		if (next != null) {
			out.characters(next.getLabel());
		}
		out.endBlock();

		out.endBlock(); // navigation title row

		out.endBlock(); // table

		if (header) {
			out.charactersUnescaped("<hr/>");
		}
	}

	private void documentHeader() {
		emitNavigation(true);
	}

	public void beginSpan(SpanType type, Attributes attributes) {
		out.beginSpan(type, attributes);
	}

	public void characters(String text) {
		out.characters(text);
	}

	public void charactersUnescaped(String literal) {
		out.charactersUnescaped(literal);
	}

	public void copyConfiguration(HtmlDocumentBuilder other) {
		out.copyConfiguration(other);
	}

	public void endBlock() {
		out.endBlock();
	}

	public void endDocument() {
		documentFooter();
		out.endDocument();
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				throw new IllegalStateException();
			}
		}
		out = null;
	}

	public void endHeading() {
		out.endHeading();
	}

	public void endSpan() {
		out.endSpan();
	}

	public void entityReference(String entity) {
		out.entityReference(entity);
	}

	public Locator getLocator() {
		return out.getLocator();
	}

	public void image(Attributes attributes, String url) {
		out.image(attributes, url);
	}

	public void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		href = adjustHref(href);
		out.imageLink(linkAttributes, imageAttributes, href, imageUrl);
	}

	private String adjustHref(String href) {
		if (href != null && href.startsWith("#")) {
			SplitOutlineItem target = outline.getOutlineItemById(href.substring(1));
			if (target != null && target.getSplitTarget() != null) {
				href = target.getSplitTarget().replace(" ", "%20") + href;
			}
		}
		return href;
	}

	public void lineBreak() {
		out.lineBreak();
	}

	public void link(Attributes attributes, String hrefOrHashName, String text) {
		hrefOrHashName = adjustHref(hrefOrHashName);
		out.link(attributes, hrefOrHashName, text);
	}

	public void setLocator(Locator locator) {
		if (out != null) {
			out.setLocator(locator);
		}
	}

	public void setOutline(SplitOutlineItem outline) {
		this.outline = outline;
	}

	public SplitOutlineItem getOutline() {
		return outline;
	}

	public void setRootFile(File rootFile) {
		this.rootFile = rootFile;
	}

	public void setNavigationImages(boolean navigationImages) {
		this.navigationImages = navigationImages;
	}

	public boolean isNavigationImages() {
		return navigationImages;
	}

}
