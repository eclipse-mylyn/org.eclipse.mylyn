/*******************************************************************************
 * Copyright (c) 2010, 2013 Markus Knittig and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Markus Knittig - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Markus Knittig
 * @author Steffen Pingel
 */
public class BuildEditorInput implements IEditorInput {
	public enum BuildInfo {
		PARTIAL, COMPLETE, ERROR;
	}

	private BuildInfo buildInfo = BuildInfo.COMPLETE;

	private IBuildPlan plan;

	private IBuild build;

	public BuildEditorInput(IBuildPlan plan) {
		Assert.isNotNull(plan);
		this.plan = plan;
		build = null;
	}

	public BuildEditorInput(IBuild build) {
		Assert.isNotNull(build);
		Assert.isNotNull(build.getPlan());
		plan = build.getPlan();
		this.build = build;
	}

	public BuildEditorInput(IBuildPlan plan, boolean partial) {
		this(plan);
		if (partial) {
			buildInfo = BuildInfo.PARTIAL;
		}
	}

	public BuildEditorInput(IBuild build, boolean partial) {
		this(build);
		if (partial) {
			buildInfo = BuildInfo.PARTIAL;
		}
	}

	public IBuild getBuild() {
		return build;
	}

	public IBuildPlan getPlan() {
		return plan;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IEditorInput.class) {
			return this;
		}
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		if (build != null) {
			return NLS.bind("{0}#{1}", plan.getLabel(), build.getLabel()); //$NON-NLS-1$
		} else {
			return plan.getLabel();
		}
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return ""; //$NON-NLS-1$
	}

	protected BuildInfo getBuildInfo() {
		return buildInfo;
	}

	public void updateBuildInfo(IBuild build, BuildInfo buildInfo) {
		this.build = build;
		plan = build.getPlan();
		this.buildInfo = buildInfo;
	}

}
