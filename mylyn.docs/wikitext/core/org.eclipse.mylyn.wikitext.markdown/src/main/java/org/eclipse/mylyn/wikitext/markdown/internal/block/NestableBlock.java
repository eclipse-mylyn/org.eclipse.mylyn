/*******************************************************************************
 * Copyright (c) 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.internal.block;

import org.eclipse.mylyn.wikitext.parser.markup.Block;

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
