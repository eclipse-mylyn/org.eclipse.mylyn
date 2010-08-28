/*******************************************************************************
 * Copyright (c) 2010 Markus Knittig and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Knittig - initial API and implementation
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
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.spi.RunBuildRequest;
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildsCorePlugin;
import org.eclipse.mylyn.commons.core.IOperationMonitor;
import org.eclipse.osgi.util.NLS;

/**
 * @author Markus Knittig
 * @author Steffen Pingel
 */
public class RunBuildOperation extends AbstractElementOperation<IBuildPlan> {

	private final RunBuildRequest request;

	private final BuildServer server;

	public RunBuildOperation(IOperationService service, RunBuildRequest request) {
		super(service);
		Assert.isNotNull(request);
		this.request = request;
		this.server = (BuildServer) request.getPlan().getServer();
	}

	@Override
	protected BuildJob doCreateJob(IBuildPlan element) {
		return new BuildJob(NLS.bind("Running Build {0}", element.getLabel())) {
			@Override
			protected IStatus doExecute(IOperationMonitor progress) {
				MultiStatus result = new MultiStatus(BuildsCorePlugin.ID_PLUGIN, 0, "Running of build failed", null);
				try {
					server.getBehaviour().runBuild(request, progress);
				} catch (CoreException e) {
					result.add((new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN, NLS.bind(
							"Running build ''{0}'' failed", request.getPlan().getName(), e))));
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

}
