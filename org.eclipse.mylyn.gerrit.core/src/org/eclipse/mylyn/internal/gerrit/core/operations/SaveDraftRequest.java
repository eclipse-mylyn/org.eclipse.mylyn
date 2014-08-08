/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Guy Perron 423242: Add ability to edit comment from compare navigator popup
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
 * @author Guy Perron
 */
public class SaveDraftRequest extends AbstractRequest<PatchLineComment> {

	private final Key patchKey;

	private final int line;

	private String uUid = null;

	private String parentUuid;

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

	public String getUuid() {
		return uUid;
	}

	public void setUuid(String uUid) {
		this.uUid = uUid;
	}

	@Override
	protected PatchLineComment execute(GerritClient client, IProgressMonitor monitor) throws GerritException {
		return client.saveDraft(getPatchKey(), getMessage(), getLine(), getSide(), getParentUuid(), getUuid(), monitor);
	}

	@Override
	public String getOperationName() {
		return Messages.GerritOperation_Saving_Draft;
	}

}
