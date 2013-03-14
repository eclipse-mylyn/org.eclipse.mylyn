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

package org.eclipse.mylyn.internal.wikitext.markdown.core.token;

import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;
import org.eclipse.mylyn.wikitext.core.parser.markup.phrase.LiteralPhraseModifierProcessor;

/**
 * A phrase modifier that detects HTML and XML entities in the source.
 * 
 * @author Stefan Seelmann
 */
public class PreserverHtmlEntityToken extends PatternBasedElement {

   @Override
   protected String getPattern(int groupOffset) {
       return "(&[A-Za-z]+;)"; //$NON-NLS-1$
   }

   @Override
   protected PatternBasedElementProcessor newProcessor() {
       return new LiteralPhraseModifierProcessor(false);
   }

   @Override
   protected int getPatternGroupCount() {
       return 1;
   }

}
