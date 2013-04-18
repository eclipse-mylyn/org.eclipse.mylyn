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

package org.eclipse.mylyn.reviews.ui.spi.editor;

import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.reviews.core.model.IApprovalType;
import org.eclipse.mylyn.reviews.core.model.IChange;
import org.eclipse.mylyn.reviews.core.model.IRequirementEntry;
import org.eclipse.mylyn.reviews.core.model.IReviewerEntry;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * Displays basic information about a given review corresponding to top sections of Gerrit web interface.
 * 
 * @author Steffen Pingel
 * @author Miles Parker
 */
public class ReviewDetailSection extends AbstractReviewSection {

	public ReviewDetailSection() {
		setPartName("Review");
	}

	@Override
	public void createModelContent() {
		createReviewersSubSection(composite);
		createRequirementsSubSection(toolkit, composite);
		createDependenciesSubSection(toolkit, composite, "Depends On", getReview().getParents());
		createDependenciesSubSection(toolkit, composite, "Needed By", getReview().getChildren());
	}

	protected void createReviewersSubSection(Composite parent) {
		if (getReview().getReviewerApprovals().isEmpty() && !canAddReviewers()) {
			return;
		}

		int style = ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT
				| ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT | ExpandableComposite.EXPANDED;

		final Section subSection = toolkit.createSection(parent, style);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(subSection);
		subSection.setTitleBarForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		subSection.setText("Reviewers");

		Composite composite = toolkit.createComposite(subSection);
		int numColumns = getModelRepository().getApprovalTypes().size() + 1;
		GridLayoutFactory.fillDefaults()
				.numColumns(numColumns)
				.extendedMargins(0, 0, 0, 5)
				.spacing(20, 5)
				.applyTo(composite);
		subSection.setClient(composite);

		if (!getReview().getReviewerApprovals().isEmpty()) {
			StringBuilder names = new StringBuilder();

			Label headerLabel = new Label(composite, SWT.NONE);
			headerLabel.setText(" "); //$NON-NLS-1$
			for (IApprovalType approvalType : getModelRepository().getApprovalTypes()) {
				Label approvalHeaderLabel = new Label(composite, SWT.NONE);
				approvalHeaderLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
				approvalHeaderLabel.setText(approvalType.getName());
			}

			for (Entry<IUser, IReviewerEntry> entry : getReview().getReviewerApprovals().entrySet()) {

				Label reviewerRowLabel = new Label(composite, SWT.NONE);
				reviewerRowLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
				reviewerRowLabel.setText(entry.getKey().getDisplayName());

				for (IApprovalType approvalType : getModelRepository().getApprovalTypes()) {
					Integer value = entry.getValue().getApprovals().get(approvalType);
					Label approvalValueLabel = new Label(composite, SWT.NONE);
					GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(approvalValueLabel);
					String rankingText = " ";
					if (value != null && value != 0) {
						if (value > 0) {
							rankingText += "+";
							approvalValueLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
						} else if (value < 0) {
							approvalValueLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
						}
						rankingText += value;
						approvalValueLabel.setToolTipText(value + "  " + approvalType.getName());
					}
					approvalValueLabel.setText(rankingText);
				}
				if (names.length() > 0) {
					names.append(", "); //$NON-NLS-1$
				}

				if (names.length() > 0) {
					names.append(", "); //$NON-NLS-1$
				}
				names.append(entry.getKey().getDisplayName());
			}

			if (names.length() > 0) {
				addTextClient(toolkit, subSection, names.toString());
			}
		}
	}

	protected boolean canAddReviewers() {
		return true;
	}

	protected void createRequirementsSubSection(final FormToolkit toolkit, final Composite parent) {
		if (getReview().getRequirements().isEmpty()) {
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

		for (Entry<IApprovalType, IRequirementEntry> requirement : getReview().getRequirements().entrySet()) {
			Label label1 = new Label(composite, SWT.NONE);
			IApprovalType key = requirement.getKey();
			label1.setText(key.getName());
			label1.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

			if (sb.length() > 0) {
				sb.append(", "); //$NON-NLS-1$
			}
			sb.append(key.getName());
		}

		addTextClient(toolkit, subSection, sb.toString());
	}

	protected void createDependenciesSubSection(final FormToolkit toolkit, final Composite parent, String title,
			List<IChange> changes) {
		if (changes.isEmpty()) {
			return;
		}

		int style = ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT;

		final Section subSection = toolkit.createSection(parent, style);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(subSection);
		subSection.setTitleBarForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		subSection.setText(title);

		Composite composite = toolkit.createComposite(subSection);
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 5).applyTo(composite);
		subSection.setClient(composite);

		for (final IChange change : changes) {
			Link link = new Link(composite, SWT.NONE);
			String changeStatus = change.getState() != null ? NLS.bind(" ({0})",
					String.valueOf(change.getState().getDescriptor())) : " ";
			String ownerName = change.getOwner().getDisplayName();
			link.setText(NLS.bind("<a>{0}</a>: {1} {3} by {2}", new String[] { StringUtils.left(change.getKey(), 9),
					change.getSubject(), ownerName, changeStatus }));
			link.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					TasksUiUtil.openTask(getTaskEditorPage().getTaskRepository(), change.getId() + ""); //$NON-NLS-1$
				}
			});
		}
	}

	@Override
	protected boolean shouldExpandOnCreate() {
		return true;
	}
}
