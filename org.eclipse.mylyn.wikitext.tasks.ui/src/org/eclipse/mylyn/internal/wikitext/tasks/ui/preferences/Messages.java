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

package org.eclipse.mylyn.internal.wikitext.tasks.ui.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * @author David Green
 */
class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.wikitext.tasks.ui.preferences.messages"; //$NON-NLS-1$

	public static String MarkupViewerPreferencePage_appearance;

	public static String MarkupViewerPreferencePage_appearanceInfo;

	public static String MarkupViewerPreferencePage_preview;

	public static String MarkupViewerPreferencePage_previewHtml;

	public static String MarkupViewerPreferencePage_updatePreview;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
