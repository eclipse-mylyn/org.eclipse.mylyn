/*******************************************************************************
 * Copyright (c) 2015, 2021 David Green.
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

package org.eclipse.mylyn.wikitext.commonmark.internal.blocks;

import static com.google.common.base.Preconditions.checkState;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.LinePredicates;
import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext;
import org.eclipse.mylyn.wikitext.commonmark.internal.SourceBlock;
import org.eclipse.mylyn.wikitext.commonmark.internal.TextSegment;
import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.InlineParser;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;

public class FencedCodeBlock extends SourceBlock {

	private final Pattern openingFencePattern = Pattern.compile("(\\s{0,4})(`{3,}|~{3,})\\s*(?:([^\\s~`]+)[^~`]*)?");

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		Matcher matcher = openingFencePattern.matcher(lineSequence.getCurrentLine().getText());
		checkState(matcher.matches());
		String indent = matcher.group(1);
		boolean indentedCodeBlock = indent != null && indent.length() == 4;
		Pattern closingFencePattern = closingFencePattern(matcher);

		Attributes codeAttributes = new Attributes();
		addInfoTextCssClass(context, codeAttributes, matcher);

		builder.setLocator(lineSequence.getCurrentLine().toLocator());
		builder.beginBlock(BlockType.CODE, codeAttributes);

		if (indentedCodeBlock) {
			outputLine(builder, indent, lineSequence.getCurrentLine());
		}
		lineSequence.advance();
		for (Line line : lineSequence.with(LinePredicates.matches(closingFencePattern).negate())) {
			outputLine(builder, indent, line);
		}
		if (indentedCodeBlock && lineSequence.getCurrentLine() != null) {
			outputLine(builder, indent, lineSequence.getCurrentLine());
		}
		lineSequence.advance();

		builder.endBlock();
	}

	private void outputLine(DocumentBuilder builder, String indent, Line line) {
		String text = line.getText();
		text = removeIndent(indent, text);
		builder.characters(text);
		builder.characters("\n");
	}

	private String removeIndent(String indent, String text) {
		if (indent != null && indent.length() > 0) {
			Pattern indentPattern = Pattern.compile("\\s{1," + indent.length() + "}(.*)");
			Matcher matcher = indentPattern.matcher(text);
			if (matcher.matches()) {
				return matcher.group(1);
			}
		}
		return text;
	}

	private Pattern closingFencePattern(Matcher matcher) {
		String fence = matcher.group(2);
		char fenceDelimiter = fence.charAt(0);
		return Pattern.compile("\\s{0,3}" + fenceDelimiter + "{" + fence.length() + ",}\\s*");
	}

	private void addInfoTextCssClass(ProcessingContext processingContext, Attributes codeAttributes, Matcher matcher) {
		String infoText = matcher.group(3);
		if (infoText != null && !infoText.isEmpty()) {
			InlineParser inlineParser = processingContext.getInlineParser();
			String language = inlineParser.toStringContent(processingContext,
					new TextSegment(Collections.singletonList(new Line(0, 0, infoText))));
			codeAttributes.setCssClass("language-" + language);
		}
	}

	@Override
	public boolean canStart(LineSequence lineSequence) {
		Line line = lineSequence.getCurrentLine();
		return line != null && openingFencePattern.matcher(line.getText()).matches();
	}

	boolean canEnd(Line line, Line startLine) {
		Matcher matcher = openingFencePattern.matcher(startLine.getText());
		checkState(matcher.matches());
		Pattern closingFencePattern = closingFencePattern(matcher);
		return closingFencePattern.matcher(line.getText()).matches();
	}

}
