/*******************************************************************************
 * Copyright (c) 2011 Ericsson AB and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Ericsson AB - Initial API and Implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.subclipse.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.mylyn.versions.core.spi.ScmResourceArtifact;

/**
 * @author Alvaro Sanchez-Leon
 */
public class SubclipseArtifact extends ScmResourceArtifact {

	public SubclipseArtifact(SubclipseConnector connector, IResource resource, String id) {
		super(connector, resource, id);
	}

}
