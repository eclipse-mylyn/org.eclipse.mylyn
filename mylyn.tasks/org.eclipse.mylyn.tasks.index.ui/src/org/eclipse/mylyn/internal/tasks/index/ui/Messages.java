/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.index.ui;

import org.eclipse.osgi.util.NLS;

/**
 * @author David Green
 */
class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.index.ui.messages"; //$NON-NLS-1$

	public static String IndexSearchHandler_Generic_date_range_search_1_week;

	public static String IndexSearchHandler_hint_content;

	public static String IndexSearchHandler_hint_generic;

	public static String IndexSearchHandler_hint_person;

	public static String IndexSearchHandler_Past_week_date_range_label;

	public static String IndexSearchHandler_summaryOnly;

	public static String IndexSearchHandler_summaryOnly_tooltip;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
