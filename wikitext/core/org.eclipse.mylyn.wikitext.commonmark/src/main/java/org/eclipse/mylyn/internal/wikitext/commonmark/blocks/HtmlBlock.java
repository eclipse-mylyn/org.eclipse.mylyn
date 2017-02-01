/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.commonmark.blocks;

import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.commonmark.Line;
import org.eclipse.mylyn.internal.wikitext.commonmark.LineSequence;
import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContext;
import org.eclipse.mylyn.internal.wikitext.commonmark.SourceBlock;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

public class HtmlBlock extends SourceBlock {

	private static final String BLOCK_TAG_NAMES = "address|article|aside|base|basefont|blockquote|body|button|canvas|caption|center|col|colgroup|dd|details|dialog|dir|div|dl|dt|embed|fieldset|figcaption|figure|footer|form|frame|frameset|h1|h2|h3|h4|h5|h6|head|header|hgroup|hr|html|iframe|legend|li|link|main|map|menu|menuitem|meta|nav|noframes|object|ol|optgroup|option|output|p|param|progress|section|source|summary|table|tbody|td|textarea|tfoot|th|thead|title|tr|track|ul|video";

	private final Pattern startPattern = Pattern.compile("\\s{0,3}((</?(?:" + BLOCK_TAG_NAMES + ")(\\s|/>|>)?)).*",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		for (Line line = lineSequence.getCurrentLine(); line != null && !line.isEmpty(); lineSequence
				.advance(), line = lineSequence.getCurrentLine()) {
			builder.charactersUnescaped(line.getText());
			builder.charactersUnescaped("\n");
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
