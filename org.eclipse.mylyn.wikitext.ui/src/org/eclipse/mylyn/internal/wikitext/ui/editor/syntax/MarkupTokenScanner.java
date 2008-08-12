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
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.mylyn.internal.wikitext.ui.editor.preferences.Preferences;
import org.eclipse.mylyn.internal.wikitext.ui.editor.syntax.FastMarkupPartitioner.MarkupPartition;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.CssStyleManager;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.FontState;
import org.eclipse.mylyn.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Font;

/**
 * 
 * 
 * @author David Green
 */
public class MarkupTokenScanner implements ITokenScanner {

	private Token currentToken = null;

	private Iterator<Token> tokenIt = null;

	private final CssStyleManager styleManager;

	private final FontState defaultState;

	private final Preferences preferences;

	public MarkupTokenScanner(Font defaultFont) {
		styleManager = new CssStyleManager(defaultFont);
		defaultState = styleManager.createDefaultFontState();

		preferences = WikiTextUiPlugin.getDefault().getPreferences();
	}

	public int getTokenLength() {
		return currentToken == null ? -1 : currentToken.getLength();
	}

	public int getTokenOffset() {
		return currentToken == null ? -1 : currentToken.getOffset();
	}

	public IToken nextToken() {
		if (tokenIt != null && tokenIt.hasNext()) {
			currentToken = tokenIt.next();
		} else {
			currentToken = null;
			tokenIt = null;
			return org.eclipse.jface.text.rules.Token.EOF;
		}
		return currentToken;
	}

	public void setRange(IDocument document, int offset, int length) {
		IDocumentPartitioner partitioner = document.getDocumentPartitioner();
		List<Token> tokens = null;

		if (partitioner instanceof FastMarkupPartitioner) {
			FastMarkupPartitioner fastMarkupPartitioner = (FastMarkupPartitioner) partitioner;
			ITypedRegion[] partitioning = partitioner.computePartitioning(offset, length);
			if (partitioning != null) {
				tokens = new ArrayList<Token>();

				ITypedRegion[] partitions = ((FastMarkupPartitioner) partitioner).getScanner().computePartitions(
						document, offset, length);
				int lastEnd = offset;

				Token defaultToken;
				{
					StyleRange styleRange = styleManager.createStyleRange(defaultState, 0, 1);
					TextAttribute textAttribute = new TextAttribute(styleRange.foreground, styleRange.background,
							styleRange.fontStyle, styleRange.font);
					defaultToken = new Token(defaultState, textAttribute, offset, length);
				}
				if (partitions != null) {
					for (ITypedRegion region : partitions) {
						if (region.getOffset() >= (offset + length)) {
							break;
						}
						if ((region.getOffset() + region.getLength()) < offset) {
							continue;
						}
						if (region instanceof MarkupPartition) {
							MarkupPartition partition = (MarkupPartition) region;

							if (lastEnd < partition.getOffset()) {
								Token blockBridgeToken = new Token(defaultToken.fontState, defaultToken.getData(),
										lastEnd, partition.getOffset() - lastEnd);
								addToken(tokens, blockBridgeToken);
							}

							// a token that spans the whole block
							Token blockToken = createToken(partition);
							if (blockToken == null) {
								blockToken = defaultToken;
							}
							if (!partition.getBlock().isSpansComputed()) {
								fastMarkupPartitioner.reparse(document, partition.getBlock());
							}
							List<Span> spans = partition.getSpans();
							if (spans != null) {
								for (Span span : spans) {
									if (span.getOffset() < lastEnd) {
										continue;
									}
									Token spanToken = createToken(blockToken.getFontState(), span);
									if (spanToken != null) {
										int blockTokenStartOffset = lastEnd < offset ? offset : lastEnd;
										if (blockTokenStartOffset < spanToken.getOffset()) {
											int blockTokenLength = spanToken.getOffset() - blockTokenStartOffset;
											final Token blockBridgeToken = new Token(blockToken.fontState,
													blockToken.getData(), blockTokenStartOffset, blockTokenLength);
											addToken(tokens, blockBridgeToken);
										}
										addToken(tokens, spanToken);
										lastEnd = spanToken.offset + spanToken.length;
										if (lastEnd > partition.getOffset() + partition.getLength()) {
											throw new IllegalStateException();
										}
									}
								}
							}
							final int partitionEnd = partition.getOffset() + partition.getLength();
							if (lastEnd < partitionEnd) {
								final int realLastEnd = Math.max(lastEnd, partition.getOffset());
								int diff = (partitionEnd) - realLastEnd;
								if (diff > 0) {
									int blockTokenStartOffset = realLastEnd;
									int blockTokenLength = diff;
									final Token blockBridgeToken = new Token(blockToken.fontState,
											blockToken.getData(), blockTokenStartOffset, blockTokenLength);
									addToken(tokens, blockBridgeToken);
									lastEnd = blockTokenStartOffset + blockTokenLength;
									if (lastEnd > partition.getOffset() + partition.getLength()) {
										throw new IllegalStateException();
									}
								}
							}
						}
					}
				}
				if (lastEnd < (offset + length)) {
					addToken(tokens, new Token(defaultToken.fontState, defaultToken.getData(), lastEnd, length
							- (lastEnd - offset)));
				}
			}
		}

		currentToken = null;
		if (tokens == null || tokens.isEmpty()) {
			tokenIt = null;
		} else {
			Iterator<Token> it = tokens.iterator();
			while (it.hasNext()) {
				Token next = it.next();
				if (next.getOffset() < offset) {
					it.remove();
				} else if (next.getOffset() + next.getLength() > (offset + length)) {
					it.remove();
				}
			}
			tokenIt = tokens.iterator();
		}

	}

