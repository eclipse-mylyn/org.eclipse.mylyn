/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.remote;

import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.reviews.core.spi.remote.JobRemoteService;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Executes remote job, ensuring that results are applied and notification occurs in UI thread.
 * 
 * @author Miles Parker
 */
public class RemoteUiService extends JobRemoteService {

	@Override
	public void modelExec(final Runnable runnable, boolean block) {
		Display displayThread = Display.getCurrent();
		if (displayThread == null) {
			if (!PlatformUI.getWorkbench().isClosing()) {
				displayThread = PlatformUI.getWorkbench().getDisplay();
			} else {
				throw new OperationCanceledException();
			}
		}
		if (block) {
			if (Display.getCurrent() != null) {
				//Don't cause deadlock, just execute now!
				runnable.run();
			} else {
				displayThread.syncExec(runnable);
			}
		} else {
			displayThread.asyncExec(runnable);
		}
	}

	@Override
	public void ensureModelThread() {
		if (Display.getCurrent() == null) {
			throw new RuntimeException("Attempted to execute a model-related operation in a non-model thread.");
		}
	}
}
