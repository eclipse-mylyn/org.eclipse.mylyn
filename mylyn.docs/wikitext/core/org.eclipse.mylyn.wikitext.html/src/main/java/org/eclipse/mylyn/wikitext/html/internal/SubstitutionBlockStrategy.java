/*******************************************************************************
 * Copyright (c) 2013, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.wikitext.html.internal;

import static java.util.Objects.requireNonNull;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;

class SubstitutionBlockStrategy implements BlockStrategy {

	private final BlockType blockType;

	protected SubstitutionBlockStrategy(BlockType blockType) {
		this.blockType = requireNonNull(blockType);
	}

	BlockType getBlockType() {
		return blockType;
	}

	@Override
	public void beginBlock(DocumentBuilder builder, BlockType unsupportedType, Attributes attributes) {
		builder.beginBlock(blockType, attributes);
	}

	@Override
	public void endBlock(DocumentBuilder builder) {
		builder.endBlock();
	}

	@Override
	public BlockSeparator trailingSeparator() {
		return null;
	}
}
