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

package org.eclipse.mylyn.internal.wikitext.tracwiki.core.phrase;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;

public class DeletedPhraseModifier extends SimplePhraseModifier {

	public DeletedPhraseModifier() {
		super("--", SpanType.DELETED, true); //$NON-NLS-1$
	}

	@Override
	protected String getPattern(int groupOffset) {
		return "(?<!-)" + super.getPattern(groupOffset) + "(?!-)"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
