/*******************************************************************************
 * Copyright (c) 2015 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Max Rydahl Andersen - copied from markdown to get base for asciidoc
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.core;

import java.util.List;

import org.eclipse.mylyn.internal.wikitext.asciidoc.core.block.ParagraphBlock;
import org.eclipse.mylyn.wikitext.core.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * A markup language implementing Asciidoc syntax.
 *
 * @author Stefan Seelmann 
 * @author Max Rydahl Andersen
 */
public class AsciidocLanguage extends AbstractMarkupLanguage {

	public AsciidocLanguage() {
		setName("Asciidoc"); //$NON-NLS-1$
	}

	@Override
	protected void addStandardTokens(PatternBasedSyntax tokenSyntax) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addStandardPhraseModifiers(PatternBasedSyntax phraseModifierSyntax) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addStandardBlocks(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Block createParagraphBlock() {
		return new ParagraphBlock();
	}

}
