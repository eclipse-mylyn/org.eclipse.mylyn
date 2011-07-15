import java.io.File;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.context.AbstractTaskContextStore;

/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html Contributors: Tasktop Technologies - initial API and
 * implementation
 *******************************************************************************/

public class ContextStore extends AbstractTaskContextStore {

	@Override
	public boolean hasContext(ITask task) {
		// ignore
		return false;
	}

	@Override
	public void importContext(ITask task, InputStream source) throws CoreException {
		// ignore

	}

	@Override
	public void cloneContext(ITask oldTask, ITask newTask) {
		ContextCorePlugin.getContextStore().saveActiveContext();
		ContextCore.getContextStore().cloneContext(oldTask.getHandleIdentifier(), newTask.getHandleIdentifier());
	}

	@Override
	public File getFileForContext(TaskRepository repository, ITask task) {
		// ignore
		return null;
	}

	@Override
	public void deleteContext(ITask oldTask) {
		ContextCorePlugin.getContextManager().deleteContext(oldTask.getHandleIdentifier());
	}

}
