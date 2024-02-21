/*******************************************************************************
 * Copyright (c) 2015, 2024 David Green and other.
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
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal.blocks;

import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext;
import org.eclipse.mylyn.wikitext.commonmark.internal.SourceBlock;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;

public class HtmlBlock extends SourceBlock {

	private static final String BLOCK_TAG_NAMES = "address|article|aside|base|basefont|blockquote|body|button|canvas|caption|center|col|colgroup|dd|details|dialog|dir|div|dl|dt|embed|fieldset|figcaption|figure|footer|form|frame|frameset|h1|h2|h3|h4|h5|h6|head|header|hgroup|hr|html|iframe|legend|li|link|main|map|menu|menuitem|meta|nav|noframes|object|ol|optgroup|option|output|p|param|progress|section|source|summary|table|tbody|td|textarea|tfoot|th|thead|title|tr|track|ul|video"; //$NON-NLS-1$

	private final Pattern startPattern = Pattern.compile("\\s{0,3}((</?(?:" + BLOCK_TAG_NAMES + ")(\\s|/>|>)?)).*", //$NON-NLS-1$ //$NON-NLS-2$
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		for (Line line = lineSequence.getCurrentLine(); line != null && !line.isEmpty(); lineSequence
				.advance(), line = lineSequence.getCurrentLine()) {
			builder.charactersUnescaped(line.getText());
			builder.charactersUnescaped("\n"); //$NON-NLS-1$
		}
	}

	@Override
	public boolean canStart(LineSequence lineSequence) {
		Line line = lineSequence.getCurrentLine();
		if (line != null) {
			return startPattern.matcher(line.getText()).matches();
		}
		return false;
	}

}
