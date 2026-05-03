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

/**
 * AsciiDoc Admonition paragraph block
 */
public class AdmonitionParagraphBlock extends ParagraphBlock {

	private static final Pattern PATTERN = Pattern.compile(
			"^(NOTE|TIP|IMPORTANT|WARNING|CAUTION):\\s+(.+)$" //$NON-NLS-1$
			);


	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0 && PATTERN.matcher(line).matches()) {
			return super.canStart(line, lineOffset);
		}
		return false;
	}

	@Override
	protected int processLineContent(String line, int offset) {
		if (getBlockLineCount() == 0) {
			return handleFirstLine(line, offset);
		}

		return super.processLineContent(line, offset);
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
		String content = matcher.group(2);

		emitAdminitionHeader(admonitionType);
		emitAdmonitionTitle(admonitionType);
		builder.characters("\n"); //$NON-NLS-1$
		emitContentStart();
		markupLanguage.emitMarkupLine(getParser(), state, content, 0);
		incrementBlockLineCount();
		return -1;
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

	@Override
	protected void performClosing() {
		builder.characters("\n"); //$NON-NLS-1$
		builder.endBlock(); // </td>
		builder.endBlock(); // </tr>
		builder.endBlock(); // </table>
		builder.endBlock(); // </div>
	}


}
