/*******************************************************************************
 * Copyright (c) 2026 Johannes Kepler University Linz
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alois Zoitl - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.markup.Block;

/**
 * AsciiDoc Admonition block
 */
public class AdmonitionBlock extends Block {

	private static final Pattern PATTERN = Pattern.compile(
			"^\\[(NOTE|TIP|IMPORTANT|WARNING|CAUTION)\\]\\s*$" //$NON-NLS-1$
			);

	private static final String DELIMITER = "===="; //$NON-NLS-1$

	private int blockLineCount = 0;
	private boolean hasTitle;

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0 && PATTERN.matcher(line).matches()) {
			hasTitle = false;
			blockLineCount = 0;
			return true;
		}
		return false;
	}

	@Override
	protected int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			return handleFirstLine(line, offset);
		} else if (blockLineCount == 1) {
			return handleSecondLine(line, offset);
		} else if (blockLineCount == 2 && hasTitle) {
			if (isDelimiter(line, offset)) {
				emitContentParagraphStart();
				blockLineCount++;
				return -1;
			}
			setClosed(true);
			return 0;
		}

		if (isDelimiter(line, offset)) {
			// closing delimiter
			setClosed(true);
			return -1;
		}

		return 0; // indicate that a nested block should handle the content
	}

	@Override
	public boolean beginNesting() {
		if (hasTitle) {
			return blockLineCount > 2;
		}
		return blockLineCount > 1;
	}

	@Override
	public int findCloseOffset(String line, int lineOffset) {
		if (beginNesting() && isDelimiter(line, lineOffset)) {
			return 0;
		}
		return -1;
	}

	@Override
	public boolean canResume(String line, int lineOffset) {
		return isDelimiter(line, lineOffset);
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			performClosing();
			builder.characters("\n"); //$NON-NLS-1$
		}
		super.setClosed(closed);
	}

	private int handleFirstLine(String line, int offset) {
		if (offset != 0) {
			// admonitions always start at the beginning
			return 0;
		}

		Matcher matcher = PATTERN.matcher(line);
		if (!matcher.matches()) {
			return 0;
		}
		String admonitionType = matcher.group(1);

		emitAdminitionHeader(admonitionType);
		emitAdmonitionTitle(admonitionType);
		builder.characters("\n"); //$NON-NLS-1$
		emitContentStart();
		blockLineCount++;
		return -1;
	}

	private int handleSecondLine(String line, int offset) {
		if (line.charAt(0) == '.') {
			Attributes titleAtt = new Attributes();
			titleAtt.setCssClass("title"); //$NON-NLS-1$
			builder.beginBlock(BlockType.DIV, titleAtt);
			builder.characters(line.substring(1));
			builder.endBlock();
			builder.characters("\n"); //$NON-NLS-1$
			blockLineCount++;
			hasTitle = true;
			return -1;
		}

		if (isDelimiter(line, offset)) {
			emitContentParagraphStart();
			blockLineCount++;
			return -1;
		}

		setClosed(true);
		return 0;
	}


	private boolean isDelimiter(String line, int offset) {
		return offset == 0 && DELIMITER.equals(line.trim());
	}

	private void emitAdmonitionTitle(String admonitionType) {
		Attributes iconAtt = new Attributes();
		iconAtt.setCssClass("icon"); //$NON-NLS-1$
		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, iconAtt);
		Attributes titleAtt = new Attributes();
		titleAtt.setCssClass("title"); //$NON-NLS-1$
		builder.beginBlock(BlockType.DIV, titleAtt);
		builder.characters(admonitionType.substring(0, 1).toUpperCase());
		builder.characters(admonitionType.substring(1).toLowerCase());
		builder.endBlock();
		builder.endBlock(); // end of icon/header cell
	}

	private void emitAdminitionHeader(String admonitionType) {
		Attributes attributes = new Attributes();
		attributes.setCssClass("admonitionblock " + admonitionType.toLowerCase()); //$NON-NLS-1$
		builder.beginBlock(BlockType.DIV, attributes);
		builder.beginBlock(BlockType.TABLE, new Attributes());
		builder.beginBlock(BlockType.TABLE_ROW, new Attributes());
	}

	private void emitContentStart() {
		Attributes contentAtt = new Attributes();
		contentAtt.setCssClass("content"); //$NON-NLS-1$
		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, contentAtt);
		builder.characters("\n"); //$NON-NLS-1$
	}

	private void emitContentParagraphStart() {
		Attributes contentAtt = new Attributes();
		contentAtt.setCssClass("paragraph"); //$NON-NLS-1$
		builder.beginBlock(BlockType.DIV, contentAtt);
		builder.characters("\n"); //$NON-NLS-1$
	}

	private void performClosing() {
		if (beginNesting()) {
			//only when we are in the begin nesting state this block has been added
			builder.endBlock(); // </div> for content paragraph
		}
		builder.endBlock(); // </td>
		builder.endBlock(); // </tr>
		builder.endBlock(); // </table>
		builder.endBlock(); // </div>
	}


}
