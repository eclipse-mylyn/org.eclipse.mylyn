/*******************************************************************************
 * Copyright (c) 2012 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.core.util;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.core.parser.markup.ContentState;

/**
 * Adapter {@link Block} for {@link ReadAheadBlock}s.
 * 
 * @author Stefan Seelmann 
 * @author Max Rydahl Andersen
 */
public class ReadAheadDispatcher extends Block {

	private final LookAheadReader lookAheadReader;

	private Block[] blocks;

	private Block dispatchedBlock;

	public ReadAheadDispatcher(Block... blocks) {
		this.blocks = blocks;
		this.lookAheadReader = new LookAheadReader();
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		dispatchedBlock = null;
		return true;
	}

	@Override
	protected int processLineContent(String line, int offset) {
		if (dispatchedBlock == null) {
			lookAheadReader.setContentState(getState());
			for (Block block : blocks) {
				if (block instanceof ReadAheadBlock) {
					ReadAheadBlock raBlock = ReadAheadBlock.class.cast(block);
					if (raBlock.canStart(line, offset, lookAheadReader)) {
						dispatchedBlock = block;
						break;
					}
				} else {
					if (block.canStart(line, offset)) {
						dispatchedBlock = block;
						break;
					}
				}
			}
		}

		int result = dispatchedBlock.processLine(line, offset);
		if (dispatchedBlock.isClosed()) {
			setClosed(true);
		}
		return result;
	}

	@Override
	public void setClosed(boolean closed) {
		dispatchedBlock.setClosed(closed);
		super.setClosed(closed);
	}

	@Override
	public void setState(ContentState state) {
		for (Block block : blocks) {
			block.setState(state);
		}
		super.setState(state);
	}

	@Override
	public void setParser(MarkupParser parser) {
		for (Block block : blocks) {
			block.setParser(parser);
		}
		super.setParser(parser);
	}

	@Override
	public Block clone() {
		ReadAheadDispatcher clone = (ReadAheadDispatcher) super.clone();
		Block[] clonedBlocks = new Block[blocks.length];
		int i = 0;
		for (Block block : blocks) {
			clonedBlocks[i++] = block.clone();
		}
		clone.blocks = clonedBlocks;
		return clone;
	}

}
