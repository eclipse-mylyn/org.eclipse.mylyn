/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     John Anvik - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core.history;

/**
 * @author John Anvik
 */
public class ResolutionEvent extends TaskRevision {

	private static final long serialVersionUID = 6609447743555687524L;

	private final ResolutionType type;

	public ResolutionEvent(ResolutionType type) {
		this.what = TaskRevision.RESOLUTION;
		this.type = type;
	}

	public ResolutionType getType() {
		return this.type;
	}

	public String getResolvedBy() {
		return this.getName();
	}

	@Override
	public String toString() {
		return this.getName() + " | " + this.getDate() + " | " + this.getWhat() + " | " + this.getRemoved() + " | "
				+ (this.getType().equals(ResolutionType.UNKNOWN) ? "" : this.getType());
	}

}
