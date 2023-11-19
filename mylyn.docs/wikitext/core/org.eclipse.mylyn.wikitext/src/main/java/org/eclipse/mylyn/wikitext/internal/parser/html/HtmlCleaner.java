/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Billy Huang - Bug 396332
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.internal.parser.html;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;

/**
 * Provides a way of cleaning up HTML to make it more suitable for conversion to Wiki markup.
 *
 * @author David Green
 */
public class HtmlCleaner {

	private final List<DocumentProcessor> processors = new ArrayList<>();
	{
		processors.add(new WhitespaceCleanupProcessor()); // ORDER DEPENDENCY - should come first
		processors.add(new RemoveEmptySpansProcessor());
		processors.add(new RemoveExcessiveStylesProcessor());
		processors.add(new RepairBrokenCSSColorStylesProcessor());
	}

	public void configure(HtmlParser parser) {
		parser.getProcessors().addAll(processors);
	}

	public void apply(Document document) {
		for (DocumentProcessor processor : processors) {
			processor.process(document);
		}
	}
}
