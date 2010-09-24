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

package org.eclipse.mylyn.scm.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Change {

	ScmUser author;

	Date date;

	String revision;

	String message;

	/**
	 * SHA1 hash.
	 */
	String id;

	List<Artifact> artifacts = new ArrayList<Artifact>();

	public List<Artifact> getArtifacts() {
		return artifacts;
	}

}
