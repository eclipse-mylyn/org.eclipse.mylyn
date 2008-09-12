/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITaskContainer;

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
		 * The element has been deleted from the tasklist
		 */
		DELETED,

		/**
		 * The root of the data structure has changed.
		 */
		ROOT
	}

	private final ITaskContainer parent;

	private final IRepositoryElement element;

	private final Kind kind;

	private boolean isTransient;

	/**
	 * @param element
	 *            - object being moved/added/removed, source assumed to be root
	 * @since 3.0
	 */
	public TaskContainerDelta(IRepositoryElement element, Kind kind) {
		this.element = element;
		this.parent = null;
		this.kind = kind;
	}

	/**
	 * @since 3.0
	 */
	public TaskContainerDelta(IRepositoryElement element, ITaskContainer parent, Kind kind) {
		this.element = element;
		this.parent = parent;
		this.kind = kind;
	}

	/**
	 * The <code>target</code> is the container that the <code>source</code> is being moved from/to
	 * 
	 * @since 3.0
	 */
	public ITaskContainer getParent() {
		return parent;
	}

	/**
	 * The element being ADDED or REMOVED wrt the <code>target</code>
	 * 
	 * @since 3.0
	 */
	public IRepositoryElement getElement() {
		return element;
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
