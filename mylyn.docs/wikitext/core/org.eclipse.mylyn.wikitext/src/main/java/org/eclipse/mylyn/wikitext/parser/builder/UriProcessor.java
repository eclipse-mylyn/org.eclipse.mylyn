/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.builder;

/**
 * A processor that can produce a URI from another URI
 *
 * @since 3.1
 */
public interface UriProcessor {
	/**
	 * Processes the given URI, producing a new URI.
	 *
	 * @param uri
	 *            the URI to process
	 * @return the new URI, or the original if there were no changes to apply
	 */
	String process(String uri);

	/**
	 * Provides a target for the given URI.
	 * 
	 * @param uri
	 *            the URI
	 * @return the target, or null
	 */
	default String target(String uri) {
		return null;
	}
}
