/*******************************************************************************
 * Copyright (c) 2014 Ericsson AB and others.
 * 
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Jacques Bouthillier - initial API and implementation
 *     Marc-Andre Laperle - Add Status to dashboard
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.dashboard.ui.internal.model;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.gerrit.dashboard.ui.internal.model.messages"; //$NON-NLS-1$

	public static String ReviewTableDefinition_branch;

	public static String ReviewTableDefinition_codeReview;

	public static String ReviewTableDefinition_id;

	public static String ReviewTableDefinition_owner;

	public static String ReviewTableDefinition_project;

	public static String ReviewTableDefinition_subject;

	public static String ReviewTableDefinition_status;

	public static String ReviewTableDefinition_updated;

	public static String ReviewTableDefinition_verify;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
