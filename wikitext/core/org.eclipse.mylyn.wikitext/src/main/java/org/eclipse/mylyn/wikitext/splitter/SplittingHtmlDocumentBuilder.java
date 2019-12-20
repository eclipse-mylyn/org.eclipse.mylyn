/*******************************************************************************
 * Copyright (c) 2007, 2015 David Green and others.
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
package org.eclipse.mylyn.wikitext.splitter;

import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.Locator;
import org.eclipse.mylyn.wikitext.parser.TableAttributes;
import org.eclipse.mylyn.wikitext.parser.TableCellAttributes;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder.Stylesheet;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;

/**
 * @author David Green
 * @since 3.0
 */
public class SplittingHtmlDocumentBuilder extends DocumentBuilder {

	private SplitOutlineItem outline;

	private HtmlDocumentBuilder rootBuilder;

	private File rootFile;

	private boolean formatting;

	private HtmlDocumentBuilder out;

	private Writer writer;

	private File currentFile;

	private boolean navigationImages;

	private String navigationImagePath = "images"; //$NON-NLS-1$

	private boolean embeddedTableOfContents;

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

	@Override
	public void acronym(String text, String definition) {
		out.acronym(text, definition);
	}

	public void addCssStylesheet(Stylesheet stylesheet) {
		out.addCssStylesheet(stylesheet);
	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		out.beginBlock(type, attributes);
	}

	@Override
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

