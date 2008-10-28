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
package org.eclipse.mylyn.internal.wikitext.ui.editor.syntax;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.Locator;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;

/**
 * 
 * 
 * @author David Green
 */
public class FastMarkupPartitioner extends FastPartitioner {
	public static final String CONTENT_TYPE_MARKUP = "__markup_block"; //$NON-NLS-1$

	public static final String[] ALL_CONTENT_TYPES = new String[] { CONTENT_TYPE_MARKUP };

	private MarkupLanguage markupLanguage;

	public FastMarkupPartitioner() {
		super(new PartitionTokenScanner(), ALL_CONTENT_TYPES);
	}

	public MarkupLanguage getMarkupLanguage() {
		return markupLanguage;
	}

	public void setMarkupLanguage(MarkupLanguage markupLanguage) {
		this.markupLanguage = markupLanguage;
		getScanner().setMarkupLanguage(markupLanguage);
		resetPartitions();
	}

	PartitionTokenScanner getScanner() {
		return (PartitionTokenScanner) fScanner;
	}

	public void resetPartitions() {
		if (fDocument != null) {
			super.flushRewriteSession();
			initialize();
		} else {
			clearPositionCache();
		}
	}

	static class PartitionTokenScanner implements IPartitionTokenScanner {

		private ITypedRegion[] regions = null;

		private MarkupLanguage markupLanguage;

		private int index = -1;

		private int offsetOfPartitions;

		private int lengthOfPartitions;

		private static class OLP {
			int offset;

			int length;

			ITypedRegion[] partitions;

			public OLP(int offset, int length, ITypedRegion[] partitions) {
				super();
				this.offset = offset;
				this.length = length;
				this.partitions = partitions;
			}
		}

		public ITypedRegion[] computePartitions(IDocument document, int offset, int length) {
			if (offsetOfPartitions <= offset && (offsetOfPartitions + lengthOfPartitions) >= (offset + length)) {
				return regions;
			} else {
				return computeOlp(document, offset, length, -1).partitions;
			}
		}

		public void setPartialRange(IDocument document, int offset, int length, String contentType, int partitionOffset) {
			OLP olp = computeOlp(document, offset, length, partitionOffset);
			regions = olp.partitions;
			offsetOfPartitions = olp.offset;
			lengthOfPartitions = olp.length;
			index = -1;
		}

		private OLP computeOlp(IDocument document, int offset, int length, int partitionOffset) {
			if (markupLanguage == null) {
				return new OLP(offset, length, null);
			}
			int startOffset = partitionOffset == -1 ? offset : Math.min(offset, partitionOffset);
			int endOffset = offset + length;

			MarkupParser markupParser = new MarkupParser(markupLanguage);
			markupLanguage.setBlocksOnly(partitionOffset != -1);
			markupLanguage.setFilterGenerativeContents(true);
			PartitionBuilder partitionBuilder = new PartitionBuilder(startOffset, markupLanguage.isBlocksOnly());
			markupParser.setBuilder(partitionBuilder);

			try {
				markupParser.parse(document.get(startOffset, endOffset - startOffset));
			} catch (BadLocationException e) {
				markupParser.parse(document.get());
			}
			ITypedRegion[] latestPartitions = partitionBuilder.partitions.toArray(new ITypedRegion[partitionBuilder.partitions.size()]);
			List<ITypedRegion> partitioning = new ArrayList<ITypedRegion>(latestPartitions.length);

			ITypedRegion previous = null;
			for (ITypedRegion region : latestPartitions) {
				if (region.getLength() == 0) {
					// ignore 0-length partitions
					continue;
				}
				if (previous != null && region.getOffset() < (previous.getOffset() + previous.getLength())) {
					throw new IllegalStateException();
				}
				previous = region;
				if (region.getOffset() >= startOffset && region.getOffset() < endOffset) {
					partitioning.add(region);
				} else if (region.getOffset() >= (offset + length)) {
					break;
				}
			}
			return new OLP(offset, length, partitioning.toArray(new ITypedRegion[partitioning.size()]));
		}

		public void setMarkupLanguage(MarkupLanguage markupLanguage) {
			this.markupLanguage = markupLanguage;
		}

