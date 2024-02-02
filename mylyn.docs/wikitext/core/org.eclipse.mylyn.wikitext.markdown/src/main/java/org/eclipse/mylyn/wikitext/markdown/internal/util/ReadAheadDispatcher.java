/*******************************************************************************
 * Copyright (c) 2012, 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.internal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.mylyn.wikitext.markdown.internal.block.NestableBlock;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.markup.ContentState;

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
		lookAheadReader = new LookAheadReader();
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
				} else if (block.canStart(line, offset)) {
					dispatchedBlock = block;
					break;
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
		List<NestableBlock> clonedBlocks = new ArrayList<>();
		for (NestableBlock block : blocks) {
			clonedBlocks.add(block.clone());
		}
		return clonedBlocks;
	}
}
