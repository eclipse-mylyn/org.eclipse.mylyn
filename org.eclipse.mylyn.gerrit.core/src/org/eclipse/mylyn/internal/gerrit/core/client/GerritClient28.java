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

package org.eclipse.mylyn.internal.gerrit.core.client;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ChangeInfo28;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.osgi.framework.Version;

import com.google.gerrit.common.data.ToggleStarRequest;
import com.google.gerrit.reviewdb.Change;
import com.google.gwtjsonrpc.client.VoidResult;

public class GerritClient28 extends GerritClient27 {

	protected GerritClient28(TaskRepository repository, Version version) {
		super(repository, version);
	}

	@Override
	public VoidResult setStarred(final String reviewId, final boolean starred, IProgressMonitor monitor)
			throws GerritException {
		final Change.Id id = new Change.Id(id(reviewId));
		final ToggleStarRequest req = new ToggleStarRequest();
		req.toggle(id, starred);
		final String uri = "/a/accounts/self/starred.changes/" + id.get(); //$NON-NLS-1$

		return execute(monitor, new Operation<VoidResult>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {

				if (starred) {
					executePutRestRequest(uri, req, ToggleStarRequest.class, createErrorHandler(), monitor);
				} else {
					executeDeleteRestRequest(uri, req, ToggleStarRequest.class, createErrorHandler(), monitor);
				}
			}
		});
	}

	protected ChangeInfo28 getAdditionalChangeInfo(int reviewId, IProgressMonitor monitor) {
		ChangeInfo28 changeInfo28 = null;
		try {
			changeInfo28 = executeGetRestRequest("/changes/" + Integer.toString(reviewId) //$NON-NLS-1$
					+ "/?o=CURRENT_REVISION&o=CURRENT_ACTIONS", ChangeInfo28.class, monitor); //$NON-NLS-1$
		} catch (GerritException e) {
			StatusHandler.log(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID, e.getMessage(), e));
		}
		return changeInfo28;
	}
}
