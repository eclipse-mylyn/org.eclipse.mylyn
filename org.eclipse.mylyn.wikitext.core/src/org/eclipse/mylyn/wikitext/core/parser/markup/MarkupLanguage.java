/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
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
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.core.util.LocationTrackingReader;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;

/**
 * A markup language, which knows its formatting rules and is able to process content based on {@link Block},
 * {@link PatternBasedElementProcessor} and {@link PatternBasedElement} concepts. All markup languages supported by
 * WikiText extend this class.
 * 
 * The MarkupLanguage class provides basic functionality for determining which blocks process which markup content in a
 * particular document. In general multi-line documents are split into consecutive regions called blocks, and each line
 * in a block is processed with spanning sections called phrase modifiers, and tokens within a span are replaced with
 * their respective replacement tokens. These rules apply to most lightweight markup languages, however subclasses may
 * override this default functionality if required. For example, by default phrase modifiers are non-overlapping and
 * non-nested, however if required a subclass could permit such nesting.
 * 
 * Generally markup language classes are not accessed directly by client code, instead client code should configure and
 * call {@link MarkupParser}, accessing the markup language by name using the {@link ServiceLocator}.
 * 
 * @author David Green
 */
public abstract class MarkupLanguage {

	private static final DefaultIdGenerationStrategy DEFAULT_ID_GENERATION_STRATEGY = new DefaultIdGenerationStrategy();

	private String name;

	private String extendsLanguage;

	private boolean filterGenerativeBlocks;

	private boolean blocksOnly;

	protected String internalLinkPattern = "{0}"; //$NON-NLS-1$

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

	/**
	 * get the id strategy employed by this markup language.
	 */
	public IdGenerationStrategy getIdGenerationStrategy() {
		return DEFAULT_ID_GENERATION_STRATEGY;
	}

	public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
		initProcessors();
		ContentState state = createState();
		state.setMarkupContent(markupContent);
		LocationTrackingReader reader = new LocationTrackingReader(new StringReader(markupContent));
		String line;
		Block currentBlock = null;

		DocumentBuilder builder = parser.getBuilder();

		builder.setLocator(state);
		try {
			if (asDocument) {
				builder.beginDocument();
			}

			try {
				while ((line = reader.readLine()) != null) {

					state.setLineNumber(reader.getLineNumber() + 1);
					state.setLineOffset(reader.getLineOffset());
					state.setLineCharacterOffset(0);
					state.setLineSegmentEndOffset(0);
					state.setLineLength(line.length());

					int lineOffset = 0;
					for (;;) {
						if (currentBlock == null) {
							currentBlock = startBlock(line, lineOffset);
							if (currentBlock == null) {
								break;
							}
							currentBlock.setState(state);
							currentBlock.setParser(parser);
						}
						lineOffset = currentBlock.processLineContent(line, lineOffset);
						if (currentBlock.isClosed()) {
							currentBlock = null;
						}
						if (lineOffset < line.length() && lineOffset >= 0) {
							if (currentBlock != null) {
								throw new IllegalStateException(
										"if a block does not fully process a line then it must be closed"); //$NON-NLS-1$
							}
						} else {
							break;
						}
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

			if (asDocument) {
				builder.endDocument();
			}
		} finally {
			builder.setLocator(null);
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

	public abstract List<Block> getBlocks();

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
		if (blocksOnly) {
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
		if (blocksOnly) {
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

	public static class PatternBasedSyntax {
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

	}

	protected abstract PatternBasedSyntax getPhraseModifierSyntax();

	protected abstract PatternBasedSyntax getReplacementTokenSyntax();

	/**
	 * The name of the markup language, typically the same as the name of the markup language supported by this markup
	 * language. This value may be displayed to the user.
	 * 
	 * @return the name, or null if unknown
	 */
	public String getName() {
		return name;
	}

	/**
	 * The name of the markup language, typically the same as the name of the markup language supported by this markup
	 * language. This value may be displayed to the user.
	 * 
	 * @param name
	 *            the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The name of the markup language that is extended by this one
	 * 
	 * @return the name, or null if this markup language does not extend another.
	 */
	public String getExtendsLanguage() {
		return extendsLanguage;
	}

	/**
	 * The name of the markup language that is extended by this one
	 * 
	 * @param extendsLanguage
	 *            the name, or null if this markup language does not extend another.
	 */
	public void setExtendsLanguage(String extendsLanguage) {
		this.extendsLanguage = extendsLanguage;
	}

	@Override
	public MarkupLanguage clone() {
		MarkupLanguage markupLanguage;
		try {
			markupLanguage = getClass().newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		markupLanguage.setName(name);
		return markupLanguage;
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
	 * indicate if the given line is considered 'empty'. The default implementation returns true for lines of length 0,
	 * and for lines whose only content is whitespace.
	 * 
	 * @param line
	 *            the line content
	 * 
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

}
