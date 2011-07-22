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

package org.eclipse.mylyn.internal.tasks.core.context;

import java.io.File;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.context.AbstractTaskContextStore;

/**
 * @author Steffen Pingel
 */
public class DefaultTaskContextStore extends AbstractTaskContextStore {

	@Override
	public boolean hasContext(ITask task) {
		return false;
	}

	@Override
	public void cloneContext(ITask sourceTask, ITask destinationTask) {
	}

	@Override
	public File getFileForContext(ITask task) {
		return null;
	}

	@Override
	public void deleteContext(ITask oldTask) {
	}

	@Override
	public void saveActiveContext() {
		// ignore
	}

	@Override
	public void refactorRepositoryUrl(String oldRepositoryUrl, String newRepositoryUrl) {
		// ignore
	}

	@Override
	public void setContextDirectory(File contextStoreDir) {
		// ignore
	}

}
