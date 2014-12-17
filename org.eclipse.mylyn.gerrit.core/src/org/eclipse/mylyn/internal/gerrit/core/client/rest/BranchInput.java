/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
