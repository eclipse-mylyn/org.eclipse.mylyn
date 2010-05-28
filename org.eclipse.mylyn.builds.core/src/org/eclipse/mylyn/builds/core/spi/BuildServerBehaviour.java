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

package org.eclipse.mylyn.builds.core.spi;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;

/**
 * @author Steffen Pingel
 */
public abstract class BuildServerBehaviour {

	private final IBuildServer server;

	public BuildServerBehaviour(IBuildServer server) {
		this.server = server;
	}

	public abstract List<IBuildPlan> getPlans(IProgressMonitor monitor);

	public final IBuildServer getServer() {
		return server;
	}

	public abstract IStatus validate();

}
