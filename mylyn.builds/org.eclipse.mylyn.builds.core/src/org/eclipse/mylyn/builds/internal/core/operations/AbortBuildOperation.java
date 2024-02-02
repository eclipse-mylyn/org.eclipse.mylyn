/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
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
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildsCorePlugin;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.osgi.util.NLS;

public class AbortBuildOperation extends AbstractElementOperation<IBuild> {

	private final IBuild build;

	private final BuildServer server;

	public AbortBuildOperation(IOperationService service, IBuild build) {
		super(service);
		Assert.isNotNull(build);
		this.build = build;
		server = (BuildServer) build.getServer();
	}

	@Override
	protected BuildJob doCreateJob(IBuild build) {
		return new BuildJob(NLS.bind("Aborting Build {0}", build.getLabel())) {
			@Override
			protected IStatus doExecute(IOperationMonitor progress) {
				MultiStatus result = new MultiStatus(BuildsCorePlugin.ID_PLUGIN, 0, "Aborting of build failed", null);
				try {
					server.getBehaviour().abortBuild(build, progress);
				} catch (UnsupportedOperationException | CoreException e) {
					result.add(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
							NLS.bind("Aborting build ''{0}'' failed", build.getName()), e));
				} catch (OperationCanceledException e) {
					return Status.CANCEL_STATUS;
				}
				setStatus(result);
				return Status.OK_STATUS;
			}
		};
	}

	@Override
	protected List<IBuild> doSyncInitInput() {
		return Collections.singletonList(build);
	}

}