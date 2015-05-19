/*******************************************************************************
 * Copyright (c) 2015, 2016 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Max Rydahl Andersen - Bug 474084: initial API and implementation
 *     Patrik Suzzi <psuzzi@gmail.com> - Bug 474084
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.asciidoc.core.AsciiDocContentState;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * Abstract class for generic asciidoc {@link Block blocks}.
 *
 * @author max
 */
public abstract class AsciiDocBlock extends Block {

	/**
	 * Pattern to identify starting delimiter pattern.
	 */
	protected final Pattern startPattern;

	/**
	 * The delimiter used to start this block. Saved to define what is used as the 'end' since block delimiters are
	 * meant to be balanced.
	 */
	protected String startDelimiter = null;

	protected int blockLineCount = 0;

	private Matcher matcher;

	public AsciiDocBlock(Pattern startPattern) {
		this.startPattern = startPattern;
	}

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			startDelimiter = line;
			processBlockStart();
			++blockLineCount;
		} else if (line.equals(startDelimiter)) {
			setClosed(true);
			return -1;
		} else {
			++blockLineCount;
			processBlockContent(line);
		}

		return -1;
	}

	protected void setStartDelimiter(String startDelimiter) {
		this.startDelimiter = startDelimiter;
	}

	protected String getStartDelimiter() {
		return startDelimiter;
	}

	protected Pattern getStartPattern() {
		return startPattern;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		matcher = startPattern.matcher(line);

		return matcher.matches();
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			processBlockEnd();
		}
		super.setClosed(closed);
	}

	protected abstract void processBlockContent(String line);

	protected abstract void processBlockStart();

	protected abstract void processBlockEnd();

	protected AsciiDocContentState getAsciiDocState() {
		return (AsciiDocContentState) state;
	}
}
