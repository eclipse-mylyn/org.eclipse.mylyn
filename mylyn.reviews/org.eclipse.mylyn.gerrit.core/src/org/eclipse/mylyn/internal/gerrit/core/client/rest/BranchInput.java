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

public class BranchInput {

	private String ref;

	private String revision;

	public BranchInput() {
	}

	public BranchInput(String ref, String revision) {
		this.ref = ref;
		this.revision = revision;
	}

	public String getRef() {
		return ref;
	}

	public String getRevision() {
		return revision;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}
}
