/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.internal.core.operations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildModel;
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildsCorePlugin;
import org.eclipse.mylyn.commons.core.IOperationMonitor;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class RefreshOperation extends AbstractElementOperation<IBuildServer> {

	private final class RefreshJob extends BuildJob {

		private final IBuildServer server;

		private RefreshJob(IBuildServer server) {
			super(NLS.bind("Refreshing Builds ({0})", server.getLabel()));
			this.server = server;
		}

		@Override
		protected IStatus doExecute(IOperationMonitor progress) {
			try {
				RefreshRequest request = new RefreshRequest(model);
				((BuildServer) server).getRefreshSession().refresh(request, progress.newChild(1));
			} catch (CoreException e) {
				setStatus(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN, NLS.bind(
						"Refresh of server ''{0}'' failed", server.getLabel()), e));
			} catch (OperationCanceledException e) {
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		}

		@Override
		public IBuildServer getElement() {
			return server;
		}

	}

	private final BuildModel model;

	private final List<IBuildServer> servers;

	public RefreshOperation(IOperationService service, BuildModel model) {
		this(service, model, null);
	}

	public RefreshOperation(IOperationService service, BuildModel model, List<IBuildElement> elements) {
		super(service);
		Assert.isNotNull(model);
		this.model = model;
		if (elements != null) {
			this.servers = new ArrayList<IBuildServer>();
			for (IBuildElement element : elements) {
				this.servers.add(element.getServer());
			}
		} else {
			this.servers = null;
		}
	}

	@Override
	protected BuildJob doCreateJob(IBuildServer server) {
		return new RefreshJob(server);
	}

	@Override
	protected List<IBuildServer> doInitInput() {
		if (servers != null) {
			register(servers);
			return servers;
		} else {
			return super.doInitInput();
		}
	}

	@Override
	protected List<IBuildServer> doSyncInitInput() {
		List<IBuildServer> servers = new ArrayList<IBuildServer>(model.getServers().size());
		for (IBuildServer server : model.getServers()) {
			if (server.getLocation().isOffline()) {
				continue;
			}
			servers.add(server);
		}
		return servers;
	}

	@Override
	protected void handleResult(final BuildJob job) {
		super.handleResult(job);
		final IStatus status = job.getStatus();
		if (status != Status.CANCEL_STATUS) {
			getService().getRealm().syncExec(new Runnable() {
				public void run() {
					job.getElement().setElementStatus(status);
				}
			});
		}
	}

}
