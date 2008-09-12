/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.tasks.bugs;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class AbstractTaskContributor {

	public abstract Map<String, String> getAttributes(IStatus status);

	public String getEditorId(IStatus status) {
		return null;
	}

	public void postProcess(IStatus status, TaskData taskData) {
	}

}
