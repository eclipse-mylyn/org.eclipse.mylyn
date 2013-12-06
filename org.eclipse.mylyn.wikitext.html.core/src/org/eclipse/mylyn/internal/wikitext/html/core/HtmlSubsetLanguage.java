/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.html.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Writer;
import java.util.Set;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.html.core.HtmlLanguage;

import com.google.common.collect.ImmutableSet;

public class HtmlSubsetLanguage extends HtmlLanguage {

	private final Set<BlockType> supportedBlockTypes;

	private final Set<SpanType> supportedSpanTypes;

	public HtmlSubsetLanguage(String name, Set<BlockType> blockTypes, Set<SpanType> spanTypes) {
		setName(checkNotNull(name));
		this.supportedBlockTypes = ImmutableSet.copyOf(checkNotNull(blockTypes));
		this.supportedSpanTypes = ImmutableSet.copyOf(checkNotNull(spanTypes));
	}

	public Set<BlockType> getSupportedBlockTypes() {
		return supportedBlockTypes;
	}

	public Set<SpanType> getSupportedSpanTypes() {
		return supportedSpanTypes;
	}

	@Override
	public DocumentBuilder createDocumentBuilder(Writer out, boolean formatting) {
		HtmlSubsetDocumentBuilder builder = new HtmlSubsetDocumentBuilder(out, formatting);
		builder.setSupportedBlockTypes(supportedBlockTypes);
		builder.setSupportedSpanTypes(supportedSpanTypes);
		return builder;
	}
}
