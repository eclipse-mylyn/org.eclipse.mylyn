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

package org.eclipse.mylyn.wikitext.ui.commands;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * @author David Green
 */
class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.wikitext.ui.commands.messages"; //$NON-NLS-1$

	public static String AbstractMarkupResourceHandler_markupLanguageMappingFailed;

	public static String AbstractMarkupResourceHandler_unexpectedError;

	public static String ConvertMarkupToDocbook_cannotCompleteOperation;

	public static String ConvertMarkupToDocbook_cannotConvert;

	public static String ConvertMarkupToDocbook_detailsFollow;

	public static String ConvertMarkupToDocbook_fileExistsOverwrite;

	public static String ConvertMarkupToDocbook_overwrite;

	public static String ConvertMarkupToEclipseHelp_cannotCompleteOperation;

	public static String ConvertMarkupToEclipseHelp_cannotConvert;

	public static String ConvertMarkupToEclipseHelp_detailsFollow;

	public static String ConvertMarkupToEclipseHelp_fileExistsOverwrite;

	public static String ConvertMarkupToEclipseHelp_overwrite;

	public static String ConvertMarkupToHtml_cannotCompleteOperation;

	public static String ConvertMarkupToHtml_cannotConvert;

	public static String ConvertMarkupToHtml_detailsFollow;

	public static String ConvertMarkupToHtml_fileExistsOverwrite;

	public static String ConvertMarkupToHtml_overwrite;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
