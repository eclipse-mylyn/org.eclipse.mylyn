/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.commonmark.inlines;

import org.eclipse.mylyn.internal.wikitext.commonmark.Line;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

public class HardLineBreak extends Inline {

	public HardLineBreak(Line line, int offset, int length) {
		super(line, offset, length);
	}

	@Override
	public void emit(DocumentBuilder builder) {
		builder.lineBreak();
	}
}
