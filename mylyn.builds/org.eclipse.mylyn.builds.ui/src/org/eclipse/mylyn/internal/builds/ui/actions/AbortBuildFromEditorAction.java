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

package org.eclipse.mylyn.internal.builds.ui.actions;

import java.util.Collections;

import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest.Scope;
import org.eclipse.mylyn.builds.internal.core.operations.AbortBuildOperation;
import org.eclipse.mylyn.builds.internal.core.operations.GetBuildsOperation;
import org.eclipse.mylyn.builds.internal.core.operations.OperationChangeEvent;
import org.eclipse.mylyn.builds.internal.core.operations.OperationChangeListener;
import org.eclipse.mylyn.commons.workbench.EditorHandle;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.editor.BuildEditor;
import org.eclipse.mylyn.internal.builds.ui.editor.RefreshBuildEditorOperationListener;
import org.eclipse.swt.widgets.Display;

public class AbortBuildFromEditorAction extends AbortBuildAction {

	private final BuildEditor editor;

	public AbortBuildFromEditorAction(BuildEditor editor) {
		this.editor = editor;
	}

	@Override
	public void run() {
		Object selection = getStructuredSelection().getFirstElement();
		if (selection instanceof IBuild build) {
			abortBuild(build, editor);
		}
	}

	public static void abortBuild(final IBuild build, final BuildEditor editor) {
		AbortBuildOperation operation = BuildsUiInternal.getFactory().getAbortBuildOperation(build);

		operation.addOperationChangeListener(new OperationChangeListener() {
			@Override
			public void done(OperationChangeEvent event) {
				if (event.getStatus().isOK()) {
					Display.getDefault().asyncExec(() -> {
						IBuildPlan plan = build.getPlan();
						String label = build.getLabel();
						EditorHandle handle = new EditorHandle();
						handle.setPart(editor);
						GetBuildsRequest request = new GetBuildsRequest(plan, Collections.singletonList(label),
								Scope.FULL);
						GetBuildsOperation operation1 = BuildsUiInternal.getFactory().getGetBuildsOperation(request);
						operation1
								.addOperationChangeListener(new RefreshBuildEditorOperationListener(build, handle));
						operation1.execute();
						if (build.getBuildNumber() == plan.getLastBuild().getBuildNumber()) {
							BuildsUiInternal.getFactory().getRefreshOperation(build).execute();
						}
					});
				}
			}
		});
		operation.execute();
	}
}
