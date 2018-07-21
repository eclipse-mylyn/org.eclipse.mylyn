/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core.util;

import org.eclipse.mylyn.commons.core.ExtensionPointReader;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.activity.DefaultTaskActivityMonitor;
import org.eclipse.mylyn.internal.tasks.core.context.DefaultTaskContextStore;
import org.eclipse.mylyn.tasks.core.activity.AbstractTaskActivityMonitor;
import org.eclipse.mylyn.tasks.core.context.AbstractTaskContextStore;

public class TasksCoreExtensionReader {

	public static AbstractTaskContextStore loadTaskContextStore() {
		ExtensionPointReader<AbstractTaskContextStore> reader = new ExtensionPointReader<AbstractTaskContextStore>(
				ITasksCoreConstants.ID_PLUGIN, "contextStore", "contextStore", AbstractTaskContextStore.class); //$NON-NLS-1$ //$NON-NLS-2$
		reader.read();
		AbstractTaskContextStore contextStore = reader.getItem();
		if (contextStore != null) {
			return contextStore;
		}
		return new DefaultTaskContextStore();
	}

	public static AbstractTaskActivityMonitor loadTaskActivityMonitor() {
		ExtensionPointReader<AbstractTaskActivityMonitor> reader = new ExtensionPointReader<AbstractTaskActivityMonitor>(
				ITasksCoreConstants.ID_PLUGIN, "activityMonitor", "activityMonitor", AbstractTaskActivityMonitor.class); //$NON-NLS-1$ //$NON-NLS-2$
		reader.read();
		AbstractTaskActivityMonitor activityMonitor = reader.getItem();
		if (activityMonitor != null) {
			return activityMonitor;
		}
		return new DefaultTaskActivityMonitor();
	}

}
