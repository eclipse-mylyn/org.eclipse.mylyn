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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.internal.core.Build;
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildsCorePlugin;
import org.eclipse.mylyn.commons.core.IOperationMonitor;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class GetBuildOutputOperation extends AbstractElementOperation<IBuild> {

	public static class BuildOutputEvent {

		private BufferedReader input;

		public BufferedReader getInput() {
			return input;
		}

	}

	public static abstract class BuildOutputReader {

		public abstract void handle(BuildOutputEvent event, IOperationMonitor monitor) throws IOException,
				CoreException;

		public abstract void done();

	}

	private final IBuild build;

	private final BuildServer server;

	private final BuildOutputReader reader;

	public GetBuildOutputOperation(IOperationService service, IBuild build, BuildOutputReader reader) {
		super(service);
		Assert.isNotNull(build);
		Assert.isNotNull(reader);
		this.build = build;
		this.reader = reader;
		this.server = (BuildServer) build.getServer();
	}

	public void doGetOutput(IBuild build, IOperationMonitor monitor) throws CoreException {
		try {
			Reader in = server.getBehaviour().getConsole(build, monitor);
			try {
				BuildOutputEvent event = new BuildOutputEvent();
				event.input = new BufferedReader(in);
				reader.handle(event, monitor);
			} finally {
				in.close();
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
					"Failed to retrieve build output", e));
		} finally {
			reader.done();
		}
	}

	@Override
	protected BuildJob doCreateJob(final IBuild build) {
		return new BuildJob(NLS.bind("Retrieving Output for Build {0}", build.getLabel())) {
			@Override
			protected IStatus doExecute(IOperationMonitor monitor) {
				try {
					doGetOutput(build, monitor);
				} catch (CoreException e) {
					setStatus(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN, NLS.bind(
							"Unexpected error while retrieving output for build ''{0}''.", build.getName()), e));
				} catch (OperationCanceledException e) {
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			}
		};
	}

	@Override
	protected List<IBuild> doSyncInitInput() {
		return Collections.singletonList((IBuild) ((Build) build).createWorkingCopy());
	}

}
