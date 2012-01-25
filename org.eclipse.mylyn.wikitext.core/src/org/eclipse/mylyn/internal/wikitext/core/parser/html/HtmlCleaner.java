/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.parser.html;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;

/**
 * Provides a way of cleaning up HTML to make it more suitable for conversion to Wiki markup.
 * 
 * @author David Green
 */
public class HtmlCleaner {

	private final List<DocumentProcessor> processors = new ArrayList<DocumentProcessor>();
	{
		processors.add(new WhitespaceCleanupProcessor()); // ORDER DEPENDENCY - should come first
		processors.add(new RemoveEmptySpansProcessor());
		processors.add(new RemoveExcessiveStylesProcessor());
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
