/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal.blocks;

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext;
import org.eclipse.mylyn.wikitext.commonmark.internal.SourceBlock;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;

public class EmptyBlock extends SourceBlock {

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		while (currentLineIsEmpty(lineSequence)) {
			lineSequence.advance();
		}
	}

	@Override
	public boolean canStart(LineSequence lineSequence) {
		return currentLineIsEmpty(lineSequence);
	}

	private boolean currentLineIsEmpty(LineSequence lineSequence) {
		Line line = lineSequence.getCurrentLine();
		return line != null && line.isEmpty();
	}

}
