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
import org.eclipse.mylyn.internal.gerrit.core.client.PatchSetContent;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;

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
}
