/*******************************************************************************
 * Copyright (c) 2013 Ericsson, Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.factories;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.gerrit.core.GerritTaskSchema;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.internal.gerrit.ui.operations.PublishDialog;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUi;

import com.google.gerrit.common.data.PatchSetPublishDetail;
import com.google.gerrit.reviewdb.PatchSet;

/**
 * @author Steffen Pingel
 * @author Miles Parker
 */
public class PublishUiFactory extends AbstractPatchSetUiFactory {

	private final int addedDrafts;

	public PublishUiFactory(IUiContext context, IReviewItemSet set) {
		super("Publish Comments...", context, set);
		this.addedDrafts = 0;
	}

	@Override
	public void execute() {
		PatchSet.Id patchSetId = getPatchSetDetail().getPatchSet().getId();
		final PatchSetPublishDetail publishDetail = getChange().getPublishDetailByPatchSetId().get(patchSetId);

		TaskAttribute comment = getTaskData().getRoot().getAttribute(TaskAttribute.COMMENT_NEW);
		String editorCommentText = comment != null ? comment.getValue() : "";
		int open = new PublishDialog(getShell(), getTask(), publishDetail, addedDrafts, editorCommentText).open(getEditor());
		if (open == Window.OK && comment != null) {
			comment.clearValues();
			if (getTaskEditorPage() != null) {
				getTaskEditorPage().doSave(new NullProgressMonitor());
			}
			try {
				TasksUi.getTaskDataManager().discardEdits(getTask());
			} catch (CoreException e) {
				Status status = new Status(IStatus.ERROR, GerritUiPlugin.PLUGIN_ID,
						"Error while clearing task status.", e);
				TasksUiInternal.displayStatus("Clear outgoing task status failed", status);
			}
		}
	}

	@Override
	public boolean isExecutable() {
		return getTaskData().getAttributeMapper().getBooleanValue(
				getTaskData().getRoot().getAttribute(GerritTaskSchema.getDefault().CAN_PUBLISH.getKey()));
	}

}
