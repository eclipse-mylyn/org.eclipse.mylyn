/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.compare;

public enum Direction {

	FORWARDS(true), BACKWARDS(false);

	private final boolean forward;

	private Direction(boolean forward) {
		this.forward = forward;

	}

	public boolean isForwards() {
		return forward;
	}

	public boolean isBackwards() {
		return !forward;
	}
}