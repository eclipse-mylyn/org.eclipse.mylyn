/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.parser.html;

/**
 * Provides a way of cleaning up HTML to make it more suitable for conversion to Wiki markup.
 * 
 * @author David Green
 */
public class HtmlCleaner {

	public void configure(HtmlParser parser) {
		parser.getProcessors().add(new WhitespaceCleanupProcessor()); // ORDER DEPENDENCY - should come first
		parser.getProcessors().add(new RemoveEmptySpansProcessor());
		parser.getProcessors().add(new RemoveExcessiveStylesProcessor());
	}
}
