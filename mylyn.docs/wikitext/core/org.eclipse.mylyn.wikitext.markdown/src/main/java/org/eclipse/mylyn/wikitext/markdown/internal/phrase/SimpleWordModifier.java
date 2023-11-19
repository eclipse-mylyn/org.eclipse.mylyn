/*******************************************************************************
 * Copyright (c) 2019 Pierre-Yves B. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Pierre-Yves B. <pyvesdev@gmail.com> - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.internal.phrase;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;

public class SimpleWordModifier extends SimplePhraseModifier {

	public SimpleWordModifier(String delimiter, SpanType spanType) {
		super(delimiter, spanType);
	}

	@Override
	protected String getPattern(int groupOffset) {
		return "(?:^| )" + super.getPattern(groupOffset) + "(?:$| )"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
