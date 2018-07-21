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
