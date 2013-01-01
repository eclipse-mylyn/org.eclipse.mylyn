/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Jan Lohre (SAP) - improvements
 *     Sascha Scholz (SAP) - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.editor;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.internal.gerrit.core.GerritUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.ui.operations.AddReviewersDialog;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.google.gerrit.common.data.AccountInfo;
import com.google.gerrit.common.data.ApprovalDetail;
import com.google.gerrit.common.data.ApprovalType;
import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.ChangeInfo;
import com.google.gerrit.common.data.GerritConfig;
import com.google.gerrit.reviewdb.ApprovalCategory;
import com.google.gerrit.reviewdb.ApprovalCategoryValue;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.PatchSetApproval;

/**
 * @author Steffen Pingel
 */
public class ReviewSection extends AbstractGerritSection {

	private Composite composite;

	private FormToolkit toolkit;

	public ReviewSection() {
		setPartName("Review");
	}

	@Override
	protected Control createContent(FormToolkit toolkit, Composite parent) {
		this.toolkit = toolkit;

		composite = toolkit.createComposite(parent);
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 5).applyTo(composite);

		GerritChange review = GerritUtil.getChange(getTaskData());
		if (review != null) {
			createReviewContent(toolkit, composite, review.getChangeDetail());
		}
		return composite;
	}

	private void createReviewContent(FormToolkit toolkit, Composite composite, ChangeDetail changeDetail) {
		GerritConfig config = getConfig();

		createPeopleSubSection(composite, changeDetail, config);

		if (changeDetail.getMissingApprovals() != null && changeDetail.getMissingApprovals().size() > 0) {
			createRequirementsSubSection(toolkit, composite, config, changeDetail);
		}

		if (changeDetail.getDependsOn() != null && changeDetail.getDependsOn().size() > 0) {
			createDependenciesSubSection(toolkit, composite, "Depends On", changeDetail, changeDetail.getDependsOn());
		}
		if (changeDetail.getNeededBy() != null && changeDetail.getNeededBy().size() > 0) {
			createDependenciesSubSection(toolkit, composite, "Needed By", changeDetail, changeDetail.getNeededBy());
		}
	}

	void createPeopleSubSection(Composite parent, ChangeDetail changeDetail, GerritConfig config) {
		if (changeDetail.getApprovals().isEmpty() && !canAddReviewers(changeDetail)) {
			return;
		}

		int style = ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT
				| ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT;

		final Section subSection = toolkit.createSection(parent, style);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(subSection);
		subSection.setTitleBarForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		subSection.setText("Reviewers");

		List<ApprovalType> approvalTypes;
		if (config != null && config.getApprovalTypes() != null && config.getApprovalTypes().getApprovalTypes() != null) {
			approvalTypes = config.getApprovalTypes().getApprovalTypes();
		} else {
			approvalTypes = Collections.emptyList();
		}

		Composite composite = toolkit.createComposite(subSection);
		int numColumns = approvalTypes.size() + 1;
		GridLayoutFactory.fillDefaults()
				.numColumns(numColumns)
				.extendedMargins(0, 0, 0, 5)
				.spacing(20, 5)
				.applyTo(composite);
		subSection.setClient(composite);

		if (changeDetail.getApprovals().size() > 0) {
			StringBuilder names = new StringBuilder();

			// column headers
			Label label = new Label(composite, SWT.NONE);
			label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
			label.setText(" "); //$NON-NLS-1$
			for (ApprovalType approvalType : approvalTypes) {
				label = new Label(composite, SWT.NONE);
				label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
				label.setText(approvalType.getCategory().getName());
			}

			for (ApprovalDetail approvalDetail : changeDetail.getApprovals()) {
				AccountInfo user = changeDetail.getAccounts().get(approvalDetail.getAccount());

				label = new Label(composite, SWT.NONE);
				label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
				label.setText(GerritUtil.getUserLabel(user));

				for (ApprovalType approvalType : approvalTypes) {
					PatchSetApproval approval = approvalDetail.getApprovalMap().get(approvalType.getCategory().getId());
					if (approval != null && approval.getValue() != 0) {
						label = new Label(composite, SWT.NONE);
						GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(label);

						ApprovalCategoryValue approvalValue = approvalType.getValue(approval.getValue());
						if (approvalValue != null) {
							label.setText(approvalValue.formatValue());
							label.setToolTipText(approvalValue.format());
						} else {
							label.setText(approval.getValue() + ""); //$NON-NLS-1$
						}
					} else {
						label = new Label(composite, SWT.NONE);
						label.setText(" "); //$NON-NLS-1$
					}
				}

				if (names.length() > 0) {
					names.append(", "); //$NON-NLS-1$
				}
				names.append(GerritUtil.getUserLabel(user));
			}

			if (names.length() > 0) {
				addTextClient(toolkit, subSection, names.toString());
			}
		}

		Composite buttonComposite = new Composite(composite, SWT.NONE);
		GridDataFactory.fillDefaults().span(numColumns, 1).applyTo(buttonComposite);
		RowLayout layout = new RowLayout();
		layout.center = true;
		layout.spacing = 10;
		buttonComposite.setLayout(layout);
		Button addReviewersButton = toolkit.createButton(buttonComposite, "Add Reviewers...", SWT.PUSH);
		addReviewersButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doAddReviewers();
			}
		});
	}

	private void doAddReviewers() {
		AddReviewersDialog dialog = new AddReviewersDialog(getShell(), getTask());
		openOperationDialog(dialog);
	}

	private boolean canAddReviewers(ChangeDetail changeDetail) {
		return changeDetail.getChange().getStatus() == Change.Status.NEW
				|| GerritUtil.isDraft(changeDetail.getChange().getStatus());
	}

	private void createRequirementsSubSection(final FormToolkit toolkit, final Composite parent, GerritConfig config,
			ChangeDetail changeDetail) {
		if (config == null) {
			return;
		}

		int style = ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT
				| ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT;

		final Section subSection = toolkit.createSection(parent, style);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(subSection);
		subSection.setTitleBarForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		subSection.setText("Requirements");

		Composite composite = toolkit.createComposite(subSection);
		GridLayoutFactory.fillDefaults().numColumns(2).spacing(20, 5).extendedMargins(0, 0, 0, 5).applyTo(composite);
		subSection.setClient(composite);

		StringBuilder sb = new StringBuilder();

		for (ApprovalCategory.Id approvalCategoryId : changeDetail.getMissingApprovals()) {
			ApprovalType type = config.getApprovalTypes().getApprovalType(approvalCategoryId);
			if (type != null) {
				ApprovalCategoryValue approvalValue = type.getMax();
				if (approvalValue != null) {
					Label label1 = new Label(composite, SWT.NONE);
					label1.setText(type.getCategory().getName());
					label1.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

					Label label2 = new Label(composite, SWT.NONE);
					label2.setText(approvalValue.format());
					//label2.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

					if (sb.length() > 0) {
						sb.append(", "); //$NON-NLS-1$
					}
					sb.append(type.getCategory().getName());
				}
			}
		}

		addTextClient(toolkit, subSection, sb.toString());
	}

	private void createDependenciesSubSection(final FormToolkit toolkit, final Composite parent, String title,
			ChangeDetail changeDetail, List<ChangeInfo> changeInfos) {
		int style = ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT;

		final Section subSection = toolkit.createSection(parent, style);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(subSection);
		subSection.setTitleBarForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		subSection.setText(title);

		Composite composite = toolkit.createComposite(subSection);
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 5).applyTo(composite);
		subSection.setClient(composite);

		for (final ChangeInfo changeInfo : changeInfos) {
			AccountInfo user = changeDetail.getAccounts().get(changeInfo.getOwner());
			Link link = new Link(composite, SWT.NONE);
			link.setText(NLS.bind("<a>{0}</a>: {1} ({3}) by {2}", new String[] { changeInfo.getKey().abbreviate(),
					changeInfo.getSubject(), GerritUtil.getUserLabel(user), String.valueOf(changeInfo.getStatus()) }));
			link.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					TasksUiUtil.openTask(getTaskEditorPage().getTaskRepository(), changeInfo.getId() + ""); //$NON-NLS-1$
				}
			});
		}
	}

	@Override
	protected boolean shouldExpandOnCreate() {
		return true;
	}

}
