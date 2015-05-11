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

package org.eclipse.mylyn.internal.context.tasks.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.context.tasks.ui.messages"; //$NON-NLS-1$

	public static String ContextMementoMigrator_0;
	public static String TaskActivityMonitor_Deactivate_and_Save_Some;

	public static String TaskActivityMonitor_Deactivate_Task_and_Save_All;

	public static String TaskActivityMonitor_Task_Deactivation;

	public static String TaskActivityMonitor_Task_Deactivation_Message;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
