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

	private final Map<String, String> fAtrributes = new HashMap<>();

	public Change(ScmArtifact base, ScmArtifact target, ChangeType changeType) {
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

	@Override
	public Map<String, String> getInfoAtrributes() {
		return fAtrributes;
	}

}
