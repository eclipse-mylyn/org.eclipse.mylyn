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

package org.eclipse.mylyn.versions.core;

/**
 * @author mattk
 */
public class Change {
	private final ScmArtifact base;

	private final ScmArtifact target;

	public Change(ScmArtifact base, ScmArtifact target) {
		super();
		this.base = base;
		this.target = target;
	}

	public ScmArtifact getBase() {
		return base;
	}

	public ScmArtifact getTarget() {
		return target;
	}

}
