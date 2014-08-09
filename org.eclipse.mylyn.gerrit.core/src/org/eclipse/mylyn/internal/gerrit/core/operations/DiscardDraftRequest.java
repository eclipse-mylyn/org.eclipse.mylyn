/*******************************************************************************
 * Copyright (c) 2014 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ericsson - initial API and implementation
 *     Guy Perron 423242: Add ability to edit comment from compare navigator popup
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;

import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.Patch.Key;
import com.google.gwtjsonrpc.client.VoidResult;

public class DiscardDraftRequest extends AbstractRequest<VoidResult> {

	private final Key patchKey;

	private final int line;

	private final String uuid;

	private final short side;

	public DiscardDraftRequest(Patch.Key patchKey, int line, short side, String uuid) {
		this.patchKey = patchKey;
		this.line = line;
		this.side = side;
		this.uuid = uuid;
	}

	public int getLine() {
		return line;
	}

	public String getUuid() {
		return uuid;
	}

	public Key getPatchKey() {
		return patchKey;
	}

	public short getSide() {
		return side;
	}

	@Override
	protected VoidResult execute(GerritClient client, IProgressMonitor monitor) throws GerritException {
		return client.deleteDraft(getPatchKey(), getMessage(), getLine(), getSide(), getUuid(), monitor);
	}

	@Override
	public String getOperationName() {
		return Messages.GerritOperation_Discarding_Draft;
	}

}
