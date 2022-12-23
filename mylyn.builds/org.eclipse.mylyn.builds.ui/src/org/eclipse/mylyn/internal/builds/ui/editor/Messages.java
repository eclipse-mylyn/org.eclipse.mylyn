/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.builds.ui.editor.messages"; //$NON-NLS-1$

	public static String BuildEditor_Build;

	public static String BuildEditor_Build_X;

	public static String BuildEditor_Details;

	public static String BuildEditor_Open_with_Web_Browser;

	public static String BuildEditor_X_Failed_Retrieve_Build_Information;

	public static String BuildEditor_X_Retrieving_Build;

	public static String HeaderPart_Build;

	public static String HeaderPart_Duration;

	public static String HeaderPart_ExecutingFor;

	public static String HeaderPart_Plan;

	public static String HeaderPart_Running;

	public static String HeaderPart_Status;

	public static String HeaderPart_Unknown;

	public static String SummaryPart_Cause;

	public static String SummaryPart_StartedOn;

	public static String SummaryPart_Summary;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
