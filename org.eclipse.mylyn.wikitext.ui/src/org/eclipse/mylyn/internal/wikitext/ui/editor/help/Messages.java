/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.editor.help;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * @author David Green
 */
class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.wikitext.ui.editor.help.messages"; //$NON-NLS-1$

	public static String HelpContent_cannotAccessContent;

	public static String HelpContent_cannotFindResource;

	public static String HelpContent_noSuchMarkupLanguage;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
