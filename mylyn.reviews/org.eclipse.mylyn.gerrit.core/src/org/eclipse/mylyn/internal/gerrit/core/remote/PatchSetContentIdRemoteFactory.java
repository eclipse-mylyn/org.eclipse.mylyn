/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson and others.
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
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;

import com.google.gerrit.common.data.PatchSetDetail;

/**
 * Manages retrieval of patch set contents, including file revisions and associated comments, from Gerrit API,
 * supporting key based retrieval of single patch sets.
 * 
 * @author Miles Parker
 */
public class PatchSetContentIdRemoteFactory extends PatchSetContentRemoteFactory<String> {

	public PatchSetContentIdRemoteFactory(GerritRemoteFactoryProvider gerritRemoteFactoryProvider) {
		super(gerritRemoteFactoryProvider);
	}

	@Override
	public PatchSetContent pull(IReviewItemSet parentObject, String key, IProgressMonitor monitor)
			throws CoreException {
		RemoteEmfConsumer<IReview, IReviewItemSet, String, PatchSetDetail, PatchSetDetail, String> itemSetConsumer = getGerritProvider()
				.getReviewItemSetFactory()
				.getConsumerForLocalKey(parentObject.getReview(), key);
		PatchSetDetail detail = itemSetConsumer.getRemoteObject();
		if (detail == null) {
			itemSetConsumer.pull(false, monitor);
			detail = itemSetConsumer.getRemoteObject();
		}
		if (detail != null) {
			PatchSetContent content = new PatchSetContent(null, detail);
			return pull(parentObject, content, monitor);
		}
		return null;
	}

	@Override
	public String getRemoteKey(PatchSetContent remoteObject) {
		return remoteObject.getId();
	}

	@Override
	public String getLocalKeyForRemoteKey(String remoteKey) {
		return remoteKey;
	}

	@Override
	public String getRemoteKeyForLocalKey(IReviewItemSet parentObject, String localKey) {
		return localKey;
	}
}
