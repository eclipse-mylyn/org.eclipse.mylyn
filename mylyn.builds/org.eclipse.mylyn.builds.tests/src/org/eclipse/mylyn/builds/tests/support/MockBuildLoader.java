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

package org.eclipse.mylyn.builds.tests.support;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.builds.internal.core.IBuildLoader;
import org.eclipse.mylyn.builds.internal.core.IBuildModelRealm;

/**
 * @author Steffen Pingel
 */
public class MockBuildLoader implements IBuildLoader {

	private final MockBuildModelRealm realm;

	public MockBuildLoader() {
		realm = new MockBuildModelRealm();
	}

	@Override
	public BuildServerBehaviour loadBehaviour(BuildServer server) throws CoreException {
		if (server.getOriginal() != null && server.getOriginal().getBehaviour() != null) {
			return server.getOriginal().getBehaviour();
		}
		return new MockBuildServerBehaviour(server);
	}

	@Override
	public IBuildModelRealm getRealm() {
		return realm;
	}

}
