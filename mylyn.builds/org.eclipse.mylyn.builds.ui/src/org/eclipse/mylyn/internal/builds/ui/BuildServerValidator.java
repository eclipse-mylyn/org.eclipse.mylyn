/******************************************************************************
 * *
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

package org.eclipse.mylyn.internal.builds.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.commons.core.operations.OperationUtil;
import org.eclipse.mylyn.commons.repositories.core.RepositoryValidator;

/**
 * @author Steffen Pingel
 */
public class BuildServerValidator extends RepositoryValidator {

	private final IBuildServer server;

	public BuildServerValidator(IBuildServer server) {
		super(server.getLocation());
		this.server = server;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			BuildServerBehaviour behaviour = ((BuildServer) server).getBehaviour();
			return behaviour.validate(OperationUtil.convert(monitor));
		} catch (CoreException e) {
			return new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, "Server validation failed", e);
		}
	}

	public IBuildServer getServer() {
		return server;
	}

}
