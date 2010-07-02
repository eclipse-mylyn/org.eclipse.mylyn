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

package org.eclipse.mylyn.internal.builds.core.operations;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.IOperationMonitor;
import org.eclipse.mylyn.builds.core.util.ProgressUtil;
import org.eclipse.mylyn.internal.builds.core.BuildsCorePlugin;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class RefreshOperation extends AbstractBuildOperation {

	private final IBuildModel model;

	public RefreshOperation(IBuildModel model) {
		super("Refresh Builds");
		Assert.isNotNull(model);
		this.model = model;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		MultiStatus result = new MultiStatus(BuildsCorePlugin.ID_PLUGIN, 0, "Refreshing of builds failed", null);
		IOperationMonitor progress = ProgressUtil.convert(monitor);
		progress.beginTask("Refreshing builds", model.getServers().size());
		for (IBuildServer server : model.getServers()) {
			try {
				server.refreshPlans(progress.newChild(1));
			} catch (CoreException e) {
				result.add(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN, NLS.bind(
						"Refresh of server ''{0}'' failed", server.getName()), e));
			} catch (OperationCanceledException e) {
				return Status.CANCEL_STATUS;
			}
		}
		setStatus(result);
		return Status.OK_STATUS;
	}

}
