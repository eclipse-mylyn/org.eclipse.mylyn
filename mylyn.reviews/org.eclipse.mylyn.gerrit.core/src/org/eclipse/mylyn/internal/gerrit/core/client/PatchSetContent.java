/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchScriptX;

import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.PatchSet;

/**
 * Defines a single patch set, that is a state comparison between an (optional) base and target. The content can then be populated by
 * calling {@link GerritClient#loadPatchSetContent(GerritPatchSetContent, org.eclipse.core.runtime.IProgressMonitor)}.
 *
 * @author Steffen Pingel
 * @author Miles Parker
 */
public class PatchSetContent {

	private final PatchSet base;

	private PatchSet target;

	private PatchSetDetail targetDetail;

	Map<Patch.Key, PatchScriptX> patchScriptByPatchKey;

	/**
	 * Creates empty patch set content using detailed target.
	 *
	 * @param base
	 *            may be null, in which case the target will be compared to an empty baseline
	 * @param targetDetail
	 */
	public PatchSetContent(PatchSet base, PatchSetDetail targetDetail) {
		this.base = base;
		this.targetDetail = targetDetail;
		patchScriptByPatchKey = new HashMap<>();
	}

	/**
	 * Creates empty patch set content using basic patch set.
	 *
	 * @param base
	 *            may be null, in which case the target will be compared to an empty baseline
	 * @param target
	 */
	public PatchSetContent(PatchSet base, PatchSet target) {
		this.base = base;
		this.target = target;
		patchScriptByPatchKey = new HashMap<>();
	}

	public PatchSet getBase() {
		return base;
	}

	public PatchSet getTarget() {
		if (targetDetail != null) {
			target = targetDetail.getPatchSet();
		}
		return target;
	}

	/**
	 * Returns null if not supplied by constructor, unless initialized by
	 * {@link GerritClient#loadPatchSetContent(GerritPatchSetContent, org.eclipse.core.runtime.IProgressMonitor)}
	 *
	 * @return
	 */
	public PatchSetDetail getTargetDetail() {
		return targetDetail;
	}

	public void setTargetDetail(PatchSetDetail targetDetail) {
		this.targetDetail = targetDetail;
	}

	void putPatchScriptByPatchKey(Patch.Key key, PatchScriptX script) {
		patchScriptByPatchKey.put(key, script);
	}

	public PatchScriptX getPatchScript(Patch.Key key) {
		return patchScriptByPatchKey.get(key);
	}

	public String getId() {
		String id = ""; //$NON-NLS-1$
		if (getBase() != null) {
			id += getBase().getId() + "-"; //$NON-NLS-1$
		}
		if (getTarget() != null) {
			id += getTarget().getId();
		}
		return id;
	}
}
