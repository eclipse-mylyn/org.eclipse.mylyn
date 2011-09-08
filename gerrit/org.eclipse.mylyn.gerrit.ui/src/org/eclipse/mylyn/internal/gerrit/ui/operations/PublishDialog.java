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

package org.eclipse.mylyn.internal.gerrit.ui.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.gerrit.core.operations.GerritOperation;
import org.eclipse.mylyn.internal.gerrit.core.operations.PublishRequest;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextEditor;
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

	private final PatchSetPublishDetail publishDetail;

	private RichTextEditor messageEditor;

	private Composite approvalComposite;

	private final List<Button> approvalButtons;

	private Label statusLabel;

	private final int addedDrafts;

	public PublishDialog(Shell parentShell, ITask task, PatchSetPublishDetail patchSet, int addedDrafts) {
		super(parentShell, task);
		this.publishDetail = patchSet;
		this.addedDrafts = addedDrafts;
		this.approvalButtons = new ArrayList<Button>();
		setNeedsConfig(true);
	}

	@Override
	public GerritOperation<Object> createOperation() {
		int patchSetId = publishDetail.getPatchSetInfo().getKey().get();
		PublishRequest request = new PublishRequest(task.getTaskId(), patchSetId, getApprovals());
		request.setMessage(messageEditor.getText());
		return getOperationFactory().createPublishOperation(task, request);
	}

	private Set<ApprovalCategoryValue.Id> getApprovals() {
		Set<ApprovalCategoryValue.Id> approvals = new HashSet<ApprovalCategoryValue.Id>();
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

		setTitle("Publish Comments");
		setMessage(NLS.bind("Change {0} - {1}", changeId, publishDetail.getPatchSetInfo().getSubject()));

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		approvalComposite = new Composite(composite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(approvalComposite);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		approvalComposite.setLayout(layout);

		messageEditor = createRichTextEditor(composite, "");
		GridDataFactory.fillDefaults().grab(true, true).minSize(400, 150).applyTo(messageEditor.getControl());
		messageEditor.getControl().setFocus();

		statusLabel = new Label(composite, SWT.NONE);
		statusLabel.setText(NLS.bind("Publishes {0} draft(s).", publishDetail.getDrafts().size() + addedDrafts));

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

		if (config.getApprovalTypes() != null && publishDetail.getAllowed() != null) {
			for (ApprovalType approvalType : config.getApprovalTypes().getApprovalTypes()) {
				Set<ApprovalCategoryValue.Id> allowed = publishDetail.getAllowed(approvalType.getCategory().getId());
				if (allowed != null && allowed.size() > 0) {
					Group group = new Group(approvalComposite, SWT.NONE);
					GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
					group.setText(approvalType.getCategory().getName());
					group.setLayout(new RowLayout(SWT.VERTICAL));

					int givenValue = 0;
					if (publishDetail.getGiven() != null) {
						PatchSetApproval approval = publishDetail.getGiven().get(approvalType.getCategory().getId());
						if (approval != null) {
							givenValue = approval.getValue();
						}
					}

					List<ApprovalCategoryValue.Id> allowedList = new ArrayList<ApprovalCategoryValue.Id>(allowed);
					Collections.sort(allowedList, new Comparator<ApprovalCategoryValue.Id>() {
						public int compare(ApprovalCategoryValue.Id o1, ApprovalCategoryValue.Id o2) {
							return o2.get() - o1.get();
						}
					});
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
	}

}
