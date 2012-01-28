/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;

import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.Patch.Key;
import com.google.gerrit.reviewdb.PatchLineComment;

/**
 * @author Steffen Pingel
 */
public class SaveDraftRequest extends AbstractRequest<PatchLineComment> {

	private final Key patchKey;

	int line;

	String parentUuid;

	private final short side;

	public SaveDraftRequest(Patch.Key patchKey, int line, short side) {
		this.patchKey = patchKey;
		this.line = line;
		this.side = side;
	}

	public int getLine() {
		return line;
	}

	public String getParentUuid() {
		return parentUuid;
	}

	public Key getPatchKey() {
		return patchKey;
	}

	public short getSide() {
		return side;
	}

	public void setParentUuid(String parentUuid) {
		this.parentUuid = parentUuid;
	}

	@Override
	protected PatchLineComment execute(GerritClient client, IProgressMonitor monitor) throws GerritException {
		return client.saveDraft(getPatchKey(), getMessage(), getLine(), getSide(), getParentUuid(), monitor);
	}

}
