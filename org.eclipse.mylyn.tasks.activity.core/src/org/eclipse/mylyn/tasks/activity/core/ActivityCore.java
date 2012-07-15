/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.activity.core;

import org.eclipse.mylyn.internal.tasks.activity.core.ActivityCoreInternal;

/**
 * @author Steffen Pingel
 */
public class ActivityCore {

	public static IActivityManager getActivityManager() {
		return ActivityCoreInternal.getActivityManager();
	}

}
