/*******************************************************************************
 * Copyright (c) 2014 Ericsson and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
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

	private final String uuid;

	public DiscardDraftRequest(Patch.Key patchKey, String uuid) {
		this.patchKey = patchKey;
		this.uuid = uuid;
	}

	public String getUuid() {
		return uuid;
	}

	public Key getPatchKey() {
		return patchKey;
	}

	@Override
	protected VoidResult execute(GerritClient client, IProgressMonitor monitor) throws GerritException {
		return client.deleteDraft(getPatchKey(), getUuid(), monitor);
	}

	@Override
	public String getOperationName() {
		return Messages.GerritOperation_Discarding_Draft;
	}

}
