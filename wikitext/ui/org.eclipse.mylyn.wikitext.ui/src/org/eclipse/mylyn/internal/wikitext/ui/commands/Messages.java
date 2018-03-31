/*******************************************************************************
 * Copyright (c) 2013 David Green and others.
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

package org.eclipse.mylyn.internal.wikitext.ui.commands;

import org.eclipse.osgi.util.NLS;

class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.wikitext.ui.commands.messages"; //$NON-NLS-1$

	public static String ConvertMarkupToMarkup_cannot_generate_detail;

	public static String ConvertMarkupToMarkup_cannot_generate_title;

	public static String ConvertMarkupToMarkup_details_follow;

	public static String ConvertMarkupToMarkup_overwrite_file;

	public static String ConvertMarkupToMarkup_overwrite_file_detail;

	public static String WikiMarkupGenerationContribution_generate_markup;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
