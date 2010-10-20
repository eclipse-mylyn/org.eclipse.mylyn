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

package org.eclipse.mylyn.internal.scm.cvs.core;

import org.eclipse.mylyn.scm.core.ScmRepository;
import org.eclipse.team.internal.ccvs.core.ICVSRepositoryLocation;

/**
 * @author Steffen Pingel
 */
public class CvsRepository extends ScmRepository {

	private final ICVSRepositoryLocation location;

	public CvsRepository(ICVSRepositoryLocation location) {
		this.location = location;
	}

	public ICVSRepositoryLocation getLocation() {
		return location;
	}

}
