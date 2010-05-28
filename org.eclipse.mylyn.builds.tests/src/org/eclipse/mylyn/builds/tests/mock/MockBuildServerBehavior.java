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

package org.eclipse.mylyn.builds.tests.mock;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;

/**
 * @author Steffen Pingel
 */
public class MockBuildServerBehavior extends BuildServerBehaviour {

	public MockBuildServerBehavior(IBuildServer server) {
		super(server);
	}

	@Override
	public List<IBuildPlan> getPlans(IProgressMonitor monitor) {
		return null;
	}

	@Override
	public IStatus validate() {
		return Status.OK_STATUS;
	}

}
