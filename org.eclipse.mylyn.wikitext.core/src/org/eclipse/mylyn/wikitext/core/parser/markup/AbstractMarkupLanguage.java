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

package org.eclipse.mylyn.wikitext.core.parser.markup;

import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.ImpliedHyperlinkReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.core.util.LocationTrackingReader;

/**
 * a standard implementation of a markup language usually extends this class, which provides default support for common
 * functionality.
 * 
 * @author David Green
 * @since 2.0
 */
public abstract class AbstractMarkupLanguage extends MarkupLanguage {

	// we use the template pattern for creating new blocks
	protected List<Block> blocks = new ArrayList<Block>();

	protected List<Block> paragraphBreakingBlocks = new ArrayList<Block>();

	protected PatternBasedSyntax tokenSyntax = new PatternBasedSyntax();

	protected PatternBasedSyntax phraseModifierSyntax = new PatternBasedSyntax();

	private boolean syntaxInitialized = false;

	/**
	 * @since 2.0
	 */
	protected MarkupLanguageConfiguration configuration;

	private boolean blocksOnly;

	private boolean filterGenerativeBlocks;

	/**
	 * @since 2.0
	 */
	protected String internalLinkPattern = "{0}"; //$NON-NLS-1$

	private boolean enableMacros = true;

	/**
	 * @since 2.0
	 */
	public static final class PatternBasedSyntax {
		protected List<PatternBasedElement> elements = new ArrayList<PatternBasedElement>();

		protected Pattern elementPattern;

		protected List<Integer> elementGroup = new ArrayList<Integer>();

		private final StringBuilder patternBuffer = new StringBuilder();

		private int patternGroup = 0;

		private final Stack<Group> groups = new Stack<Group>();
		{
			groups.push(new Group());
		}

		public PatternBasedSyntax() {
		}

		public void add(PatternBasedElement element) {
			elementPattern = null;
			elements.add(element);
			if (groups.peek().count++ > 0) {
				patternBuffer.append('|');
			}
			++patternGroup;
			patternBuffer.append('(');
			patternBuffer.append(element.getPattern(patternGroup));
			patternBuffer.append(')');
			elementGroup.add(patternGroup);
			patternGroup += element.getPatternGroupCount();
		}

		/**
		 * 
		 */
		protected List<PatternBasedElement> getElements() {
			return Collections.unmodifiableList(elements);
		}

		public void beginGroup(String regexFragment, int size) {
			add(regexFragment, size, true);
		}

		public void endGroup(String regexFragment, int size) {
			add(regexFragment, size, false);
		}

		private void add(String regexFragment, int size, boolean beginGroup) {
			elementPattern = null;
			if (beginGroup) {
				if (groups.peek().count++ > 0) {
					patternBuffer.append('|');
				}
				groups.push(new Group());
				patternBuffer.append("(?:"); //$NON-NLS-1$
			} else {
				groups.pop();
			}
			patternBuffer.append(regexFragment);
			if (!beginGroup) {
				patternBuffer.append(")"); //$NON-NLS-1$
			}
			patternGroup += size;
		}

		public PatternBasedElementProcessor findPatternBasedElement(String lineText, int offset) {
			Matcher matcher = getPattern().matcher(lineText);
			if (offset > 0) {
				matcher.region(offset, lineText.length());
			}
			if (matcher.find()) {
				int size = elementGroup.size();
				for (int x = 0; x < size; ++x) {
					int group = elementGroup.get(x);
					String value = matcher.group(group);
					if (value != null) {
						PatternBasedElement element = elements.get(x);
						PatternBasedElementProcessor processor = element.newProcessor();
						processor.setLineStartOffset(matcher.start());
						processor.setLineEndOffset(matcher.end());
						processor.setGroup(0, matcher.group(0), matcher.start(0), matcher.end(0));
						for (int y = 0; y < element.getPatternGroupCount(); ++y) {
							final int groupIndex = group + y + 1;
							processor.setGroup(y + 1, matcher.group(groupIndex), matcher.start(groupIndex),
									matcher.end(groupIndex));
						}
						return processor;
					}
				}
				throw new IllegalStateException();
			} else {
				return null;
			}
		}

		public Pattern getPattern() {
			if (elementPattern == null) {
				if (patternBuffer.length() > 0) {
					elementPattern = Pattern.compile(patternBuffer.toString());
				} else {
					return null;
				}
			}
			return elementPattern;
		}

