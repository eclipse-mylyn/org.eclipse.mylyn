/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.parser.markup.token;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * A processor that emits a line break.
 * 
 * @see DocumentBuilder#lineBreak()
 * 
 * @author David Green
 * @since 1.0
 */
public class LineBreakReplacementTokenProcessor extends PatternBasedElementProcessor {

	@Override
	public void emit() {
		getBuilder().lineBreak();
	}

}
