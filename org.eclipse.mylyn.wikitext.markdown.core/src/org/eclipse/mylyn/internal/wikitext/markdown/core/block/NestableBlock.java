/*******************************************************************************
 * Copyright (c) 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.core.block;

import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * Block that can be nested within a {@link QuoteBlock}.
 * 
 * @author Stefan Seelmann
 */
public abstract class NestableBlock extends Block {

	@Override
	public int processLine(String line, int offset) {
		return processLineContent(line, offset);
	}

	@Override
	public NestableBlock clone() {
		return (NestableBlock) super.clone();
	}
}
