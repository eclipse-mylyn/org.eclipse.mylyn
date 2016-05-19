/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.operations;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.core.operations.messages"; //$NON-NLS-1$

	public static String RefactorTaskIdsOperation_FailedTaskCount;

	public static String RefactorTaskIdsOperation_TaskDataRefactorError;

	public static String RefactorTaskIdsOperation_UpdateTaskId;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
