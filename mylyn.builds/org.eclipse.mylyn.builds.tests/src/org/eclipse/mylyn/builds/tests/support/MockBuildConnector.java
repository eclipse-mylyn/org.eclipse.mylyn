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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.builds.tests.support;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildFactory;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.BuildConnector;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class MockBuildConnector extends BuildConnector {

	public static final String KIND = "org.eclipse.mylyn.builds.tests.mock";

	public MockBuildConnector() {
	}

	@Override
	public BuildServerBehaviour getBehaviour(RepositoryLocation location) throws CoreException {
		return new MockBuildServerBehaviour();
	}

	@Override
	public IBuildElement getBuildElementFromUrl(IBuildServer server, String url) {
		if (url.startsWith(server.getUrl())) {
			IBuildPlan plan = IBuildFactory.INSTANCE.createBuildPlan();
			plan.setId("1");
			plan.setServer(server);

			IBuild build = IBuildFactory.INSTANCE.createBuild();
			build.setUrl(server.getUrl());
			build.setId("1");
			build.setLabel("1");
			build.setPlan(plan);

			return build;
		}
		return null;
	}

}
