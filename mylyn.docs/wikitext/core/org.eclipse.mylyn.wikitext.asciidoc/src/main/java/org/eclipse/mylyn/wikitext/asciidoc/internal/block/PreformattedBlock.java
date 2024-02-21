/*******************************************************************************
 * Copyright (c) 2015, 2024 Max Rydahl and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *     Max Rydahl Andersen - copied from markdown to get base for asciidoc, Bug 474084
 *     Patrik Suzzi <psuzzi@gmail.com> - Bug 474084
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;

/**
 * AsciiDoc preformatted block.
 *
 * @author Stefan Seelmann
 * @author Max Rydahl Andersen
 */
public class PreformattedBlock extends AsciiDocBlock {

	private static final Pattern startPattern = Pattern.compile("((?: {4}|\\t))((?: {4}|\\t)*)(.*)"); //$NON-NLS-1$

	private Matcher matcher;

	private boolean hasContent;

	public PreformattedBlock() {
		super(startPattern);
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0) {
			matcher = startPattern.matcher(line);
			return matcher.matches();
		}
		return false;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			builder.endBlock();
		}
		super.setClosed(closed);
	}

	@Override
	protected void processBlockStart() {
		builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		String line = startDelimiter;
		setStartDelimiter(matcher.group(1));
		hasContent = false;
		processBlockContent(line);
	}

	@Override
	protected boolean isClosingLine(String line, int offset) {
		return !line.startsWith(getStartDelimiter()) || line.trim().isEmpty();
	}

	@Override
	protected void processBlockContent(String line) {
		// extract the content
		matcher = startPattern.matcher(line);
		if (matcher.matches()) {
			String indent = matcher.group(2);
			String content = matcher.group(3);

			if (hasContent) {
				builder.characters("\n"); //$NON-NLS-1$
			}

			// emit, handle intention, encode ampersands (&) and angle brackets (< and >)
			if (indent != null) {
				builder.characters(indent);
			}
			builder.characters(content);
			hasContent = true;
		}
	}

	@Override
	protected void processBlockEnd() {
		// ignore
	}
}
