/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.deprecated;

import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
public abstract class LegacyTaskDataCollector extends TaskDataCollector {

	@Deprecated
	public abstract void accept(RepositoryTaskData taskData);

	@Override
	public void accept(TaskData taskData) {
		throw new UnsupportedOperationException();
	}

}
