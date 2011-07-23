/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.activity;

import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityManager;
import org.eclipse.mylyn.tasks.core.activity.AbstractTaskActivityMonitor;

/**
 * @author Steffen Pingel
 */
public class DefaultTaskActivityMonitor extends AbstractTaskActivityMonitor {

	@Override
	public List<ITask> getActivationHistory() {
		return Collections.emptyList();
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public void loadActivityTime() {
		// ignore
	}

	@Override
	public void reloadActivityTime() {
		// ignore		
	}

	@Override
	public void start(ITaskActivityManager taskActivityManager) {
		// ignore
	}

	@Override
	public void stop() {
		// ignore
	}

}
