/*******************************************************************************
 * Copyright (c) 2011 Ericsson AB and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *   Ericsson AB - Initial API and Implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.subclipse.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.tigris.subversion.subclipse.core.ISVNRepositoryLocation;
import org.tigris.subversion.subclipse.core.resources.SVNWorkspaceRoot;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * @author Alvaro Sanchez-Leon
 */
@SuppressWarnings("restriction")
public class SubclipseRepository extends ScmRepository {

	private final ISVNRepositoryLocation location;

	private final SVNUrl folderUrlStr;

	public SubclipseRepository(SubclipseConnector connector, ISVNRepositoryLocation location,
			IProject aSelectedProject) {
		this.location = location;
		setName(location.getLocation());
		setUrl(location.getUrl().toString());
		setConnector(connector);
		folderUrlStr = SVNWorkspaceRoot.getSVNFolderFor(aSelectedProject).getUrl();
	}

	public ISVNRepositoryLocation getLocation() {
		return location;
	}

	public SVNUrl getProjectSVNFolder() {
		return folderUrlStr;
	}

}
