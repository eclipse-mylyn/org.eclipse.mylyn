/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.bugzilla.core.history;

import java.io.Serializable;

/**
 * @author John Anvik
 */
public class AttachmentFlag implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final AttachmentFlagStatus status;

	private final AttachmentFlagState state;

	public AttachmentFlag(AttachmentFlagStatus status, AttachmentFlagState state) {
		this.status = status;
		this.state = state;
	}

	public AttachmentFlagState getState() {
		return this.state;
	}

	public AttachmentFlagStatus getStatus() {
		return this.status;
	}

	@Override
	public String toString() {
		return this.status.name() + "[" + (this.state.equals(AttachmentFlagState.UNKNOWN) ? "" : this.state.name())
		+ "]";
	}
}
