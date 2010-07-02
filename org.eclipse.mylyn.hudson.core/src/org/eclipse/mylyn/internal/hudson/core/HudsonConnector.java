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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.BuildConnector;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;

/**
 * @author Markus Knittig
 */
public class HudsonConnector extends BuildConnector {

	@Override
	public BuildServerBehaviour getBehaviour(IBuildServer server) throws CoreException {
		return new HudsonServerBehaviour(createLocation(server));
	}

}
