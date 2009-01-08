/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.parser;

/**
 * Attributes specific to {@link DocumentBuilder#beginHeading(int, Attributes)}
 * 
 * @author David Green
 * @since 1.0
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
