/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.internal.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;

/**
 * @author Steffen Pingel
 */
public interface IBuildLoader {

	public BuildServerBehaviour loadBehaviour(BuildServer server) throws CoreException;

	public IBuildModelRealm getRealm();

}
