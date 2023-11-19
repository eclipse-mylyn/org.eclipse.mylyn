/*******************************************************************************
 * Copyright (c) 2015 David Green.
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

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCharactersSpan extends SourceSpan {

	private final Pattern pattern = Pattern.compile("((?: *[^\n `\\[\\]\\\\!<&*_h]+)+).*", Pattern.DOTALL);

	@Override
	public Optional<? extends Inline> createInline(Cursor cursor) {
		Matcher matcher = cursor.matcher(pattern);
		if (matcher.matches()) {
			String group = matcher.group(1);
			int length = cursor.getOffset(matcher.end(1)) - cursor.getOffset();
			return Optional.of(new Characters(cursor.getLineAtOffset(), cursor.getOffset(), length, group));
		}
		return Optional.empty();
	}

}
