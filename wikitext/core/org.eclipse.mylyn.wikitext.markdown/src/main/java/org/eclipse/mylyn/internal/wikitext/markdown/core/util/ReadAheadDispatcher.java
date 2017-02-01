/*******************************************************************************
 * Copyright (c) 2012, 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.mylyn.internal.wikitext.markdown.core.block.NestableBlock;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.ContentState;

/**
 * Adapter {@link NestableBlock} for {@link ReadAheadBlock}s.
 * 
 * @author Stefan Seelmann
 */
public class ReadAheadDispatcher extends NestableBlock {

	private final LookAheadReader lookAheadReader;

	private List<NestableBlock> blocks;

	private NestableBlock dispatchedBlock;

	public ReadAheadDispatcher(NestableBlock... blocks) {
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
			for (NestableBlock block : blocks) {
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
		for (NestableBlock block : blocks) {
			block.setState(state);
		}
		super.setState(state);
	}

	@Override
	public void setParser(MarkupParser parser) {
		for (NestableBlock block : blocks) {
			block.setParser(parser);
		}
		super.setParser(parser);
	}

	@Override
	public NestableBlock clone() {
		ReadAheadDispatcher clone = (ReadAheadDispatcher) super.clone();
		clone.blocks = cloneBlocks(blocks);
		return clone;
	}

	private List<NestableBlock> cloneBlocks(List<NestableBlock> blocks) {
		List<NestableBlock> clonedBlocks = new ArrayList<NestableBlock>();
		for (NestableBlock block : blocks) {
			clonedBlocks.add(block.clone());
		}
		return clonedBlocks;
	}
}
