/*******************************************************************************
 * Copyright (c) 2015, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal.blocks;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Validate;
import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.LinePredicates;
import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext;
import org.eclipse.mylyn.wikitext.commonmark.internal.SourceBlock;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;

public class IndentedCodeBlock extends SourceBlock {

	private static final Pattern PATTERN = Pattern.compile("(?: {0,3}\t| {4})(.*)"); //$NON-NLS-1$

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		builder.setLocator(lineSequence.getCurrentLine().toLocator());
		builder.beginBlock(BlockType.CODE, new Attributes());

		boolean blockHasContent = false;
		Iterator<Line> iterator = lineSequence.with(LinePredicates.matches(PATTERN).or(LinePredicates.empty()))
				.iterator();
		while (iterator.hasNext()) {
			Line line = iterator.next();
			Matcher matcher = PATTERN.matcher(line.getText());
			if (!matcher.matches()) {
				Validate.isTrue(line.isEmpty());
				if (iterator.hasNext()) {
					builder.characters("\n"); //$NON-NLS-1$
				}
			} else {
				String content = matcher.group(1);
				if (!content.isEmpty() || blockHasContent && iterator.hasNext()) {
					blockHasContent = true;
					builder.characters(content);
					builder.characters("\n"); //$NON-NLS-1$
				}
			}
		}
		builder.endBlock();
	}

	@Override
	public boolean canStart(LineSequence lineSequence) {
		Line line = lineSequence.getCurrentLine();
		return line != null && PATTERN.matcher(line.getText()).matches();
	}

}