	private void addToken(List<Token> tokens, Token newToken) {
		checkAddToken(tokens, newToken);
		tokens.add(newToken);
	}

	private void checkAddToken(List<Token> tokens, Token newToken) {
		if (newToken.getLength() <= 0) {
			throw new IllegalStateException(String.format("Bad token length %s", newToken.getLength()));
		}
		if (newToken.getOffset() < 0) {
			throw new IllegalStateException(String.format("Bad token offset %s", newToken.getOffset()));
		}
		if (!tokens.isEmpty()) {
			Token previous = tokens.get(tokens.size() - 1);
			if (previous.getOffset() >= newToken.getOffset()) {
				throw new IllegalStateException("New token starts on or before previous", previous.created);
			} else if (previous.getOffset() + previous.getLength() > newToken.getOffset()) {
				throw new IllegalStateException("New token starts before the end of the previous", previous.created);
			}
		}
	}

	private Token createToken(FontState parentState, Span span) {
		if (span.getLength() == 0) {
			return null;
		}
		String cssStyles = null;
		String key = null;
		switch (span.getType()) {
		case BOLD:
			key = Preferences.PHRASE_BOLD;
			break;
		case CITATION:
			key = Preferences.PHRASE_CITATION;
			break;
		case CODE:
			key = Preferences.PHRASE_CODE;
			break;
		case DELETED:
			key = Preferences.PHRASE_DELETED_TEXT;
			break;
		case EMPHASIS:
			key = Preferences.PHRASE_EMPHASIS;
			break;
		case INSERTED:
			key = Preferences.PHRASE_INSERTED_TEXT;
			break;
		case ITALIC:
			key = Preferences.PHRASE_ITALIC;
			break;
		case MONOSPACE:
			key = Preferences.PHRASE_MONOSPACE;
			break;
		case SPAN:
			key = Preferences.PHRASE_SPAN;
			break;
		case STRONG:
			key = Preferences.PHRASE_STRONG;
			break;
		case SUBSCRIPT:
			key = Preferences.PHRASE_SUBSCRIPT;
			break;
		case SUPERSCRIPT:
			key = Preferences.PHRASE_SUPERSCRIPT;
			break;
		case UNDERLINED:
			key = Preferences.PHRASE_UNDERLINED;
			break;
		}
		cssStyles = preferences.getCssByPhraseModifierType().get(key);
		if (cssStyles == null && span.getAttributes().getCssStyle() == null) {
			return null;
		}
		FontState fontState = new FontState(parentState);
		if (cssStyles != null) {
			styleManager.processCssStyles(fontState, parentState, cssStyles);
		}
		if (span.getAttributes().getCssStyle() != null) {
			styleManager.processCssStyles(fontState, parentState, span.getAttributes().getCssStyle());
		}
		StyleRange styleRange = styleManager.createStyleRange(fontState, 0, 1);

		TextAttribute textAttribute = new TextAttribute(styleRange.foreground, styleRange.background,
				styleRange.fontStyle, styleRange.font);
		return new Token(fontState, textAttribute, span.getOffset(), span.getLength());
	}

	private Token createToken(MarkupPartition partition) {
		if (partition.getLength() == 0) {
			return null;
		}
		FontState fontState = new FontState(defaultState);
		boolean hasStyles = processStyles(partition.getBlock(), partition, fontState);

		if (partition.getBlock().getAttributes().getCssStyle() != null) {
			styleManager.processCssStyles(fontState, defaultState, partition.getBlock().getAttributes().getCssStyle());
		} else {
			if (!hasStyles) {
				return null;
			}
		}
		StyleRange styleRange = styleManager.createStyleRange(fontState, 0, 1);

		TextAttribute textAttribute = new TextAttribute(styleRange.foreground, styleRange.background,
				styleRange.fontStyle, styleRange.font);
		return new Token(fontState, textAttribute, partition.getOffset(), partition.getLength());
	}

	private boolean processStyles(Block block, MarkupPartition partition, FontState fontState) {
		boolean hasStyles = false;
		if (block.getParent() != null) {
			hasStyles = processStyles(block.getParent(), partition, fontState);
		}
		String cssStyles = computeCssStyles(block, partition);
		if (cssStyles != null) {
			hasStyles = true;
			styleManager.processCssStyles(fontState, defaultState, cssStyles);
		}
		return hasStyles;
	}

	private String computeCssStyles(Block block, MarkupPartition partition) {
		String cssStyles = null;
		if (block.getHeadingLevel() > 0) {
			cssStyles = preferences.getCssByBlockModifierType().get(
					Preferences.HEADING_PREFERENCES[block.getHeadingLevel()]);
		} else if (block.getType() != null) {
			String key = null;
			switch (block.getType()) {
			case CODE:
				key = Preferences.BLOCK_BC;
				break;
			case QUOTE:
				key = Preferences.BLOCK_QUOTE;
				break;
			case PREFORMATTED:
				key = Preferences.BLOCK_PRE;
				break;
			}
			cssStyles = preferences.getCssByBlockModifierType().get(key);
		}
		return cssStyles;
	}

	private static class Token extends org.eclipse.jface.text.rules.Token {

		private final int offset;

		private final int length;

		private final FontState fontState;

		private final Exception created = new Exception();

		public Token(FontState fontState, TextAttribute attribute, int offset, int length) {
			super(attribute);
			this.fontState = fontState;
			if (offset < 0) {
				throw new IllegalArgumentException();
			}
			if (length < 0) {
				throw new IllegalArgumentException();
			}
			this.offset = offset;
			this.length = length;
		}

		public int getOffset() {
			return offset;
		}

		public int getLength() {
			return length;
		}

		public FontState getFontState() {
			return fontState;
		}

		@Override
		public TextAttribute getData() {
			return (TextAttribute) super.getData();
		}

	}
}