		public int getTokenLength() {
			return regions[index].getLength();
		}

		public int getTokenOffset() {
			return regions[index].getOffset();
		}

		public IToken nextToken() {
			if (regions == null || ++index >= regions.length) {
				return Token.EOF;
			}
			return new Token(regions[index].getType());
		}

		public void setRange(IDocument document, int offset, int length) {
			setPartialRange(document, offset, length, null, -1);
		}
	}

	public static class MarkupPartition implements ITypedRegion {

		private final Block block;

		private int offset;

		private int length;

		private List<Span> spans;

		private MarkupPartition(Block block, int offset, int length) {
			this.block = block;
			this.offset = offset;
			this.length = length;
		}

		public int getOffset() {
			return offset;
		}

		public int getLength() {
			return length;
		}

		public String getType() {
			return CONTENT_TYPE_MARKUP;
		}

		public Block getBlock() {
			return block;
		}

		public List<Span> getSpans() {
			if (spans == null) {

				List<Span> spans = new ArrayList<Span>();
				getSpans(block, spans);
				this.spans = spans;
			}
			return spans;
		}

		private void getSpans(Block block, List<Span> spans) {
			for (Segment<?> s : block.getChildren().asList()) {
				if (s.getOffset() >= offset && s.getOffset() < (offset + length)) {
					if (s instanceof Span) {
						spans.add((Span) s);
					} else {
						getSpans((Block) s, spans);
					}
				}
			}
		}

		@Override
		public String toString() {
			return String.format("MarkupPartition(type=%s,offset=%s,length=%s,end=%s)", block.getType(), offset, //$NON-NLS-1$
					length, offset + length);
		}
	}

	private static class PartitionBuilder extends DocumentBuilder {

		private final Block outerBlock = new Block(null, 0, Integer.MAX_VALUE / 2);

		private Block currentBlock = outerBlock;

		private Span currentSpan = null;

		private final int offset;

		private List<MarkupPartition> partitions;

		private final boolean blocksOnly;

		public PartitionBuilder(int offset, boolean blocksOnly) {
			this.offset = offset;
			this.blocksOnly = blocksOnly;
		}

		@Override
		public void acronym(String text, String definition) {
		}

		@Override
		public void beginBlock(BlockType type, Attributes attributes) {
			final int newBlockOffset = getLocator().getDocumentOffset() + offset;
			Block newBlock = new Block(type, attributes, newBlockOffset, currentBlock.getLength()
					- (newBlockOffset - currentBlock.getOffset()));
			newBlock.setSpansComputed(!blocksOnly);
			currentBlock.add(newBlock);
			currentBlock = newBlock;
		}

		@Override
		public void beginDocument() {
		}

		@Override
		public void beginHeading(int level, Attributes attributes) {
			final int newBlockOffset = getLocator().getDocumentOffset() + offset;
			Block newBlock = new Block(level, attributes, newBlockOffset, currentBlock.getLength()
					- (newBlockOffset - currentBlock.getOffset()));
			newBlock.setSpansComputed(!blocksOnly);
			currentBlock.add(newBlock);
			currentBlock = newBlock;
		}

		@Override
		public void beginSpan(SpanType type, Attributes attributes) {
			Span span = new Span(type, attributes, getLocator().getDocumentOffset() + offset,
					getLocator().getLineSegmentEndOffset() - getLocator().getLineCharacterOffset());
			if (currentSpan != null) {
				currentSpan.add(span);
				currentSpan = span;
			} else {
				currentSpan = span;
				currentBlock.add(span);
			}
		}

		@Override
		public void characters(String text) {
		}

		@Override
		public void charactersUnescaped(String literal) {
		}

		@Override
		public void endBlock() {
			currentBlock = currentBlock.getParent();
			if (currentBlock == null) {
				throw new IllegalStateException();
			}
		}

		@Override
		public void endDocument() {
			if (currentBlock != outerBlock) {
				throw new IllegalStateException();
			}
			Locator locator = getLocator();
			outerBlock.setLength((locator == null ? 0 : locator.getDocumentOffset()) + offset);

			partitions = new ArrayList<MarkupPartition>();

			for (Segment<?> child : outerBlock.getChildren().asList()) {
				createRegions(null, child);
			}
		}

