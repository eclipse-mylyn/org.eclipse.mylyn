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
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal.blocks;

import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext;
import org.eclipse.mylyn.wikitext.commonmark.internal.SourceBlock;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;

public class HtmlType7Block extends SourceBlock {

	private static final String ATTRIBUTE_VALUE_QUOTED = "\"[^\"]*\""; //$NON-NLS-1$

	private static final String ATTRIBUTE_VALUE_SINGLEQUOTED = "'[^']*'"; //$NON-NLS-1$

	private static final String ATTRIBUTE_VALUE_UNQUOTED = "[^ \"'=<>`]+"; //$NON-NLS-1$

	private static final String ATTRIBUTE_VALUE = "(?:" + ATTRIBUTE_VALUE_QUOTED + "|" + ATTRIBUTE_VALUE_SINGLEQUOTED //$NON-NLS-1$ //$NON-NLS-2$
			+ "|" + ATTRIBUTE_VALUE_UNQUOTED + ")"; //$NON-NLS-1$ //$NON-NLS-2$

	private static final String ATTRIBUTE_NAME = "[a-z_:][a-z0-9_.:-]*"; //$NON-NLS-1$

	private static final String ATTRIBUTE = "(?:" + ATTRIBUTE_NAME + "(?:\\s*=\\s*" + ATTRIBUTE_VALUE + ")?)"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private static final String REPEATING_ATTRIBUTE = "(?:\\s+" + ATTRIBUTE + ")*"; //$NON-NLS-1$ //$NON-NLS-2$

	private static final String HTML_TAG_NAME = "([a-z][a-z0-9-]*)"; //$NON-NLS-1$

	private final Pattern startPattern = Pattern.compile(
			"\\s{0,3}<" + HTML_TAG_NAME + REPEATING_ATTRIBUTE + "\\s*/?>\\s*", //$NON-NLS-1$ //$NON-NLS-2$
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	private final Pattern closePattern = Pattern.compile("\\s{0,3}</" + HTML_TAG_NAME + "\\s*>\\s*", //$NON-NLS-1$ //$NON-NLS-2$
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		Line line = lineSequence.getCurrentLine();
		while (line != null && !line.isEmpty()) {
			builder.charactersUnescaped(line.getText());
			builder.charactersUnescaped("\n"); //$NON-NLS-1$

			lineSequence.advance();
			line = lineSequence.getCurrentLine();
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
