package org.eclipse.mylyn.versions.tasks.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.versions.tasks.core.IChangeSetMapping;

public abstract class AbstractChangesetMappingProvider {

	public abstract void getChangesetsForTask(IChangeSetMapping mapping, IProgressMonitor monitor) throws CoreException ;

	public abstract int getScoreFor(ITask task);
}

