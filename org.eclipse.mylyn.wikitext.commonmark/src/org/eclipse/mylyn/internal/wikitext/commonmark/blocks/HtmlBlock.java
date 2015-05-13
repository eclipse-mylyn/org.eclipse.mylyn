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

	private static final String BLOCK_TAG_NAMES = "article|header|aside|hgroup|blockquote|hr|iframe|body|li|map|button|object|canvas|ol|caption|output|col|p|colgroup|pre|dd|progress|div|section|dl|table|td|dt|tbody|embed|textarea|fieldset|tfoot|figcaption|th|figure|thead|footer|tr|form|ul|h1|h2|h3|h4|h5|h6|video|script|style";

	private final Pattern startPattern = Pattern.compile("\\s{0,3}((</?(?:" + BLOCK_TAG_NAMES
			+ ")(\\s|/>|>)?)|(<!--)|(-->)|(<\\?)|(\\?>)|(<!\\[CDATA\\[)|(]]>)).*", Pattern.CASE_INSENSITIVE
			| Pattern.MULTILINE);

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		for (Line line = lineSequence.getCurrentLine(); line != null && !line.isEmpty(); lineSequence.advance(), line = lineSequence.getCurrentLine()) {
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
