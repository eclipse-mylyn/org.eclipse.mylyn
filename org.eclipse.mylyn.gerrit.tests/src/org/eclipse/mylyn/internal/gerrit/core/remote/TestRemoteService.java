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

package org.eclipse.mylyn.internal.gerrit.core.remote;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.reviews.core.spi.remote.AbstractRemoteConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.AbstractRemoteService;

public class TestRemoteService extends AbstractRemoteService {

	@Override
	public void retrieve(AbstractRemoteConsumer process, boolean force) {
		try {
			process.pull(force, new NullProgressMonitor());
			process.applyModel(force);
		} catch (CoreException e) {
			throw new RuntimeException();
		}
	}

	@Override
	public void ensureModelThread() {
	}

	@Override
	public void modelExec(Runnable runnable, boolean block) {
		runnable.run();
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public void dispose() {
	}

}
