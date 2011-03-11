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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.versions.core.spi.ScmInfoAttributes;

/**
 * @author mattk
 */
public class Change implements ScmInfoAttributes {
	private final ScmArtifact base;

	private final ScmArtifact target;

	private final ChangeType changeType;

	private final Map<String, String> fAtrributes = new HashMap<String, String>();

	public Change(ScmArtifact base, ScmArtifact target, ChangeType changeType) {
		super();
		this.base = base;
		this.target = target;
		this.changeType = changeType;
	}

	public ScmArtifact getBase() {
		return base;
	}

	public ScmArtifact getTarget() {
		return target;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public Map<String, String> getInfoAtrributes() {
		return fAtrributes;
	}

}
