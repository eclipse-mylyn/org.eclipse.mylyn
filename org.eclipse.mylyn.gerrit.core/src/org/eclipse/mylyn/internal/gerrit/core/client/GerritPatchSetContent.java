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
import com.google.gerrit.reviewdb.Patch;

/**
 * @author Steffen Pingel
 */
public class GerritPatchSetContent {

	Map<Patch.Key, PatchScript> patchScriptByPatchKey;

	public Map<Patch.Key, PatchScript> getPatchScriptByPatchKey() {
		return patchScriptByPatchKey;
	}

	void setPatchScriptByPatchKey(Map<Patch.Key, PatchScript> patchScriptByPatchKey) {
		this.patchScriptByPatchKey = patchScriptByPatchKey;
	}

}
