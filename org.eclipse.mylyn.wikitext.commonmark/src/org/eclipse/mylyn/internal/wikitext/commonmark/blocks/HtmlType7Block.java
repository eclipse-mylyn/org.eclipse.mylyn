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

public class HtmlType7Block extends SourceBlock {

	private static final String ATTRIBUTE_VALUE_QUOTED = "\"[^\"]*\"";

	private static final String ATTRIBUTE_VALUE_SINGLEQUOTED = "'[^']*'";

	private static final String ATTRIBUTE_VALUE_UNQUOTED = "[^\"'<>=]+";

	private static final String ATTRIBUTE_VALUE = "(?:" + ATTRIBUTE_VALUE_QUOTED + "|" + ATTRIBUTE_VALUE_SINGLEQUOTED
			+ "|" + ATTRIBUTE_VALUE_UNQUOTED + ")";

	private static final String ATTRIBUTE_NAME = "[a-zA-Z_][a-zA-Z0-9_:.-]*";

	private static final String ATTRIBUTE = "(?:\\s+" + ATTRIBUTE_NAME + "(?:\\s*=\\s*" + ATTRIBUTE_VALUE + ")?)";

	private final Pattern startPattern = Pattern.compile("<[a-z_][a-zA-Z_:0-9-]*" + ATTRIBUTE + "*/?>\\s*",
			Pattern.CASE_INSENSITIVE);

	private final Pattern closePattern = Pattern.compile("</[a-z_][a-zA-Z_:0-9-]*\\s*/?>\\s*",
			Pattern.CASE_INSENSITIVE);

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
			return startPattern.matcher(line.getText()).matches() || closePattern.matcher(line.getText()).matches();
		}
		return false;
	}
}
