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

package org.eclipse.mylyn.internal.wikitext.ui.editor;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * @author David Green
 */
class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.wikitext.ui.editor.messages"; //$NON-NLS-1$

	public static String MarkupEditor_markupLanguage;

	public static String MarkupEditor_markupPreferenceError;

	public static String MarkupEditor_markupPreferenceError2;

	public static String MarkupEditor_markupSource;

	public static String MarkupEditor_markupSource_named;

	public static String MarkupEditor_markupSource_tooltip;

	public static String MarkupEditor_markupSource_tooltip_named;

	public static String MarkupEditor_preview;

	public static String MarkupEditor_preview_tooltip;

	public static String MarkupEditor_updateOutline;

	public static String ContentAssistProposal_label;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
