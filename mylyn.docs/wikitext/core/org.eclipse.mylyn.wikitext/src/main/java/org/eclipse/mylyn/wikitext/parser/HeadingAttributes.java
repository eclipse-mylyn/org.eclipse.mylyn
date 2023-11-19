/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
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
package org.eclipse.mylyn.wikitext.parser;

/**
 * Attributes specific to {@link DocumentBuilder#beginHeading(int, Attributes)}
 *
 * @author David Green
 * @since 3.0
 */
public class HeadingAttributes extends Attributes {
	private boolean omitFromTableOfContents;

	/**
	 * a hint to document processors to indicate if this heading should participate in a table of contents. The default
	 * is false.
	 */
	public boolean isOmitFromTableOfContents() {
		return omitFromTableOfContents;
	}

	/**
	 * a hint to document processors to indicate if this heading should participate in a table of contents. The default
	 * is false.
	 */
	public void setOmitFromTableOfContents(boolean omitFromTableOfContents) {
		this.omitFromTableOfContents = omitFromTableOfContents;
	}
}
