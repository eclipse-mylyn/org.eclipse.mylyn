/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac;

import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;

/**
 * @author Steffen Pingel
 */
public class TracQueryHit extends AbstractQueryHit {

	private TracTask task;

	protected TracQueryHit(TracTask task, String repositoryUrl, String id) {
		super(repositoryUrl, task.getDescription(), id);

		this.task = task;
	}

	@Override
	public AbstractRepositoryTask getCorrespondingTask() {
		return task;
	}

	@Override
	public AbstractRepositoryTask getOrCreateCorrespondingTask() {
		return task;
	}

	@Override
	public boolean isCompleted() {
		return task.isCompleted();
	}

	@Override
	public void setCorrespondingTask(AbstractRepositoryTask task) {
		if (task instanceof TracTask) {
			this.task = (TracTask) task;
		}
	}

	public String getDescription() {
		return task.getDescription();
	}

	public String getPriority() {
		return task.getPriority();
	}

	public void setHandleIdentifier(String id) {
		task.setHandleIdentifier(id);
	}

}
