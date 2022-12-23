/*******************************************************************************
 * Copyright (c) 2009, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.java.ui.messages"; //$NON-NLS-1$

	public static String JavaStackTraceContextComputationStrategy_Finding_Java_Context_Element_Progress_Label;

	public static String LandmarkMarkerManager_Mylyn_Landmark;

	public static String LandmarkMarkerManager_Updating_Landmark_Markers;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
