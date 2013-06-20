/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miles Parker, Tasktop Technologies - initial API and implementation
 *     Steffen Pingel, Tasktop Technologies - original GerritUtil implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.client.PatchSetContent;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;

import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.PatchSet;

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
	public PatchSetContent pull(IReviewItemSet parentObject, String key, IProgressMonitor monitor) throws CoreException {
		RemoteEmfConsumer<IReview, IReviewItemSet, String, PatchSetDetail, PatchSetDetail, String> itemSetConsumer = getGerritProvider().getReviewItemSetFactory()
				.getConsumerForLocalKey(parentObject.getReview(), key);
		PatchSetDetail detail = itemSetConsumer.getRemoteObject();
		if (detail == null) {
			itemSetConsumer.pull(false, monitor);
			detail = itemSetConsumer.getRemoteObject();
		}
		if (detail != null) {
			PatchSetContent content = new PatchSetContent((PatchSet) null, detail);
			return pull(parentObject, content, monitor);
		}
		throw new CoreException(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID,
				"Couldn't obtain patch set detail for " + key + ". Check remote connection."));
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
