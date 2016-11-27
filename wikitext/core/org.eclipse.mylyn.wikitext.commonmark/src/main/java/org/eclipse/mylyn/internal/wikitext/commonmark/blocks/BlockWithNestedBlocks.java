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

import org.eclipse.mylyn.internal.wikitext.commonmark.LineSequence;
import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContextBuilder;
import org.eclipse.mylyn.internal.wikitext.commonmark.SourceBlock;

abstract class BlockWithNestedBlocks extends SourceBlock {

	@Override
	public abstract void createContext(ProcessingContextBuilder contextBuilder, LineSequence lineSequence);
}
