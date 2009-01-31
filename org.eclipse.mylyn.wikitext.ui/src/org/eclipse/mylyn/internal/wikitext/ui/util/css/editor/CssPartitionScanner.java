/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util.css.editor;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * 
 * @author David Green
 */
class CssPartitionScanner extends RuleBasedPartitionScanner {
	public final static String CONTENT_TYPE_COMMENT = "__css_comment"; //$NON-NLS-1$

	public final static String CONTENT_TYPE_BLOCK = "__css_block"; //$NON-NLS-1$

	public CssPartitionScanner() {

		setPredicateRules(new IPredicateRule[] {
		// 
				new MultiLineRule("/*", "*/", new Token(CONTENT_TYPE_COMMENT)), //$NON-NLS-1$ //$NON-NLS-2$
				new MultiLineRule("{", "}", new Token(CONTENT_TYPE_BLOCK)), //$NON-NLS-1$ //$NON-NLS-2$
		});
	}
}
