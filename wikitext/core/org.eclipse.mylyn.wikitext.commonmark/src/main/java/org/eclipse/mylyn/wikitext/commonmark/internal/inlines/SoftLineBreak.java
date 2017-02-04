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

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;

public class SoftLineBreak extends Inline {

	public SoftLineBreak(Line line, int offset, int length) {
		super(line, offset, length);
	}

	@Override
	public void emit(DocumentBuilder builder) {
		builder.characters("\n");
	}

}
