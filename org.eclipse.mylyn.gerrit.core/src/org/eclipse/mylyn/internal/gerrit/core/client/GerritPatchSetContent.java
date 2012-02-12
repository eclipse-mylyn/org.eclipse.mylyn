/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.util.Map;

import com.google.gerrit.common.data.PatchScript;
import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.PatchSet;

/**
 * @author Steffen Pingel
 */
public class GerritPatchSetContent {

	Map<Patch.Key, PatchScript> patchScriptByPatchKey;

	private final PatchSet base;

	private final PatchSetDetail target;

	public GerritPatchSetContent(PatchSet base, PatchSetDetail target, Map<Patch.Key, PatchScript> patchScriptByPatchKey) {
		this.base = base;
		this.target = target;
		this.patchScriptByPatchKey = patchScriptByPatchKey;
	}

	public GerritPatchSetContent(PatchSetDetail target) {
		this(null, target, null);
	}

	public PatchSet getBase() {
		return base;
	}

	public PatchSetDetail getTarget() {
		return target;
	}

	public Map<Patch.Key, PatchScript> getPatchScriptByPatchKey() {
		return patchScriptByPatchKey;
	}

}
