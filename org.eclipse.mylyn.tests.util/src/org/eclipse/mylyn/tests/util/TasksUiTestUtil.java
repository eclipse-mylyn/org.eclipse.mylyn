/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests.util;

import org.eclipse.mylyn.internal.tasks.core.util.ContributorBlackList;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiExtensionReader;

/**
 * @author Steffen Pingel
 */
public class TasksUiTestUtil {

	/**
	 * Ensures that connector UI extensions are registered (see bug 400370).
	 */
	public static void ensureTasksUiInitialization() {
		TasksUiExtensionReader.initWorkbenchUiExtensions(new ContributorBlackList());
	}

}
