/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.builds.internal.core.operations;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest;
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildsCorePlugin;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class GetBuildsOperation extends AbstractElementOperation<IBuildPlan> {

	private List<IBuild> builds;

	private final GetBuildsRequest request;

	private final BuildServer server;

	public GetBuildsOperation(IOperationService service, GetBuildsRequest request) {
		super(service);
		Assert.isNotNull(request);
		Assert.isNotNull(request.getPlan());
		Assert.isNotNull(request.getPlan().getServer());
		this.request = request;
		server = (BuildServer) request.getPlan().getServer();
	}

	@Override
	protected BuildJob doCreateJob(IBuildPlan element) {
		return new BuildJob(NLS.bind("Retrieving Builds for {0}", element.getLabel())) {
			@Override
			protected IStatus doExecute(IOperationMonitor progress) {
				MultiStatus result = new MultiStatus(BuildsCorePlugin.ID_PLUGIN, 0, "Retrieval of builds failed", null);
				try {
					builds = server.getBehaviour().getBuilds(request, progress);
				} catch (CoreException e) {
					result.add(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
							NLS.bind("Getting build ''{0}'' failed", request.getPlan().getName()), e));
				} catch (OperationCanceledException e) {
					return Status.CANCEL_STATUS;
				}
				setStatus(result);
				return Status.OK_STATUS;
			}
		};
	}

	@Override
	protected List<IBuildPlan> doSyncInitInput() {
		return Collections.singletonList(request.getPlan());
	}

	public List<IBuild> getBuilds() {
		return builds;
	}

}
