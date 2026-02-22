/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.rest;

public class BranchInfo extends BranchInput {

	private boolean can_delete;

	public BranchInfo() {
	}

	public BranchInfo(String ref, String revision) {
		super(ref, revision);
	}

	public boolean canDelete() {
		return can_delete;
	}
}
