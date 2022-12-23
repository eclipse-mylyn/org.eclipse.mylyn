/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Miles Parker, Tasktop Technologies - initial API and implementation
 *     Steffen Pingel, Tasktop Technologies - original GerritUtil implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.client.PatchSetContent;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;

import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.PatchSet;

/**
 * Manages retrieval of patch set contents, including file revisions and associated comments, from Gerrit API,
 * supporting arbitrary patch set contents, including comparisons.
 * 
 * @author Miles Parker
 */
public class PatchSetContentCompareRemoteFactory extends PatchSetContentRemoteFactory<PatchSetContent> {

	public PatchSetContentCompareRemoteFactory(GerritRemoteFactoryProvider gerritRemoteFactoryProvider) {
		super(gerritRemoteFactoryProvider);
	}

	@Override
	public PatchSetContent pull(IReviewItemSet parentObject, PatchSetContent content, IProgressMonitor monitor)
			throws CoreException {
		return super.pull(parentObject, content, monitor);
	}

	@Override
	public PatchSetContent getRemoteKey(PatchSetContent remoteObject) {
		return remoteObject;
	}

	@Override
	public String getLocalKeyForRemoteKey(PatchSetContent content) {
		return content.getId();
	}

	@Override
	public PatchSetContent getRemoteObjectForLocalKey(IReviewItemSet parentObject, String localKey) {
		return super.getRemoteKeyForLocalKey(parentObject, localKey);
	}

	@Override
	public PatchSetContent getRemoteKeyForLocalKey(IReviewItemSet parentObject, String localKey) {
		PatchSetDetailRemoteFactory itemSetFactory = ((GerritRemoteFactoryProvider) getFactoryProvider())
				.getReviewItemSetFactory();
		PatchSetDetail detail = itemSetFactory.getRemoteKeyForLocalKey(parentObject.getReview(), localKey);
		if (detail != null) {
			return new PatchSetContent((PatchSet) null, detail);
		}
		return null;
	}
}
