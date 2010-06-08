/*******************************************************************************
 * Copyright (c) 2010 Markus Knittig and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Knittig - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.core;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.IOperationMonitor;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient;

/**
 * @author Markus Knittig
 */
public class HudsonServerBehaviour extends BuildServerBehaviour {

	private final RestfulHudsonClient client;

	public HudsonServerBehaviour(IBuildServer server) {
		super(server);
		client = new RestfulHudsonClient(new WebLocation(server.getRepositoryUrl()));
	}

	@Override
	public List<IBuildPlan> getPlans(IOperationMonitor monitor) throws CoreException {
		return null;
	}

	@Override
	public IStatus validate(IOperationMonitor monitor) throws CoreException {
		return client.validate(monitor);
	}

}
