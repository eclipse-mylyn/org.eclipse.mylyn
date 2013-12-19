/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.html.core;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

class SubstitutionBlockStrategy implements BlockStrategy {

	private final BlockType blockType;

	protected SubstitutionBlockStrategy(BlockType blockType) {
		this.blockType = checkNotNull(blockType);
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

}
