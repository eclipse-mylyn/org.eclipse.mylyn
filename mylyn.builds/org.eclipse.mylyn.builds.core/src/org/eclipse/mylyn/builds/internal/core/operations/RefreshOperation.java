/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildModel;
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildsCorePlugin;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.osgi.util.NLS;

/**
 * The refresh operation refreshes elements of the model with the latest state from the server.
 * 
 * @author Steffen Pingel
 * @author Lucas Panjer
 */
public class RefreshOperation extends AbstractElementOperation<IBuildServer> {

	private final class RefreshData {

		private IBuildServer server;

		private List<IBuildElement> elements;

	}

	/**
	 * Executes the actual refresh for a specific server.
	 */
	private final class RefreshJob extends BuildJob {

		private final RefreshData data;

		private RefreshJob(RefreshData data) {
			super(NLS.bind("Refreshing Builds ({0})", data.server.getLabel()));
			this.data = data;
		}

		@Override
		protected IStatus doExecute(IOperationMonitor progress) {
			try {
				List<IBuildPlan> plansToRefresh = null;
				for (IBuildElement element : data.elements) {
					if (element instanceof IBuildPlan) {
						if (plansToRefresh == null) {
							plansToRefresh = new ArrayList<>();
						}
						plansToRefresh.add((IBuildPlan) element);
					}
				}
				RefreshRequest request = new RefreshRequest(model, plansToRefresh);
				((BuildServer) data.server).getRefreshSession().refresh(request, progress.newChild(1));
			} catch (CoreException e) {
				setStatus(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
						NLS.bind("Refresh of server ''{0}'' failed", data.server.getLabel()), e));
			} catch (OperationCanceledException e) {
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		}

		@Override
		public boolean belongsTo(Object family) {
			if (family instanceof RefreshJob refreshJob) {
				if (getElement().equals(refreshJob.getElement())) {
					return true;
				}
				return false;
			}
			return super.belongsTo(family);
		}

		@Override
		public IBuildServer getElement() {
			return data.server;
		}

	}

	private final BuildModel model;

	//private final List<IBuildServer> servers;

	private final Map<IBuildServer, RefreshData> dataByServer;

	public RefreshOperation(IOperationService service, BuildModel model) {
		this(service, model, null);
	}

	public RefreshOperation(IOperationService service, BuildModel model, List<IBuildElement> elements) {
		super(service);
		Assert.isNotNull(model);
		this.model = model;
		if (elements != null) {
			dataByServer = new LinkedHashMap<>();
			for (IBuildElement element : elements) {
				RefreshData data = dataByServer.get(element.getServer());
				if (data == null) {
					data = new RefreshData();
					data.server = element.getServer();
					data.elements = new ArrayList<>();
					dataByServer.put(element.getServer(), data);
				}
				// add specific elements to refresh
				if (!(element instanceof IBuildServer)) {
					data.elements.add(element);
				}
			}
		} else {
			dataByServer = null;
		}
	}

	@Override
	protected BuildJob doCreateJob(IBuildServer server) {
		if (dataByServer != null) {
			RefreshData data = dataByServer.get(server);
			return new RefreshJob(data);
		} else {
			RefreshData data = new RefreshData();
			data.server = server;
			data.elements = Collections.emptyList();
			return new RefreshJob(data);
		}
	}

	@Override
	protected List<IBuildServer> doInitInput() {
		if (dataByServer != null) {
			for (Map.Entry<IBuildServer, RefreshData> data : dataByServer.entrySet()) {
				register(Collections.singletonList(data.getKey()));
				register(data.getValue().elements);
			}
			return new ArrayList<>(dataByServer.keySet());
		} else {
			return super.doInitInput();
		}
	}

	@Override
	protected List<IBuildServer> doSyncInitInput() {
		List<IBuildServer> servers = new ArrayList<>(model.getServers().size());
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
			getService().getRealm().exec(() -> job.getElement().setElementStatus(status));
		}
		if (dataByServer != null) {
			final RefreshData data = ((RefreshJob) job).data;
			getService().getRealm().exec(() -> unregister(data.elements));
		}
	}
}
