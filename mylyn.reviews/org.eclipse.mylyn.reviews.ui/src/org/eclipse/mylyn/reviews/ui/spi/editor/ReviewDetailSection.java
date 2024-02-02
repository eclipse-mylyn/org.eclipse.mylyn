/*******************************************************************************
 * Copyright (c) 2010, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Jan Lohre (SAP) - improvements
 *     Sascha Scholz (SAP) - improvements
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.editor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsImages;
import org.eclipse.mylyn.reviews.core.model.IApprovalType;
import org.eclipse.mylyn.reviews.core.model.IChange;
import org.eclipse.mylyn.reviews.core.model.IRequirementEntry;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewerEntry;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.model.RequirementStatus;
import org.eclipse.mylyn.reviews.ui.spi.factories.AbstractUiFactoryProvider;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
public abstract class ReviewDetailSection extends AbstractReviewSection {

	public ReviewDetailSection() {
		setPartName(Messages.ReviewDetailSection_Review);
	}

	@Override
	protected Control createContent(FormToolkit toolkit, Composite parent) {
		Control content = super.createContent(toolkit, parent);
		createReviewersSubSection(composite);
		createDependenciesSubSection(toolkit, composite, Messages.ReviewDetailSection_Depends_On,
				getReview().getParents());
		createDependenciesSubSection(toolkit, composite, Messages.ReviewDetailSection_Needed_By,
				getReview().getChildren());
		return content;
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
		subSection.setText(Messages.ReviewDetailSection_Reviewers);

		Composite composite = toolkit.createComposite(subSection);
		List<IApprovalType> approvalTypes = getModelRepository().getApprovalTypes();
		List<IApprovalType> approvalTypesWithLabel = new ArrayList<>(approvalTypes.size());
		for (IApprovalType approvalType : approvalTypes) {
			if (!approvalType.getName().equals(approvalType.getKey())) {
				approvalTypesWithLabel.add(approvalType);
			}
		}

		int numColumns = approvalTypesWithLabel.size() + 1;
		GridLayoutFactory.fillDefaults()
				.numColumns(numColumns)
				.extendedMargins(0, 0, 0, 5)
				.equalWidth(true)
				.spacing(4, 5)
				.applyTo(composite);
		subSection.setClient(composite);

		if (!approvalTypesWithLabel.isEmpty()) {
			StringBuilder names = new StringBuilder();

			Label headerLabel = new Label(composite, SWT.NONE);
			headerLabel.setText(" "); //$NON-NLS-1$
			StringBuilder needs = new StringBuilder();

			for (IApprovalType approvalType : approvalTypesWithLabel) {
				IRequirementEntry requirementEntry = getReview().getRequirements().get(approvalType);
				Composite headerContainer = new Composite(composite, SWT.NONE);
				headerContainer.setForeground(toolkit.getColors().getColor(IFormColors.TB_BG));
				GridLayoutFactory.fillDefaults().numColumns(2).applyTo(headerContainer);
				GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).applyTo(headerContainer);
				CLabel approvalHeaderLabel = new CLabel(headerContainer, SWT.NONE);
				approvalHeaderLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
				approvalHeaderLabel.setText(approvalType.getName());
				GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(approvalHeaderLabel);
				RequirementStatus status = null;
				if (requirementEntry != null) {
					status = requirementEntry.getStatus();
					switch (status) {
						case SATISFIED:
							approvalHeaderLabel.setImage(CommonImages.getImage(ReviewsImages.APPROVED));
							break;
						case NOT_SATISFIED:
							approvalHeaderLabel.setImage(CommonImages.getImage(ReviewsImages.UNKNOWN));
							break;
						case REJECTED:
							approvalHeaderLabel.setImage(CommonImages.getImage(ReviewsImages.REJECTED));
							break;
						default:
							//To ensure that label is aligned properly
							approvalHeaderLabel.setImage(CommonImages.getImage(ReviewsImages.BLANK));
							break;
					}
				}
				if (status != null && (status == RequirementStatus.UNKNOWN || status == RequirementStatus.REJECTED)) {
					if (needs.length() > 0) {
						needs.append(", "); //$NON-NLS-1$
					}
					needs.append(approvalType.getName());
				}
			}

			AbstractUiFactoryProvider<IUser> reviewerUiFactoryProvider = getReviewerUiFactoryProvider();

			Map<IUser, IReviewerEntry> sortedReviewerApprovals = new TreeMap<>(
					Comparator.comparing(IUser::getDisplayName));
			sortedReviewerApprovals.putAll(getReview().getReviewerApprovals());

			for (Entry<IUser, IReviewerEntry> approval : sortedReviewerApprovals.entrySet()) {
				IUser currentUser = approval.getKey();

				createReviewerLabelAndControls(composite, reviewerUiFactoryProvider, currentUser);

				for (IApprovalType approvalType : approvalTypesWithLabel) {
					Integer value = approval.getValue().getApprovals().get(approvalType);
					Label approvalValueLabel = new Label(composite, SWT.NONE);
					GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).applyTo(approvalValueLabel);
					String rankingText = " "; //$NON-NLS-1$
					if (value != null && value != 0) {
						if (value > 0) {
							rankingText += "+"; //$NON-NLS-1$
							approvalValueLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
						} else if (value < 0) {
							approvalValueLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
						}
						rankingText += value;
						approvalValueLabel.setToolTipText(value + "  " + approvalType.getName()); //$NON-NLS-1$
					}
					approvalValueLabel.setText(rankingText);
				}
				if (names.length() > 0) {
					names.append(", "); //$NON-NLS-1$
				}
				names.append(currentUser.getDisplayName());
			}

			String headerText = names.toString();
			if (needs.length() > 0) {
				headerText += NLS.bind(Messages.ReviewDetailSection_Needs_X, needs);
			}
			if (headerText.length() > 0) {
				addTextClient(toolkit, subSection, headerText);
			}
		}
		if (getUiFactoryProvider() != null) {
			Composite actionComposite = getUiFactoryProvider().createControls(this, composite, getToolkit(),
					getReview());
			GridDataFactory.fillDefaults().span(2, 1).applyTo(actionComposite);
		}

	}

	private void createReviewerLabelAndControls(Composite parent,
			AbstractUiFactoryProvider<IUser> reviewerUiFactoryProvider, IUser user) {
		Composite reviewerComp = toolkit.createComposite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(reviewerComp);

		if (reviewerUiFactoryProvider != null) {
			createReviewerControls(reviewerComp, user, reviewerUiFactoryProvider);
		}
		createReviewerLabel(reviewerComp, user);
	}

	private void createReviewerLabel(Composite reviewerComposite, IUser user) {
		Label reviewerRowLabel = new Label(reviewerComposite, SWT.NONE);
		reviewerRowLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		reviewerRowLabel.setText(user.getDisplayName());
	}

	private void createReviewerControls(Composite reviewerComposite, IUser user,
			AbstractUiFactoryProvider<IUser> reviewerUiFactoryProvider) {
		Composite controlComposite = reviewerUiFactoryProvider.createControls(this, reviewerComposite, getToolkit(),
				user);
		GridDataFactory.fillDefaults().applyTo(controlComposite);
	}

	protected boolean canAddReviewers() {
		return true;
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
			String changeStatus = change.getState() != null
					? NLS.bind(Messages.ReviewDetailSection_Bracket_X_bracket,
							String.valueOf(change.getState().getName()))
					: " "; //$NON-NLS-1$
			String ownerName = change.getOwner().getDisplayName();
			link.setText(NLS.bind(Messages.ReviewDetailSection_Link_W_X_Y_by_Z, new String[] {
					StringUtils.left(change.getKey(), 9), change.getSubject(), changeStatus, ownerName }));
			link.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					TasksUiUtil.openTask(getTaskEditorPage().getTaskRepository(), change.getId());
				}
			});
		}
	}

	protected abstract AbstractUiFactoryProvider<IReview> getUiFactoryProvider();

	protected AbstractUiFactoryProvider<IUser> getReviewerUiFactoryProvider() {
		return null;
	}

	@Override
	protected boolean shouldExpandOnCreate() {
		return true;
	}
}
