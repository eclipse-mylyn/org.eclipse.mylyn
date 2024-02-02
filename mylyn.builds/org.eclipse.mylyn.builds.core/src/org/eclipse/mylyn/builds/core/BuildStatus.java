/*******************************************************************************
 * Copyright (c) 2010, 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.core;

/**
 * @author Steffen Pingel
 */
// FIXME rename class to something less confusing
public enum BuildStatus {

	// FIXME rename FAILED to FAILURE
	FAILED, UNSTABLE, SUCCESS, DISABLED, ABORTED, NOT_BUILT;

	public String getLabel() {
		return switch (this) {
			case FAILED -> "Failed";
			case UNSTABLE -> "Unstable";
			case SUCCESS -> "Success";
			case DISABLED -> "Disabled";
			case ABORTED -> "Aborted";
			case NOT_BUILT -> "Not built";
			default -> "Unknown";
		};
	}

}
