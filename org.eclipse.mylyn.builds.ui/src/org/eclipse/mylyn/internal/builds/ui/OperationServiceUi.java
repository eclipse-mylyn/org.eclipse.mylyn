/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.builds.internal.core.IBuildModelRealm;
import org.eclipse.mylyn.builds.internal.core.operations.AbstractOperation;
import org.eclipse.mylyn.builds.internal.core.operations.IOperationService;
import org.eclipse.mylyn.builds.internal.core.util.BuildScheduler;
import org.eclipse.mylyn.commons.core.IOperationMonitor.OperationFlag;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Steffen Pingel
 */
public class OperationServiceUi implements IOperationService {

	private final BuildScheduler scheduler = new BuildScheduler();

	public IBuildModelRealm getRealm() {
		return BuildsUiInternal.getModel().getLoader().getRealm();
	}

	public BuildScheduler getScheduler() {
		return scheduler;
	}

	public void handleResult(AbstractOperation operation, IStatus status) {
		int flags = StatusManager.LOG;
		if (!operation.hasFlag(OperationFlag.BACKGROUND)) {
			flags |= StatusManager.SHOW;
		}
		StatusManager.getManager().handle(status, flags);
	}

}