				writer = new OutputStreamWriter(new FileOutputStream(currentFile), StandardCharsets.UTF_8);
				HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer, formatting);
				rootBuilder.copyConfiguration(builder);
				if (item.getLabel() != null) {
					String title = rootBuilder.getTitle();
					if (title == null) {
						title = item.getLabel();
					} else {
						title += " - " + item.getLabel(); //$NON-NLS-1$
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
		emitEmbeddedTableOfContentsFooter();
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
			out.charactersUnescaped("<hr class=\"navigation-separator\"/>"); //$NON-NLS-1$
		}

		TableAttributes tableAttributes = new TableAttributes();
		tableAttributes.setCssClass("navigation"); //$NON-NLS-1$
		tableAttributes.setCssStyle("width: 100%;"); //$NON-NLS-1$
		tableAttributes.setBorder("0"); //$NON-NLS-1$
		tableAttributes.setSummary("navigation"); //$NON-NLS-1$
		out.beginBlock(BlockType.TABLE, tableAttributes);

		TableCellAttributes tableCellAttributes;
		if (header) {
			// header row, emit title of page
			out.beginBlock(BlockType.TABLE_ROW, new Attributes());

			tableCellAttributes = new TableCellAttributes();
			tableCellAttributes.setAlign("center"); //$NON-NLS-1$
			tableCellAttributes.setCssStyle("width: 100%"); //$NON-NLS-1$
			tableCellAttributes.setColspan("3"); //$NON-NLS-1$
			out.beginBlock(BlockType.TABLE_CELL_HEADER, tableCellAttributes);
			if (rootPage) {
				out.characters(rootBuilder.getTitle());
			} else {
				out.characters(current == null ? "" : current.getLabel()); //$NON-NLS-1$
			}
			out.endBlock();

			out.endBlock();
		}

		// navigation row
		out.beginBlock(BlockType.TABLE_ROW, new Attributes());

		LinkAttributes linkAttributes;

		tableCellAttributes = new TableCellAttributes();
		tableCellAttributes.setAlign("left"); //$NON-NLS-1$
		tableCellAttributes.setCssStyle("width: 20%"); //$NON-NLS-1$
		out.beginBlock(BlockType.TABLE_CELL_NORMAL, tableCellAttributes);
		if (previous != null) {
			linkAttributes = new LinkAttributes();
			linkAttributes.setTitle(previous.getLabel());
			if (navigationImages) {
				ImageAttributes imageAttributes = new ImageAttributes();
				imageAttributes.setAlt(Messages.getString("SplittingHtmlDocumentBuilder.Previous")); //$NON-NLS-1$
				out.imageLink(linkAttributes, imageAttributes, previous.getSplitTarget(),
						computeNavImagePath(Messages.getString("SplittingHtmlDocumentBuilder.Previous_Image"))); //$NON-NLS-1$
			} else {
				out.link(linkAttributes, previous.getSplitTarget(),
						Messages.getString("SplittingHtmlDocumentBuilder.Previous")); //$NON-NLS-1$
			}
		}
		out.endBlock();

		tableCellAttributes = new TableCellAttributes();
		tableCellAttributes.setAlign("center"); //$NON-NLS-1$
		tableCellAttributes.setCssStyle("width: 60%"); //$NON-NLS-1$
		out.beginBlock(BlockType.TABLE_CELL_NORMAL, tableCellAttributes);
		if (!header && !rootPage) {
			linkAttributes = new LinkAttributes();
			linkAttributes.setTitle(rootBuilder.getTitle());
			if (navigationImages) {
				ImageAttributes imageAttributes = new ImageAttributes();
				imageAttributes.setAlt(rootBuilder.getTitle());
				out.imageLink(linkAttributes, imageAttributes, rootFile.getName(),
						computeNavImagePath(Messages.getString("SplittingHtmlDocumentBuilder.Home_Image"))); //$NON-NLS-1$
			} else {
				out.link(linkAttributes, rootFile.getName(), Messages.getString("SplittingHtmlDocumentBuilder.Home")); //$NON-NLS-1$
			}
		}
		out.endBlock();

		tableCellAttributes = new TableCellAttributes();
		tableCellAttributes.setAlign("right"); //$NON-NLS-1$
		tableCellAttributes.setCssStyle("width: 20%"); //$NON-NLS-1$
		out.beginBlock(BlockType.TABLE_CELL_NORMAL, tableCellAttributes);
		if (next != null) {
			linkAttributes = new LinkAttributes();
			linkAttributes.setTitle(next.getLabel());
			if (navigationImages) {
				ImageAttributes imageAttributes = new ImageAttributes();
				imageAttributes.setAlt(Messages.getString("SplittingHtmlDocumentBuilder.Next")); //$NON-NLS-1$
				out.imageLink(linkAttributes, imageAttributes, next.getSplitTarget(),
						computeNavImagePath(Messages.getString("SplittingHtmlDocumentBuilder.Next_Image"))); //$NON-NLS-1$
			} else {
				out.link(linkAttributes, next.getSplitTarget(),
						Messages.getString("SplittingHtmlDocumentBuilder.Next")); //$NON-NLS-1$
			}
		}
		out.endBlock();

		out.endBlock(); // navigation row

		// navigation title row
		out.beginBlock(BlockType.TABLE_ROW, new Attributes());

		tableCellAttributes = new TableCellAttributes();
		tableCellAttributes.setAlign("left"); //$NON-NLS-1$
		tableCellAttributes.setValign("top"); //$NON-NLS-1$
		tableCellAttributes.setCssStyle("width: 20%"); //$NON-NLS-1$
		out.beginBlock(BlockType.TABLE_CELL_NORMAL, tableCellAttributes);
		if (previous != null) {
			out.characters(previous.getLabel());
		}
		out.endBlock();

		tableCellAttributes = new TableCellAttributes();
		tableCellAttributes.setAlign("center"); //$NON-NLS-1$
		tableCellAttributes.setCssStyle("width: 60%"); //$NON-NLS-1$
		out.beginBlock(BlockType.TABLE_CELL_NORMAL, tableCellAttributes);

		out.endBlock();

		tableCellAttributes = new TableCellAttributes();
		tableCellAttributes.setAlign("right"); //$NON-NLS-1$
		tableCellAttributes.setValign("top"); //$NON-NLS-1$
		tableCellAttributes.setCssStyle("width: 20%"); //$NON-NLS-1$
		out.beginBlock(BlockType.TABLE_CELL_NORMAL, tableCellAttributes);
		if (next != null) {
			out.characters(next.getLabel());
		}
		out.endBlock();

		out.endBlock(); // navigation title row

		out.endBlock(); // table

		if (header) {
			out.charactersUnescaped("<hr class=\"navigation-separator\"/>"); //$NON-NLS-1$
		}
	}

	private String computeNavImagePath(String imagePath) {
		if (navigationImagePath != null) {
			return navigationImagePath + "/" + imagePath; //$NON-NLS-1$
		}
		return imagePath;
	}

	public String getNavigationImagePath() {
		return navigationImagePath;
	}

	public void setNavigationImagePath(String navigationImagePath) {
		this.navigationImagePath = navigationImagePath;
		if (this.navigationImagePath != null && this.navigationImagePath.endsWith("/")) { //$NON-NLS-1$
			this.navigationImagePath = this.navigationImagePath.substring(0, this.navigationImagePath.length() - 1);
		}
	}

	private void documentHeader() {
		emitNavigation(true);
		emitEmbeddedTableOfContentsHeader();
	}

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
		if (type == SpanType.LINK && attributes instanceof LinkAttributes) {
			LinkAttributes linkAttributes = (LinkAttributes) attributes;
			linkAttributes.setHref(adjustHref(linkAttributes.getHref()));
		}
		out.beginSpan(type, attributes);
	}

	@Override
	public void characters(String text) {
		out.characters(text);
	}

	@Override
	public void charactersUnescaped(String literal) {
		out.charactersUnescaped(literal);
	}

	public void copyConfiguration(HtmlDocumentBuilder other) {
		out.copyConfiguration(other);
	}

	@Override
	public void endBlock() {
		out.endBlock();
	}

	@Override
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

	@Override
	public void flush() {
		if (out != null) {
			out.flush();
		}
	}

	@Override
	public void endHeading() {
		out.endHeading();
	}

	@Override
	public void endSpan() {
		out.endSpan();
	}

	@Override
	public void entityReference(String entity) {
		out.entityReference(entity);
	}

	@Override
	public Locator getLocator() {
		return out.getLocator();
	}

	@Override
	public void image(Attributes attributes, String url) {
		out.image(attributes, url);
	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		href = adjustHref(href);
		out.imageLink(linkAttributes, imageAttributes, href, imageUrl);
	}

	private String adjustHref(String href) {
		if (href != null && href.startsWith("#")) { //$NON-NLS-1$
			href = getHrefOfHeading(href.substring(1));
		}
		return href;
	}

	private String getHrefOfHeading(String headingId) {
		SplitOutlineItem target = outline.getOutlineItemById(headingId);
		if (target != null) {
			SplitOutlineItem pageTarget = target;
			while (pageTarget.getParent() != null
					&& pageTarget.getSplitTarget().equals(pageTarget.getParent().getSplitTarget())) {
				pageTarget = pageTarget.getParent();
			}
			if (!currentFile.getName().equals(pageTarget.getSplitTarget())) {
				String pageHref = pageTarget.getSplitTarget().replace(" ", "%20"); //$NON-NLS-1$//$NON-NLS-2$
				return pageHref + '#' + headingId;
			}
		}
		return '#' + headingId;
	}

	@Override
	public void lineBreak() {
		out.lineBreak();
	}

	@Override
	public void horizontalRule() {
		out.horizontalRule();
	}

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		hrefOrHashName = adjustHref(hrefOrHashName);
		out.link(attributes, hrefOrHashName, text);
	}

	@Override
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

	public void setFormatting(boolean formatting) {
		this.formatting = formatting;
	}

	public boolean isFormatting() {
		return formatting;
	}

	public void setEmbeddedTableOfContents(boolean embeddedTableOfContents) {
		this.embeddedTableOfContents = embeddedTableOfContents;
	}

	public boolean isEmbeddedTableOfContents() {
		return embeddedTableOfContents;
	}

	private void emitEmbeddedTableOfContents() {
		String currentName = currentFile.getName();
		SplitOutlineItem pageItem = null;
		List<SplitOutlineItem> pageOrder = outline.getPageOrder();
		for (SplitOutlineItem item : pageOrder) {
			if (item.getSplitTarget().equals(currentName)) {
				pageItem = item;
			}
		}
		checkState(pageItem != null);
		emitToc(outline, 0);
	}

	private void emitToc(OutlineItem item, int level) {
		if (item.getChildren().isEmpty()) {
			return;
		}
		out.beginBlock(BlockType.NUMERIC_LIST, new Attributes());

		for (OutlineItem child : item.getChildren()) {
			Attributes itemAttributes = new Attributes();
			if (isExpandedInTableOfContents(child)) {
				itemAttributes.appendCssClass("expanded"); //$NON-NLS-1$
			} else {
				itemAttributes.appendCssClass("collapsed"); //$NON-NLS-1$
			}
			out.beginBlock(BlockType.LIST_ITEM, itemAttributes);
			out.link(getHrefOfHeading(child.getId()), child.getLabel());
			emitToc(child, level + 1);
			out.endBlock();
		}
		out.endBlock();
	}

	private boolean isExpandedInTableOfContents(OutlineItem target) {
		SplitOutlineItem pageTarget = (SplitOutlineItem) target;
		while (pageTarget.getParent() != outline && pageTarget.getSplitTarget() == null) {
			pageTarget = pageTarget.getParent();
		}
		if (pageTarget.getSplitTarget() != null && !currentFile.getName().equals(pageTarget.getSplitTarget())) {
			return false;
		}
		return true;
	}

	private void emitEmbeddedTableOfContentsHeader() {
		if (embeddedTableOfContents) {
			beginDiv("container"); //$NON-NLS-1$
			beginDiv("row"); //$NON-NLS-1$

			beginDiv("table-of-contents", "span2"); //$NON-NLS-1$//$NON-NLS-2$

			out.beginHeading(1, new Attributes());
			out.characters(Messages.getString("SplittingHtmlDocumentBuilder.TableOfContentsHeading")); //$NON-NLS-1$
			out.endHeading();
			emitEmbeddedTableOfContents();

			endDiv(); // table of contents

			beginDiv("main-content", "span10"); //$NON-NLS-1$//$NON-NLS-2$
		}
	}

	private void beginDiv(String... cssClasses) {
		Attributes attributes = new Attributes();
		for (String cssClass : cssClasses) {
			attributes.appendCssClass(cssClass);
		}
		out.beginBlock(BlockType.DIV, attributes);
	}

	private void endDiv() {
		out.endBlock();
	}

	private void emitEmbeddedTableOfContentsFooter() {
		if (embeddedTableOfContents) {
			endDiv(); // main content
			endDiv(); // row
			endDiv(); // container
		}
	}
}
