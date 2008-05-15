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
		 * One container (source) added to another (target)
		 */
		ADDED,

		/**
		 * One container (source) removed from another (target)
		 */
		REMOVED,

		/**
		 * The internal state of the container (target) has changed, e.g. attributes, summary, priority, etc
		 */
		CONTENT,

		/**
		 * The root of the data structure has changed.
		 */
		ROOT
	}

	private final ITaskElement target;

	private final ITaskElement source;

	private final Kind kind;

	private boolean isTransient;

	/**
	 * @since 3.0
	 */
	public TaskContainerDelta(ITaskElement target, Kind kind) {
		this.target = target;
		this.source = null;
		this.kind = kind;
	}

	/**
	 * @since 3.0
	 */
	public TaskContainerDelta(ITaskElement source, ITaskElement target, Kind kind) {
		this.source = source;
		this.target = target;
		this.kind = kind;
	}

	/**
	 * The target is the container modified or the target destination for the <code>source</code> element
	 * 
	 * @since 3.0
	 */
	public ITaskElement getTarget() {
		return target;
	}

	/**
	 * The element being ADDED or REMOVED wrt the <code>target</code>
	 * 
	 * @since 3.0
	 */
	public ITaskElement getSource() {
		return source;
	}

	public Kind getKind() {
		return kind;
	}

	/**
	 * @since 3.0
	 */
	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}

	/**
	 * @since 3.0
	 */
	public boolean isTransient() {
		return isTransient;
	}

}
