/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.ICapabilityContext;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;

/**
 * @author Steffen Pingel
 */
public class CapabilityContext implements ICapabilityContext {

	private final ITaskDataManager taskDataManager;

	public CapabilityContext(ITaskDataManager taskDataManager) {
		Assert.isNotNull(taskDataManager);
		this.taskDataManager = taskDataManager;
	}

	public ITaskDataManager getTaskDataManager() {
		return taskDataManager;
	}

}
