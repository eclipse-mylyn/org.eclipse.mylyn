/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.internal.core.operations.GetBuildsOperation;
import org.eclipse.mylyn.builds.internal.core.operations.OperationChangeEvent;
import org.eclipse.mylyn.builds.internal.core.operations.OperationChangeListener;
import org.eclipse.mylyn.commons.workbench.EditorHandle;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.mylyn.internal.builds.ui.editor.BuildEditorInput.BuildInfo;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class RefreshBuildEditorOperationListener extends OperationChangeListener {

	private final IBuild build;

	private final EditorHandle handle;

	public RefreshBuildEditorOperationListener(IBuild build, EditorHandle handle) {
		this.build = build;
		this.handle = handle;
	}

	@Override
	public void done(OperationChangeEvent event) {
		IBuildPlan plan = build.getPlan();
		String label = build.getLabel();
		if (event.getStatus().isOK() && !Display.getDefault().isDisposed()) {
			GetBuildsOperation operation = (GetBuildsOperation) event.getOperation();
			List<IBuild> builds = operation.getBuilds();
			if (builds != null && builds.size() > 0) {
				IBuild updatedBuild = builds.get(0);
				updatedBuild.setPlan(plan);
				updatedBuild.setServer(plan.getServer());
				updateBuildInfo(updatedBuild, BuildInfo.COMPLETE);
			} else {
				IStatus status = new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN,
						NLS.bind("The requested build ''{0}'' was not found", label)); //$NON-NLS-1$
				handle.setStatus(status);
				updateBuildInfo(build, BuildInfo.ERROR);
			}
		} else {
			handle.setStatus(event.getStatus());
			updateBuildInfo(build, BuildInfo.ERROR);
		}
	}

	private void updateBuildInfo(IBuild updatedBuild, BuildInfo buildInfo) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
			if (handle.getPart() instanceof BuildEditor) {
				BuildEditor editor = (BuildEditor) handle.getPart();
				if (shouldUpdate(editor)) {
					editor.getEditorInput().updateBuildInfo(updatedBuild, buildInfo);
					editor.refresh();
				}
			}
		});
	}

	private boolean shouldUpdate(BuildEditor editor) {
		return !editor.isDisposed() && !editor.getEditorSite().getWorkbenchWindow().getShell().isDisposed();
	}

}
