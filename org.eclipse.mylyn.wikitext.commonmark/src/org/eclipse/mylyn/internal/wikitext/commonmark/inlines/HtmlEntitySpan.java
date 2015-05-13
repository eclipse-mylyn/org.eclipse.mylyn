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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Optional;

public class HtmlEntitySpan extends SourceSpan {

	private final Pattern pattern = Pattern.compile("&(#x[a-f0-9]{1,8}|#[0-9]{1,8}|[a-z][a-z0-9]{1,31});.*",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	@Override
	public Optional<? extends Inline> createInline(Cursor cursor) {
		char c = cursor.getChar();
		if (c == '&') {
			Matcher matcher = cursor.matcher(pattern);
			if (matcher.matches()) {
				String ent = matcher.group(1);
				return Optional.of(new HtmlEntity(cursor.getLineAtOffset(), cursor.getOffset(), ent.length() + 2, ent));
			}
		}
		return Optional.absent();
	}
}
