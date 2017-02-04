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

import com.google.common.base.Optional;

public class AllCharactersSpan extends SourceSpan {

	@Override
	public Optional<? extends Inline> createInline(Cursor cursor) {
		return Optional.of(
				new Characters(cursor.getLineAtOffset(), cursor.getOffset(), 1, Character.toString(cursor.getChar())));
	}

}
