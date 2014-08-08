/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.operations;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.gerrit.core.operations.messages"; //$NON-NLS-1$

	public static String GerritOperation_Operation_Failed;

	public static String GerritOperation_Abandoning_Change;

	public static String GerritOperation_Adding_Reviewers;

	public static String GerritOperation_Publishing_Change;

	public static String GerritOperation_Rebasing_Change;

	public static String GerritOperation_Refreshing_Configuration;

	public static String GerritOperation_Restoring_Change;

	public static String GerritOperation_Saving_Draft;

	public static String GerritOperation_Submitting_Change;

	public static String GerritOperation_Discarding_Draft;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
