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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.commons.core.IOperationMonitor;
import org.eclipse.mylyn.internal.builds.core.Build;
import org.eclipse.mylyn.internal.builds.core.BuildServer;
import org.eclipse.mylyn.internal.builds.core.BuildsCorePlugin;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class GetBuildOutputOperation extends BuildJob {

	private final IBuild build;

	private final BuildServer server;

	private final StringBuffer consoleOutput = new StringBuffer();

	public GetBuildOutputOperation(IBuild build) {
		super("Retrieving build output");
		Assert.isNotNull(build);
		this.build = ((Build) build).createWorkingCopy();
		this.server = (BuildServer) build.getServer();
	}

	@Override
	protected IStatus doExecute(IOperationMonitor monitor) {
		MultiStatus result = new MultiStatus(BuildsCorePlugin.ID_PLUGIN, 0, "Running of build failed", null);
		try {
			doGetOutput(build, monitor);
		} catch (CoreException e) {
			result.add((new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN, NLS.bind("Run of build ''{0}'' failed",
					build.getName(), e))));
		} catch (OperationCanceledException e) {
			return Status.CANCEL_STATUS;
		}
		setStatus(result);
		return Status.OK_STATUS;
	}

	public void doGetOutput(IBuild build, IOperationMonitor monitor) throws CoreException {
		try {
			InputStream in = server.getBehaviour().getConsole(build, monitor);
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = reader.readLine()) != null) {
					consoleOutput.append(line);
				}
			} finally {
				in.close();
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
					"Failed to retrieve console output", e));
		}
	}

	public String getConsoleOutput() {
		return consoleOutput.toString();
	}

}
