/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
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
package org.eclipse.mylyn.wikitext.ui.editor;

import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;

/**
 * Document providers used with {@link WikiTextSourceEditor} <em>should</em> implement this interface. Implementors
 * should prefer to subclass {@link AbstractWikiTextDocumentProvider} instead of directly implementing this interface.
 *
 * @author David Green
 * @since 1.3
 */
public interface WikiTextDocumentProvider {
	/**
	 * @since 3.0
	 */
	public void setMarkupLanguage(MarkupLanguage markupLanguage);
}