		public void clear() {
			elements.clear();
			elementPattern = null;
			elementGroup.clear();
			patternBuffer.delete(0, patternBuffer.length());
			patternGroup = 0;
			groups.clear();
			groups.push(new Group());
		}

	}

	private static class LineState {
		int lineOffset;

		String line;

		public LineState(String line, int offset) {
			this.line = line;
			lineOffset = offset;
		}
	}

	@Override
	public void configure(MarkupLanguageConfiguration configuration) throws UnsupportedOperationException {
		this.configuration = configuration;
		initializeSyntax(true);
	}

	private void initializeSyntax(boolean force) {
		if (force || !syntaxInitialized) {
			syntaxInitialized = true;
			initializeSyntax();
		}
	}

	/**
	 * Create new state for tracking a document and its contents during a parse session. Subclasses may override this
	 * method to provide additional state tracking capability.
	 * 
	 * @return the new state.
	 */
	protected ContentState createState() {
		ContentState contentState = new ContentState();
		contentState.getIdGenerator().setGenerationStrategy(getIdGenerationStrategy());
		return contentState;
	}

	@Override
	public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
		initializeSyntax(false);
		initProcessors();
		ContentState state = createState();
		state.setMarkupContent(markupContent);

		DocumentBuilder builder = parser.getBuilder();
		builder.setLocator(state);

