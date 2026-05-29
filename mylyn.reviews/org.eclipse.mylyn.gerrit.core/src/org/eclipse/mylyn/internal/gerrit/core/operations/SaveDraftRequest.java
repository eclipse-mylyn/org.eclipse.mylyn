/*******************************************************************************
 * Copyright (c) 2011, 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Guy Perron 423242: Add ability to edit comment from compare navigator popup
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.CommentInput;

import com.google.gerrit.reviewdb.Patch.Key;

/**
 * @author Steffen Pingel
 * @author Guy Perron
 */
public class SaveDraftRequest extends AbstractRequest<CommentInput> {

	private final Key patchKey;

	private final int line;

	private final String uUid;

	private final String parentUuid;

	private final short side;

	public SaveDraftRequest(Key patchKey, int line, short side, String parentUuid, String uUid) {
		this.patchKey = patchKey;
		this.line = line;
		this.side = side;
		this.parentUuid = parentUuid;
		this.uUid = uUid;
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

	public String getUuid() {
		return uUid;
	}

	@Override
	protected CommentInput execute(GerritClient client, IProgressMonitor monitor) throws GerritException {
		return client.saveDraft(getPatchKey(), getMessage(), getLine(), getSide(), getParentUuid(), getUuid(), monitor);
	}

	@Override
	public String getOperationName() {
		return Messages.GerritOperation_Saving_Draft;
	}

}
