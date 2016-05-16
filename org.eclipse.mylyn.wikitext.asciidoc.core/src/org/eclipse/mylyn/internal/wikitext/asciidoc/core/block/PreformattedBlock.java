/*******************************************************************************
 * Copyright (c) 2015, 2016 Max Rydahl and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *     Max Rydahl Andersen - copied from markdown to get base for asciidoc, Bug 474084
 *     Patrik Suzzi <psuzzi@gmail.com> - Bug 474084
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

/**
 * AsciiDoc preformatted block.
 *
 * @author Stefan Seelmann
 * @author Max Rydahl Andersen
 */
public class PreformattedBlock extends AsciiDocBlock {

	private static final Pattern startPattern = Pattern.compile("(?: {4}|\\t)((?: {4}|\\t)*)(.*)"); //$NON-NLS-1$

	public PreformattedBlock() {
		super(startPattern);
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0) {
			return startPattern.matcher(line).matches();
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
		processBlockContent(startDelimiter);
	}

	@Override
	protected void processBlockContent(String line) {
		// extract the content
		Matcher matcher = startPattern.matcher(line);
		if (!matcher.matches()) {
			setClosed(true);
			return;
		}
		String indent = matcher.group(1);
		String content = matcher.group(2);

		// emit, handle intention, encode ampersands (&) and angle brackets (< and >)
		if (indent != null) {
			builder.characters(indent);
		}
		builder.characters(content);
		builder.lineBreak();
	}

	@Override
	protected void processBlockEnd() {
		// ignore
	}
}
