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
package org.eclipse.mylyn.wikitext.core.parser.outline;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.HeadingAttributes;
import org.eclipse.mylyn.wikitext.core.parser.IdGenerator;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;

/**
 * A parser for creating an outline of a document based on the headings in the document. Uses {@link MarkupLanguage a
 * markup language} to determine where headings start and end.
 * 
 * @see OutlineItem
 * 
 * @author David Green
 */
public class OutlineParser {

	private int labelMaxLength = 0;

	private MarkupLanguage markupLanguage;

	public OutlineParser(MarkupLanguage markupLanguage) {
		this.markupLanguage = markupLanguage;
	}

	public OutlineParser() {
	}

	public int getLabelMaxLength() {
		return labelMaxLength;
	}

	public void setLabelMaxLength(int labelMaxLength) {
		this.labelMaxLength = labelMaxLength;
	}

	public OutlineItem parse(String markup) {
		OutlineItem root = createRootItem();

		return parse(root, markup);
	}

	public MarkupLanguage getMarkupLanguage() {
		return markupLanguage;
	}

	public void setMarkupLanguage(MarkupLanguage markupLanguage) {
		this.markupLanguage = markupLanguage;
	}

	public OutlineItem createRootItem() {
		return createOutlineItem(null, 0, "<root>", -1, 0, "<root>"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public OutlineItem parse(OutlineItem root, String markup) {
		if (markup == null || markup.length() == 0 || markupLanguage == null) {
			return root;
		}

		markupLanguage.setFilterGenerativeContents(true);
		markupLanguage.setBlocksOnly(isBlocksOnly());
		try {
			OutlineBuilder outlineBuilder = (OutlineBuilder) createOutlineUpdater(root);
			outlineBuilder.idGenerator.setGenerationStrategy(markupLanguage.getIdGenerationStrategy());
			MarkupParser markupParser = new MarkupParser();
			markupParser.setBuilder(outlineBuilder);
			markupParser.setMarkupLanaguage(markupLanguage);
			markupParser.parse(markup);
		} finally {
			markupLanguage.setFilterGenerativeContents(false);
			markupLanguage.setBlocksOnly(false);
		}

		return root;
	}

	/**
	 * normally outline parsing is performed only on blocks. Overriding classes may return false if they wish to process
	 * all content.
	 */
	protected boolean isBlocksOnly() {
		return true;
	}

	protected OutlineItem createOutlineItem(OutlineItem current, int level, String id, int offset, int length,
			String label) {
		return new OutlineItem(current, level, id, offset, length, label);
	}

	public DocumentBuilder createOutlineUpdater(OutlineItem rootItem) {
		return new OutlineBuilder(rootItem, labelMaxLength);
	}

	/**
	 * A document builder that creates the outline structure based on headings in the document
	 */
	protected class OutlineBuilder extends DocumentBuilder {

		private OutlineItem currentItem;

		private int level;

		private StringBuilder buf;

		protected final IdGenerator idGenerator = new IdGenerator();

		private int offset;

		private int length;

		private final OutlineItem rootItem;

		private final int labelMaxLength;

		private Attributes attributes;

		public OutlineBuilder(OutlineItem root, int labelMaxLength) {
			super();
			this.currentItem = root;
			rootItem = root;
			this.labelMaxLength = labelMaxLength;
		}

		@Override
		public void acronym(String text, String definition) {
		}

		@Override
		public void beginBlock(BlockType type, Attributes attributes) {
		}

		@Override
		public void beginDocument() {
			rootItem.clear();
			currentItem = rootItem;
		}

		@Override
		public void beginHeading(int level, Attributes attributes) {
			this.level = level;
			this.attributes = attributes;
			buf = new StringBuilder();
			offset = getLocator().getDocumentOffset();
			length = getLocator().getLineLength();
		}

		@Override
		public void beginSpan(SpanType type, Attributes attributes) {
		}

		@Override
		public void characters(String text) {
			if (buf != null) {
				buf.append(text);
			}
		}

		@Override
		public void charactersUnescaped(String literal) {
			if (buf != null) {
				buf.append(literal);
			}
		}

		@Override
		public void endBlock() {
		}

		@Override
		public void endDocument() {
		}

		@Override
		public void endHeading() {
			boolean includeInToc = true;
			if (attributes instanceof HeadingAttributes) {
				HeadingAttributes headingAttributes = (HeadingAttributes) attributes;
				if (headingAttributes.isOmitFromTableOfContents()) {
					includeInToc = false;
				}
			}
			if (includeInToc) {
				String label = buf.toString();
				String fullLabelText = label;
				if (label == null) {
					label = ""; //$NON-NLS-1$
				} else {
					if (labelMaxLength > 0 && label.length() > labelMaxLength) {
						label = label.substring(0, labelMaxLength) + "..."; //$NON-NLS-1$
					}
				}
				String kind = "h" + level; //$NON-NLS-1$

				while (level <= currentItem.getLevel()) {
					currentItem = currentItem.getParent();
				}
				String id = attributes.getId();
				if (id != null) {
					idGenerator.reserveId(id);
				} else {
					id = idGenerator.newId(kind, fullLabelText);
				}
				currentItem = createOutlineItem(currentItem, level, id, offset, length, label);
				currentItem.setTooltip(fullLabelText);
				currentItem.setKind(kind);
			}
			buf = null;
			offset = 0;
			length = 0;
			attributes = null;
		}

		@Override
		public void endSpan() {
		}

		@Override
		public void entityReference(String entity) {
		}

		@Override
		public void image(Attributes attributes, String url) {
		}

		@Override
		public void imageLink(Attributes linkAttributes, Attributes ImageAttributes, String href, String imageUrl) {
		}

		@Override
		public void lineBreak() {
		}

		@Override
		public void link(Attributes attributes, String hrefOrHashName, String text) {
			if (buf != null) {
				buf.append(text);
			}
		}

	}
}
