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

package org.eclipse.mylyn.internal.wikitext.markdown.core.block;

import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * Markdown inline HTML.
 * 
 * @author Stefan Seelmann
 */
public class InlineHtmlBlock extends Block {

   @Override
   public boolean canStart(String line, int lineOffset) {
       return line.startsWith("<"); //$NON-NLS-1$
   }

   @Override
   protected int processLineContent(String line, int offset) {
       // empty line: start new block
       if (markupLanguage.isEmptyLine(line)) {
           setClosed(true);
           return 0;
       }

       builder.charactersUnescaped(line);
       builder.characters("\n"); //$NON-NLS-1$

       return -1;
   }

}
