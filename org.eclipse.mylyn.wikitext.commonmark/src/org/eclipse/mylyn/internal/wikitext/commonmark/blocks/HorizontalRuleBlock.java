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

public class HorizontalRuleBlock extends SourceBlock {

	private final Pattern startPattern = Pattern.compile("\\s{0,3}((_\\s*){3,}|(-\\s*){3,}|(\\*\\s*){3,})");

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		builder.setLocator(lineSequence.getCurrentLine().toLocator());
		builder.horizontalRule();
		lineSequence.advance();
	}

	@Override
	public boolean canStart(LineSequence lineSequence) {
		Line line = lineSequence.getCurrentLine();
		return line != null && startPattern.matcher(line.getText()).matches();
	}

}
