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
		this.realm = new MockBuildModelRealm();
	}

	public BuildServerBehaviour loadBehaviour(BuildServer server) throws CoreException {
		if (server.getOriginal() != null && server.getOriginal().getBehaviour() != null) {
			return server.getOriginal().getBehaviour();
		}
		return new MockBuildServerBehaviour(server);
	}

	public IBuildModelRealm getRealm() {
		return realm;
	}

}
