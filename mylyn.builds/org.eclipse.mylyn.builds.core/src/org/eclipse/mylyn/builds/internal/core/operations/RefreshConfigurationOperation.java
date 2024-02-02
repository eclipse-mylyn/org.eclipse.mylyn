/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.BuildServerConfiguration;
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildsCorePlugin;
import org.eclipse.mylyn.builds.internal.core.util.BuildRunnableWithResult;
import org.eclipse.mylyn.builds.internal.core.util.BuildRunner;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class RefreshConfigurationOperation extends BuildJob {

	private final List<IBuildServer> servers;

	public RefreshConfigurationOperation(List<IBuildServer> servers) {
		super("Refresh Configuration");
		Assert.isNotNull(servers);
		this.servers = new ArrayList<>(servers.size());
		for (IBuildServer server : servers) {
			this.servers.add(((BuildServer) server).createWorkingCopy());
		}
	}

	@Override
	protected IStatus doExecute(IOperationMonitor progress) {
		MultiStatus result = new MultiStatus(BuildsCorePlugin.ID_PLUGIN, 0, "Refreshing of builds failed", null);
		if (servers.size() == 1) {
			progress.beginTask("Refreshing configuration", servers.size());
		} else {
			progress.beginTask("Refreshing server configurations", servers.size());
		}
		for (IBuildServer server : servers) {
			try {
				progress.subTask(NLS.bind("{0}", server.getLabel()));
				doRefresh((BuildServer) server, progress.newChild(1));
			} catch (CoreException e) {
				result.add(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
						NLS.bind("Refresh of server ''{0}'' failed", server.getName()), e));
			} catch (OperationCanceledException e) {
				return Status.CANCEL_STATUS;
			}
		}
		setStatus(result);
		return Status.OK_STATUS;
	}

	public void doRefresh(final BuildServer server, final IOperationMonitor monitor) throws CoreException {
		Object result = BuildRunner.run(new BuildRunnableWithResult<BuildServerConfiguration>() {
			@Override
			public BuildServerConfiguration run() throws CoreException {
				return server.getBehaviour().refreshConfiguration(monitor);
			}
		});
		if (result == null) {
			throw new CoreException(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
					"Server did not provide a valid configuration."));
		}
	}

}
