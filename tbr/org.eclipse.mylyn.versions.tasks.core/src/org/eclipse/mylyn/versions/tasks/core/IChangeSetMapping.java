package org.eclipse.mylyn.versions.tasks.core;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.versions.core.ChangeSet;

/**
 * 
 * @author mattk
 * @noextend
 * @implement
 */
public interface IChangeSetMapping {
	public ITask getTask();
	
	public void addChangeSet(ChangeSet changeset);
}