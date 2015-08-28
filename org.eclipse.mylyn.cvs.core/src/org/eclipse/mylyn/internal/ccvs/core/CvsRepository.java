/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.ccvs.core;

import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.team.internal.ccvs.core.ICVSRepositoryLocation;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("restriction")
public class CvsRepository extends ScmRepository {

	private final ICVSRepositoryLocation location;

	public CvsRepository(CvsConnector connector, ICVSRepositoryLocation location) {
		this.location = location;
		setName(location.getLocation(true));
		setUrl(location.getLocation(true));
		setConnector(connector);
	}

	public ICVSRepositoryLocation getLocation() {
		return location;
	}

}
