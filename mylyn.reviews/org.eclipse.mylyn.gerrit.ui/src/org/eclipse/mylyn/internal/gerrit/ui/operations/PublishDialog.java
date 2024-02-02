/*******************************************************************************
 * Copyright (c) 2011, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchSetPublishDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PermissionLabel;
import org.eclipse.mylyn.internal.gerrit.core.operations.GerritOperation;
import org.eclipse.mylyn.internal.gerrit.core.operations.PublishRequest;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextEditor;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.google.gerrit.common.data.ApprovalType;
import com.google.gerrit.common.data.GerritConfig;
import com.google.gerrit.common.data.PatchSetPublishDetail;
import com.google.gerrit.reviewdb.ApprovalCategoryValue;
import com.google.gerrit.reviewdb.PatchSetApproval;

/**
 * @author Steffen Pingel
 */
public class PublishDialog extends GerritOperationDialog {

	private static final String KEY_ID = "ApprovalCategoryValue.Id"; //$NON-NLS-1$

	private final GerritChange gerritChange;

	private final PatchSetPublishDetail publishDetail;

	private RichTextEditor messageEditor;

	private Composite approvalComposite;

	private final List<Button> approvalButtons;

	private final String editorCommentText;

	private final IReviewItemSet set;

	public PublishDialog(Shell parentShell, ITask task, GerritChange gerritChange, PatchSetPublishDetail publishDetail,
			IReviewItemSet set, String editorCommentText) {
		super(parentShell, task);
		this.gerritChange = gerritChange;
		this.publishDetail = publishDetail;
		this.set = set;
		this.editorCommentText = editorCommentText;
		approvalButtons = new ArrayList<>();
		setNeedsConfig(true);
	}

	@Override
	public GerritOperation<Object> createOperation() {
		int patchSetId = publishDetail.getPatchSetInfo().getKey().get();
		PublishRequest request = new PublishRequest(task.getTaskId(), patchSetId, getApprovals());
		request.setMessage(messageEditor.getText());
		return getOperationFactory().createOperation(task, request);
	}

	private Set<ApprovalCategoryValue.Id> getApprovals() {
		Set<ApprovalCategoryValue.Id> approvals = new HashSet<>();
		for (Button button : approvalButtons) {
			if (button.getSelection()) {
				approvals.add((ApprovalCategoryValue.Id) button.getData(KEY_ID));
			}
		}
		return approvals;
	}

	@Override
	protected Control createPageControls(Composite parent) {
		String changeId = publishDetail.getChange().getKey().abbreviate();

		setTitle(Messages.PublishDialog_Publish_Comments);
		setMessage(NLS.bind(Messages.PublishDialog_Change_X_dash_Y, changeId,
				publishDetail.getPatchSetInfo().getSubject()));

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		approvalComposite = new Composite(composite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(approvalComposite);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		approvalComposite.setLayout(layout);

		messageEditor = createRichTextEditor(composite, ""); //$NON-NLS-1$
		GridDataFactory.fillDefaults().grab(true, true).hint(600, 200).applyTo(messageEditor.getControl());
		messageEditor.setText(editorCommentText);
		messageEditor.getViewer().setSelectedRange(editorCommentText.length(), 0);
		messageEditor.getControl().setFocus();

		int drafts = 0;
		for (IComment comment : set.getAllComments()) {
			drafts += comment.isDraft() ? 1 : 0;
		}
		if (drafts > 0) {
			Label statusLabel = new Label(composite, SWT.NONE);
			statusLabel.setText(drafts > 1
					? NLS.bind(Messages.PublishDialog_Publishes_X_drafts, drafts)
					: Messages.PublishDialog_Publishes_1_draft);
		}

		return composite;
	}

	@Override
	protected void doRefresh(GerritConfig config) {
		Control[] children = approvalComposite.getChildren();
		for (Control child : children) {
			child.dispose();
		}
		approvalButtons.clear();

		if (config == null) {
			return;
		}

		Collection<ApprovalType> approvalTypes = getApprovalTypes(config);
		if (approvalTypes == null) {
			approvalTypes = gerritChange.getChangeDetail().getApprovalTypes();
		}

		for (ApprovalType approvalType : approvalTypes) {
			Set<ApprovalCategoryValue.Id> allowed = getAllowed(publishDetail, approvalType);
			if (allowed != null && allowed.size() > 0) {
				Group group = new Group(approvalComposite, SWT.NONE);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
				group.setText(approvalType.getCategory().getName());
				group.setLayout(new RowLayout(SWT.VERTICAL));

				int givenValue = 0;
				if (publishDetail.getGiven() != null) {
					// Gerrit 2.1
					PatchSetApproval approval = publishDetail.getGiven().get(approvalType.getCategory().getId());
					if (approval != null) {
						givenValue = approval.getValue();
					}
				}

				List<ApprovalCategoryValue.Id> allowedList = new ArrayList<>(allowed);
				Collections.sort(allowedList, (o1, o2) -> o2.get() - o1.get());
				for (ApprovalCategoryValue.Id valueId : allowedList) {
					ApprovalCategoryValue approvalValue = approvalType.getValue(valueId.get());

					Button button = new Button(group, SWT.RADIO);
					button.setText(approvalValue.format());
					if (approvalValue.getValue() == givenValue) {
						button.setSelection(true);
					}

					button.setData(KEY_ID, valueId);
					approvalButtons.add(button);
				}
			}
		}
	}

	private Collection<ApprovalType> getApprovalTypes(GerritConfig config) {
		if (config.getApprovalTypes() != null && config.getApprovalTypes().getApprovalTypes() != null) {
			return config.getApprovalTypes().getApprovalTypes();
		}
		return null;
	}

	private Set<ApprovalCategoryValue.Id> getAllowed(PatchSetPublishDetail publishDetail, ApprovalType approvalType) {
		if (publishDetail.getAllowed() != null) {
			// Gerrit 2.1
			return publishDetail.getAllowed(approvalType.getCategory().getId());
		} else if (publishDetail instanceof PatchSetPublishDetailX) {
			// Gerrit 2.2
			List<PermissionLabel> labels = ((PatchSetPublishDetailX) publishDetail).getLabels();
			if (labels != null) {
				Set<ApprovalCategoryValue.Id> result = new HashSet<>();
				for (PermissionLabel label : labels) {
					if (label.matches(approvalType.getCategory())) {
						for (ApprovalCategoryValue value : approvalType.getValues()) {
							if (label.matches(value)) {
								result.add(value.getId());
							}
						}
					}
				}
				return result;
			}
		}
		return null;
	}
}
