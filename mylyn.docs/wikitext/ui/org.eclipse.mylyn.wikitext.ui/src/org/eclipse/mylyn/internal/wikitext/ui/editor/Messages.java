/*******************************************************************************
 * Copyright (c) 2007, 2014 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Marc-Andre Laperle (Ericsson) - Add collapse all button (Bug 424558)
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.editor;

import org.eclipse.osgi.util.NLS;

/**
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

	public static String MarkupEditor_previewUnavailable;

	public static String MarkupEditor_openWorkspaceFileFailed;

	public static String MarkupEditor_updateOutline;

	public static String MarkupEditor_collapseAllAction_label;

	public static String MarkupEditor_collapseAllAction_tooltip;

	public static String MarkupEditor_collapseAllAction_description;

	public static String MarkupEditor_previewScrollingFailed;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
