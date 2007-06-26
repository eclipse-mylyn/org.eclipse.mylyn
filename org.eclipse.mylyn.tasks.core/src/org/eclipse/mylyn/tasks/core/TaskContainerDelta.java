/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

/**
 * Immutable. Defines changes to Task List elements.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public final class TaskContainerDelta {

	public enum Kind {
		/**
		 * One container added to another or to the root.
		 */
		ADDED,

		/**
		 * One container removed from another or from the root.
		 */
		REMOVED,

		/**
		 * Container has changed, e.g. has new children, a task's priority or planning info. For tasks changed state
		 * tends to be show in a view.
		 */
		CHANGED,

		/**
		 * The content of the container has changed, e.g. new data has been downloaded for a task from the repository.
		 * For tasks content tends to be show in an editor.
		 */
		CONTENT,

		/**
		 * The root of the data structure has changed.
		 */
		ROOT
	}

	private final AbstractTaskContainer container;

	private final Kind kind;

	public TaskContainerDelta(AbstractTaskContainer container, Kind kind) {
		this.container = container;
		this.kind = kind;
	}

	public AbstractTaskContainer getContainer() {
		return container;
	}

	public Kind getKind() {
		return kind;
	}

}
