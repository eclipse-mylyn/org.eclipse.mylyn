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

package org.eclipse.mylyn.internal.wikitext.commonmark;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContext.NamedUriWithTitle;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

public class ProcessingContextBuilder {

	private final Map<String, NamedUriWithTitle> linkByName = new HashMap<>();

	public ProcessingContextBuilder referenceDefinition(String name, String href, String title) {
		if (!Strings.isNullOrEmpty(name)) {
			String key = name.toLowerCase(Locale.ROOT);
			if (!linkByName.containsKey(key)) {
				linkByName.put(key, new NamedUriWithTitle(name, href, title));
			}
		}
		return this;
	}

	public ProcessingContext build() {
		return new ProcessingContext(ImmutableMap.copyOf(linkByName));
	}

	ProcessingContextBuilder() {
		// prevent instantiation
	}
}