		@SuppressWarnings("resource")
		LocationTrackingReader reader = new LocationTrackingReader(new StringReader(markupContent));
		try {
			if (asDocument) {
				builder.beginDocument();
			}
			Stack<Block> nestedBlocks = null;
			Stack<LineState> lineStates = null;
			String line;
			Block currentBlock = null;
			try {
				line = reader.readLine();
				int lineOffset = 0;
				while (line != null) {

					state.setLineNumber(reader.getLineNumber() + 1);
					state.setLineOffset(reader.getLineOffset());
					state.setLineCharacterOffset(lineOffset);
					state.setLineSegmentEndOffset(0);
					state.setLineLength(line.length());

					for (;;) {
						popClosedBlocks(nestedBlocks);
						if (nestedBlocks != null && !nestedBlocks.isEmpty()) {
							Block nestedParent = nestedBlocks.peek();
							int closeOffset = nestedParent.findCloseOffset(line, lineOffset);
							if (closeOffset != -1) {
								if (closeOffset > lineOffset) {
									String truncatedLine = line.substring(0, closeOffset);
									if (lineStates == null) {
										lineStates = new Stack<LineState>();
									}
									lineStates.push(new LineState(line, closeOffset));
									line = truncatedLine;
								} else {
									if (currentBlock != null) {
										currentBlock.setClosed(true);
										currentBlock = null;
									}
									currentBlock = nestedBlocks.pop();
									lineOffset = closeOffset;
									state.setLineCharacterOffset(lineOffset);
								}
							}
						}
						if (currentBlock == null) {
							if (nestedBlocks != null && !nestedBlocks.isEmpty()) {
								Block nestedParent = nestedBlocks.peek();
								if (nestedParent.canResume(line, lineOffset)) {
									currentBlock = nestedParent;
								}
							}
							if (currentBlock == null) {
								currentBlock = startBlock(line, lineOffset);
								if (currentBlock == null) {
									break;
								}
								currentBlock.setMarkupLanguage(this);
								currentBlock.setState(state);
								currentBlock.setParser(parser);
							}
						}
						lineOffset = currentBlock.processLineContent(line, lineOffset);
						if (currentBlock.isClosed()) {
							currentBlock = null;
						} else if (currentBlock.beginNesting()) {
							if (nestedBlocks == null) {
								nestedBlocks = new Stack<Block>();
							}
							nestedBlocks.push(currentBlock);
							currentBlock = null;
						}
						if (lineOffset < line.length() && lineOffset >= 0) {
							if (currentBlock != null) {
								throw new IllegalStateException(
										String.format(
												"if a block does not fully process a line then it must be closed, at or near line %s lineOffset %s, block %s", reader.getLineNumber(), lineOffset, currentBlock.getClass().getName())); //$NON-NLS-1$
							}
						} else {
							break;
						}
					}
					if (lineStates != null && !lineStates.isEmpty()) {
						LineState lineState = lineStates.pop();
						line = lineState.line;
						lineOffset = lineState.lineOffset;
					} else {
						lineOffset = 0;
						line = reader.readLine();
					}
				}
				state.setLineNumber(reader.getLineNumber() + 1);
				state.setLineOffset(reader.getLineOffset());
				state.setLineCharacterOffset(0);
				state.setLineLength(0);

			} catch (IOException e) {
				throw new IllegalStateException(e);
			}

			if (currentBlock != null && !currentBlock.isClosed()) {
				currentBlock.setClosed(true);
			}
			if (nestedBlocks != null) {
				while (!nestedBlocks.isEmpty()) {
					Block block = nestedBlocks.pop();
					if (!block.isClosed()) {
						block.setClosed(true);
					}
				}
				nestedBlocks = null;
			}

			if (asDocument) {
				builder.endDocument();
			}
		} finally {
			builder.setLocator(null);
		}
	}

	private void popClosedBlocks(Stack<Block> blocks) {
		while (blocks != null && !blocks.isEmpty()) {
			Block block = blocks.peek();
			if (block.isClosed()) {
				blocks.pop();
			} else {
				break;
			}
		}
	}

	private void initProcessors() {
		for (Block block : getBlocks()) {
			if (block.getMarkupLanguage() != null) {
				return;
			}
			block.setMarkupLanguage(this);
		}
	}

	public Block startBlock(String line, int lineOffset) {
		if (isEmptyLine(line)) {
			// nothing starts on an empty line
			return null;
		}
		for (Block block : getBlocks()) {
			if (block.canStart(line, lineOffset)) {
				return block.clone();
			}
		}
		return null;
	}

	/**
	 * indicate if the given line is considered 'empty'. The default implementation returns true for lines of length 0,
	 * and for lines whose only content is whitespace.
	 * 
	 * @param line
	 *            the line content
	 * @return true if the given line is considered empty by this markup language
	 */
	public boolean isEmptyLine(String line) {
		if (line.length() == 0) {
			return true;
		}
		for (int x = 0; x < line.length(); ++x) {
			if (!Character.isWhitespace(line.charAt(x))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Emit a markup line that may contain phrase modifiers and replacement tokens, but no block modifiers.
	 * 
	 * @param parser
	 * @param state
	 * @param textLineOffset
	 *            the offset of the provided text in the current line
	 * @param line
	 *            the text to process
	 * @param offset
	 *            the offset in the <code>text</code> at which processing should begin
	 */
	public void emitMarkupLine(MarkupParser parser, ContentState state, int textLineOffset, String line, int offset) {
		if (offset == line.length()) {
			return;
		}
		if (isBlocksOnly()) {
			emitMarkupText(parser, state, line.substring(offset));
			return;
		}
		int previousShift = state.getShift();
		state.setShift(previousShift + textLineOffset);
		for (;;) {
			PatternBasedElementProcessor phraseModifier = getPhraseModifierSyntax().findPatternBasedElement(line,
					offset);
			if (phraseModifier != null) {
				int newOffset = phraseModifier.getLineStartOffset();
				if (offset < newOffset) {
					state.setLineCharacterOffset(state.getShift() + offset);
					state.setLineSegmentEndOffset(state.getShift() + newOffset);
					String text = line.substring(offset, newOffset);
					emitMarkupText(parser, state, text);
				}
				phraseModifier.setMarkupLanguage(this);
				phraseModifier.setParser(parser);
				phraseModifier.setState(state);
				state.setLineCharacterOffset(state.getShift() + phraseModifier.getLineStartOffset());
				state.setLineSegmentEndOffset(state.getShift() + phraseModifier.getLineEndOffset());
				phraseModifier.emit();
				offset = phraseModifier.getLineEndOffset();
				if (offset >= line.length()) {
					break;
				}
			} else {
				state.setLineCharacterOffset(state.getShift() + offset);
				state.setLineSegmentEndOffset(state.getShift() + line.length());
				emitMarkupText(parser, state, line.substring(offset));
				break;
			}
		}
		state.setShift(previousShift);
	}

	/**
	 * Emit a markup line that may contain phrase modifiers and replacement tokens, but no block modifiers.
	 * 
	 * @param parser
	 * @param state
	 * @param line
	 * @param offset
	 */
	public void emitMarkupLine(MarkupParser parser, ContentState state, String line, int offset) {
		emitMarkupLine(parser, state, 0, line, offset);
	}

	/**
	 * Emit markup that may contain replacement tokens but no phrase or block modifiers.
	 * 
	 * @param parser
	 * @param state
	 * @param text
	 */
	public void emitMarkupText(MarkupParser parser, ContentState state, String text) {
		if (isBlocksOnly()) {
			parser.getBuilder().characters(text);
			return;
		}
		int offset = 0;
		for (;;) {
			PatternBasedElementProcessor tokenReplacement = getReplacementTokenSyntax().findPatternBasedElement(text,
					offset);
			if (tokenReplacement != null) {
				int newOffset = tokenReplacement.getLineStartOffset();
				if (offset < newOffset) {
					String text2 = text.substring(offset, newOffset);
					emitMarkupText(parser, state, text2);
				}
				tokenReplacement.setMarkupLanguage(this);
				tokenReplacement.setParser(parser);
				tokenReplacement.setState(state);

				state.setLineCharacterOffset(state.getShift() + tokenReplacement.getLineStartOffset());
				state.setLineSegmentEndOffset(state.getShift() + tokenReplacement.getLineEndOffset());

				tokenReplacement.emit();
				offset = tokenReplacement.getLineEndOffset();
				if (offset >= text.length()) {
					break;
				}
			} else {
				parser.getBuilder().characters(offset > 0 ? text.substring(offset) : text);
				break;
			}
		}
	}

	private static class Group {
		int count;
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	public final List<Block> getParagraphBreakingBlocks() {
		return paragraphBreakingBlocks;
	}

	/**
	 * initialize the syntax of the markup language.
	 */
	private void initializeSyntax() {
		if (!blocks.isEmpty()) {
			clearLanguageSyntax();
		}
		initializeBlocks();
		initializePhraseModifiers();
		initializeTokens();
	}

	protected void clearLanguageSyntax() {
		blocks.clear();
		paragraphBreakingBlocks.clear();
		tokenSyntax.clear();
		phraseModifierSyntax.clear();
	}

	protected final void initializeTokens() {
		addStandardTokens(tokenSyntax);
		addTokenExtensions(tokenSyntax);
		if (configuration != null) {
			configuration.addTokenExtensions(tokenSyntax);
		}
	}

	protected final void initializePhraseModifiers() {
		addStandardPhraseModifiers(phraseModifierSyntax);
		addPhraseModifierExtensions(phraseModifierSyntax);
		if (configuration != null) {
			configuration.addPhraseModifierExtensions(phraseModifierSyntax);
		}
	}

	protected final void initializeBlocks() {
		addStandardBlocks(blocks, paragraphBreakingBlocks);
		// extensions
		addBlockExtensions(blocks, paragraphBreakingBlocks);
		if (configuration != null) {
			configuration.addBlockExtensions(blocks, paragraphBreakingBlocks);
		}
		// ~extensions

		blocks.add(createParagraphBlock()); // ORDER DEPENDENCY: this must come last
	}

	/**
	 * @since 2.0
	 */
	protected abstract void addStandardTokens(PatternBasedSyntax tokenSyntax);

	/**
	 * @since 2.0
	 */
	protected abstract void addStandardPhraseModifiers(PatternBasedSyntax phraseModifierSyntax);

	protected abstract void addStandardBlocks(List<Block> blocks, List<Block> paragraphBreakingBlocks);

	protected abstract Block createParagraphBlock();

	/**
	 * subclasses may override this method to add blocks to the language. Overriding classes should call
	 * <code>super.addBlockExtensions(blocks,paragraphBreakingBlocks)</code> if the default language extensions are
	 * desired.
	 * 
	 * @param blocks
	 *            the list of blocks to which extensions may be added
	 * @param paragraphBreakingBlocks
	 *            the list of blocks that end a paragraph
	 */
	protected void addBlockExtensions(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
		// no block extensions
	}

	/**
	 * subclasses may override this method to add tokens to the language. Overriding classes should call
	 * <code>super.addTokenExtensions(tokenSyntax)</code> if the default language extensions are desired.
	 * 
	 * @param tokenSyntax
	 *            the token syntax
	 * @since 2.0
	 */
	protected void addTokenExtensions(PatternBasedSyntax tokenSyntax) {
		// no token extensions
	}

	/**
	 * subclasses may override this method to add phrases to the language. Overriding classes should call
	 * <code>super.addPhraseModifierExtensions(phraseModifierSyntax)</code> if the default language extensions are
	 * desired.
	 * 
	 * @param phraseModifierSyntax
	 *            the phrase modifier syntax
	 * @since 2.0
	 */
	protected void addPhraseModifierExtensions(PatternBasedSyntax phraseModifierSyntax) {
		// no phrase extensions
	}

	/**
	 * Indicate if this markup language detects 'raw' hyperlinks; that is hyperlinks without any special markup. The
	 * default implementation checks the markup syntax for use of {@link ImpliedHyperlinkReplacementToken} and returns
	 * true if it is in the syntax.
	 * 
	 * @return true if raw hyperlinks are detected by this markup language, otherwise false.
	 */
	public boolean isDetectingRawHyperlinks() {
		initializeSyntax(false);
		PatternBasedSyntax replacementTokenSyntax = getReplacementTokenSyntax();
		if (replacementTokenSyntax != null) {
			for (PatternBasedElement element : replacementTokenSyntax.getElements()) {
				if (element instanceof ImpliedHyperlinkReplacementToken) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @since 2.0
	 */
	protected PatternBasedSyntax getPhraseModifierSyntax() {
		return phraseModifierSyntax;
	}

	/**
	 * @since 2.0
	 */
	protected PatternBasedSyntax getReplacementTokenSyntax() {
		return tokenSyntax;
	}

	/**
	 * Indicate if generative contents should be filtered. This option is used with the {@link OutlineParser}.
	 */
	public boolean isFilterGenerativeContents() {
		return filterGenerativeBlocks;
	}

	/**
	 * Indicate if table of contents should be filtered. This option is used with the {@link OutlineParser}.
	 */
	public void setFilterGenerativeContents(boolean filterGenerativeBlocks) {
		this.filterGenerativeBlocks = filterGenerativeBlocks;
	}

	/**
	 * indicate if the parser should detect blocks only. This is useful for use in a document partitioner where the
	 * partition boundaries are defined by blocks.
	 */
	public boolean isBlocksOnly() {
		return blocksOnly;
	}

	/**
	 * indicate if the parser should detect blocks only. This is useful for use in a document partitioner where the
	 * partition boundaries are defined by blocks.
	 */
	public void setBlocksOnly(boolean blocksOnly) {
		this.blocksOnly = blocksOnly;
	}

	/**
	 * The pattern to use when creating hyperlink targets for internal links. The pattern is implementation-specific,
	 * however implementations are encouraged to use {@link MessageFormat}, where the 0th parameter is the internal
	 * link.
	 * 
	 * @see MessageFormat
	 */
	public String getInternalLinkPattern() {
		return internalLinkPattern;
	}

	/**
	 * The pattern to use when creating hyperlink targets for internal links. The pattern is implementation-specific,
	 * however implementations are encouraged to use {@link MessageFormat}, where the 0th parameter is the internal
	 * link.
	 * 
	 * @see MessageFormat
	 */
	public void setInternalLinkPattern(String internalLinkPattern) {
		this.internalLinkPattern = internalLinkPattern;
	}

	/**
	 * Indicate if macro processing is enabled. Generally such processing is enabled except when used in a source
	 * editor.
	 * <p>
	 * Macros are defined as text substitution prior to normal processing. Such preprocessing changes the markup before
	 * it is processed, and as such has the side-effect of changing computed offsets when parsing markup.
	 * </p>
	 * <p>
	 * The default value is true.
	 * </p>
	 * 
	 * @return true if macros are enabled, otherwise false
	 */
	public boolean isEnableMacros() {
		return enableMacros;
	}

	/**
	 * Indicate if macro processing is enabled. Generally such processing is enabled except when used in a source
	 * editor.
	 * <p>
	 * Macros are defined as text substitution prior to normal processing. Such preprocessing changes the markup before
	 * it is processed, and as such has the side-effect of changing computed offsets when parsing markup.
	 * </p>
	 * 
	 * @param enableMacros
	 *            true if macros are enabled, otherwise false
	 */
	public void setEnableMacros(boolean enableMacros) {
		this.enableMacros = enableMacros;
	}

	@Override
	public MarkupLanguage clone() {
		AbstractMarkupLanguage copy = (AbstractMarkupLanguage) super.clone();
		copy.configuration = configuration == null ? null : configuration.clone();
		copy.internalLinkPattern = internalLinkPattern;
		copy.enableMacros = enableMacros;
		return copy;
	}
}
