/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.bugzilla.core.history;

/**
 * @author John Anvik
 */
public class StatusEvent extends TaskRevision {

	private static final long serialVersionUID = 245446279981920435L;

	private final StatusType type;

	public StatusEvent(StatusType type) {
		this.what = TaskRevision.STATUS;
		this.type = type;
	}

	public StatusType getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return this.getName() + " | " + this.getDate() + " | " + this.getWhat() + " | " + this.getRemoved() + " | "
				+ this.getType();
	}
}
