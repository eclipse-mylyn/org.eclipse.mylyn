/*******************************************************************************
 * Copyright (c) 2014 Ericsson AB and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
