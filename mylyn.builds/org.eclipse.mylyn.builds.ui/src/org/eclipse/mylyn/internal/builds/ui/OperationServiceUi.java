/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.builds.internal.core.IBuildModelRealm;
import org.eclipse.mylyn.builds.internal.core.operations.AbstractOperation;
import org.eclipse.mylyn.builds.internal.core.operations.IOperationService;
import org.eclipse.mylyn.builds.internal.core.util.BuildScheduler;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor.OperationFlag;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Steffen Pingel
 */
public class OperationServiceUi implements IOperationService {

	private final BuildScheduler scheduler = new BuildScheduler();

	@Override
	public IBuildModelRealm getRealm() {
		return BuildsUiInternal.getModel().getLoader().getRealm();
	}

	@Override
	public BuildScheduler getScheduler() {
		return scheduler;
	}

	@Override
	public void handleResult(AbstractOperation operation, IStatus status) {
		int flags = StatusManager.LOG;
		if (!operation.hasFlag(OperationFlag.BACKGROUND)) {
			flags |= StatusManager.SHOW;
		}
		StatusManager.getManager().handle(status, flags);
	}

}
