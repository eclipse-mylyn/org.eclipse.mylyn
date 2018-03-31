/*******************************************************************************
 * Copyright (c) 2012, 2016 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *     Max Rydahl Andersen - Bug 474084
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.markup.Block;
import org.eclipse.mylyn.wikitext.parser.markup.ContentState;

/**
 * Adapter {@link Block} for {@link ReadAheadBlock}s.
 *
 * @author Stefan Seelmann
 * @author Max Rydahl Andersen
 */
public class ReadAheadDispatcher extends Block {

	private final LookAheadReader lookAheadReader;

	private List<Block> blocks;

	private Block dispatchedBlock;

	public ReadAheadDispatcher(Block... blocks) {
		this.blocks = cloneBlocks(Arrays.asList(blocks));
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
		clone.blocks = cloneBlocks(blocks);
		return clone;
	}

	private List<Block> cloneBlocks(List<Block> blocks) {
		List<Block> clonedBlocks = new ArrayList<>();
		for (Block block : blocks) {
			clonedBlocks.add(block.clone());
		}
		return clonedBlocks;
	}
}
