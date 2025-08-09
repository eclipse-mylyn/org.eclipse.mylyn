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
 *     See git history
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
			case FAILED -> Messages.BuildStatus_failed;
			case UNSTABLE -> Messages.BuildStatus_unstable;
			case SUCCESS -> Messages.BuildStatus_success;
			case DISABLED -> Messages.BuildStatus_disabled;
			case ABORTED -> Messages.BuildStatus_aborted;
			case NOT_BUILT -> Messages.BuildStatus_notBuilt;
			default -> Messages.BuildStatus_unknown;
		};
	}

}
