/******************************************************************************
 * *
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.builds.core.util.ProgressUtil;
import org.eclipse.mylyn.commons.repositories.RepositoryValidator;
import org.eclipse.mylyn.internal.builds.core.BuildServer;

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
			return behaviour.validate(ProgressUtil.convert(monitor));
		} catch (CoreException e) {
			return new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, "Server validation failed", e);
		}
	}

}
