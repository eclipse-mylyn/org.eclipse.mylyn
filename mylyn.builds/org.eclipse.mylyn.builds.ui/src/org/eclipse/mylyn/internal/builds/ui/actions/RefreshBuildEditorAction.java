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

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest.Scope;
import org.eclipse.mylyn.builds.internal.core.operations.GetBuildsOperation;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.EditorHandle;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.editor.BuildEditor;
import org.eclipse.mylyn.internal.builds.ui.editor.RefreshBuildEditorOperationListener;

public class RefreshBuildEditorAction extends Action {

	private final BuildEditor editor;

	public RefreshBuildEditorAction(BuildEditor editor) {
		super(Messages.RefreshBuildEditorAction_RefreshBuildEditor);
		setImageDescriptor(CommonImages.REFRESH);
		this.editor = editor;
	}

	public void updateEnablement() {
		setEnabled(getBuild().getState() != BuildState.STOPPED);
	}

	@Override
	public void run() {
		IBuild build = getBuild();
		IBuildPlan plan = build.getPlan();
		String label = build.getLabel();
		EditorHandle handle = new EditorHandle();
		handle.setPart(editor);
		GetBuildsRequest request = new GetBuildsRequest(plan, Collections.singletonList(label), Scope.FULL);
		GetBuildsOperation operation = BuildsUiInternal.getFactory().getGetBuildsOperation(request);
		operation.addOperationChangeListener(new RefreshBuildEditorOperationListener(build, handle));
		operation.execute();
	}

	private IBuild getBuild() {
		return editor.getEditorInput().getBuild();
	}

}
