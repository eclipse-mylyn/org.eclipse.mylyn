/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.tasks.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Steffen Pingel
 */
public class TaskContextStoreEvent {

	public enum Kind {
		CLEAR, COPY, DELETE, MERGE, MOVE, SAVE
	}

	private final ITask sourceTask;

	private final ITask targetTask;

	private final Kind kind;

	public TaskContextStoreEvent(Kind kind, ITask sourceTask, ITask targetTask) {
		Assert.isNotNull(kind);
		Assert.isNotNull(sourceTask);
		this.kind = kind;
		this.sourceTask = sourceTask;
		this.targetTask = targetTask;
	}

	public TaskContextStoreEvent(Kind kind, ITask sourceTask) {
		this(kind, sourceTask, null);
	}

	public Kind getKind() {
		return kind;
	}

	public ITask getSourceTask() {
		return sourceTask;
	}

	public ITask getTargetTask() {
		return targetTask;
	}

}
