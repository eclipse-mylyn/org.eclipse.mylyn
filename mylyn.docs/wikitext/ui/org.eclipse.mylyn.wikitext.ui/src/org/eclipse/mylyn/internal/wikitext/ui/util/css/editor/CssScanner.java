/*******************************************************************************
 * Copyright (c) 2009, 2011 David Green and others.
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

package org.eclipse.mylyn.internal.wikitext.ui.util.css.editor;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

/**
 * @author David Green
 */
public class CssScanner extends RuleBasedScanner {

	public CssScanner(ColorRegistry colorRegistry) {
		setRules(//
				new SelectorRule(new Token(new TextAttribute(colorRegistry.get(Colors.KEY_SELECTOR)))),
				new WhitespaceRule(new CssWhitespaceDetector()));
	}
}