		public MarkupPartition createRegions(MarkupPartition parent, Segment<?> segment) {
			if (segment.getLength() == 0) {
				return parent;
			}
			if (segment instanceof Block) {
				Block block = (Block) segment;

				if (!filtered(block)) {
					MarkupPartition partition = new MarkupPartition(block, segment.getOffset(), segment.getLength());
					if (parent == null) {
						partitions.add(partition);
					} else {
						// parent needs adjusting to prevent overlap

						// start on the same offset
						if (partition.offset == parent.offset) {
							if (partition.length == parent.length) {
								// same length, so remove parent all together
								partitions.remove(parent);
								partitions.add(partition);
							} else {
								// start on same offset, but new partition is smaller
								// so move parent after new partition
								parent.offset = partition.offset + partition.length;
								partitions.add(partitions.size() - 1, partition);
							}
						} else {
							if (partition.length + partition.offset == parent.length + parent.offset) {
								// end on the same offset, so shrink the parent
								parent.length = partition.offset - parent.offset;
								partitions.add(partition);
							} else {
								// split the parent
								int parentLength = parent.length;
								parent.length = partition.offset - parent.offset;
								final int splitOffset = partition.offset + partition.length;
								MarkupPartition split = new MarkupPartition(parent.block, splitOffset, parent.offset
										+ parentLength - splitOffset);
								partitions.add(partition);
								partitions.add(split);
								parent = split;
							}
						}
					}
					if (!block.getChildren().isEmpty()) {
						for (Segment<?> child : block.getChildren().asList()) {
							partition = createRegions(partition, child);
						}
					}
				}
			}
			return parent;
		}

		private boolean filtered(Block block) {
			if (block.getType() == null) {
				return false;
			}
			switch (block.getType()) {
			case DEFINITION_ITEM:
			case DEFINITION_TERM:
			case LIST_ITEM:
			case TABLE_CELL_HEADER:
			case TABLE_CELL_NORMAL:
			case TABLE_ROW:
				return true;
			case PARAGRAPH:
				// bug 249615: ignore paras that are nested inside a quote block
				if (block.getParent() != null && block.getParent().getType() == BlockType.QUOTE) {
					return true;
				}
				break;
			case BULLETED_LIST:
			case NUMERIC_LIST:
			case DEFINITION_LIST:
				return block.getParent() != null && filtered(block.getParent());
			}
			return false;
		}

		@Override
		public void endHeading() {
			currentBlock = currentBlock.getParent();
			if (currentBlock == null) {
				throw new IllegalStateException();
			}
		}

		@Override
		public void endSpan() {
			if (currentSpan == null) {
				throw new IllegalStateException();
			}
			if (currentSpan.getParent() instanceof Span) {
				currentSpan = (Span) currentSpan.getParent();
			} else {
				currentSpan = null;
			}
		}

		@Override
		public void entityReference(String entity) {
		}

		@Override
		public void image(Attributes attributes, String url) {
		}

		@Override
		public void imageLink(Attributes linkAttributes, Attributes attributes, String href, String imageUrl) {
		}

		@Override
		public void lineBreak() {
		}

		@Override
		public void link(Attributes attributes, String hrefOrHashName, String text) {
		}

	}

	public void reparse(IDocument document, Block block) {
		MarkupParser markupParser = new MarkupParser(markupLanguage);
		markupLanguage.setBlocksOnly(false);
		markupLanguage.setFilterGenerativeContents(true);
		PartitionBuilder partitionBuilder = new PartitionBuilder(block.getOffset(), false);
		markupParser.setBuilder(partitionBuilder);

		try {
			markupParser.parse(document.get(block.getOffset(), block.getLength()));
			for (Segment<?> s : partitionBuilder.outerBlock.getChildren().asList()) {
				if (s.getOffset() == block.getOffset()) {
					if (s instanceof Block) {
						block.replaceChildren(s);
						block.setSpansComputed(true);
						break;
					}
				}
			}
		} catch (BadLocationException e) {
			throw new IllegalStateException(e);
		}

	}

}
