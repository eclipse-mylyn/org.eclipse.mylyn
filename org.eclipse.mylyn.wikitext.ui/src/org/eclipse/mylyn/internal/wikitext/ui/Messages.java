/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * @author David Green
 */
class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.wikitext.ui.messages"; //$NON-NLS-1$

	public static String WikiTextNature_cannotValidateNatureSet;

	public static String WikiTextUiPlugin_contentRequired;

	public static String WikiTextUiPlugin_descriptionRequired;

	public static String WikiTextUiPlugin_invalidExtension;

	public static String WikiTextUiPlugin_invalidMarkupLanguage;

	public static String WikiTextUiPlugin_markupLanguageContentAlreadyDeclared;

	public static String WikiTextUiPlugin_markupLanguageRequired;

	public static String WikiTextUiPlugin_nameRequired;

	public static String WikiTextUiPlugin_resourceRequired;

	public static String WikiTextUiPlugin_unexpectedExtensionElement;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
