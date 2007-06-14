/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;


/**
 * Immutable.
 * 
 * @author Mik Kersten
 */
public final class TaskContainerDelta {

	public enum Kind {
		ADDED, REMOVED, CHANGED
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
