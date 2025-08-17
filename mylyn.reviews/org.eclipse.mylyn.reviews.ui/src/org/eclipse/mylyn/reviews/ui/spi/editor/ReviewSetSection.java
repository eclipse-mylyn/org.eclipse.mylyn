/*******************************************************************************
 * Copyright (c) 2013, 2015 Tasktop Technologies, Ericsson and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Sascha Scholz (SAP) - improvements
 *     Sam Davis - improvements for bug 383592
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.internal.reviews.ui.providers.ReviewsLabelProvider;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.ui.spi.factories.AbstractReviewItemSetUiFactoryProvider;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 * @author Sascha Scholz
 * @author Miles Parker
 */
public abstract class ReviewSetSection extends AbstractReviewSection {

	protected ReviewsLabelProvider labelProvider;

	private List<ReviewSetContentSection> reviewSetSections;

	public ReviewSetSection() {
		setPartName(Messages.ReviewSetSection_Patch_Sets);
	}

	@Override
	public void initialize(AbstractTaskEditorPage taskEditorPage) {
		reviewSetSections = new ArrayList<>();
		super.initialize(taskEditorPage);
	}

	public void revealPatchSet(int patchSetNumber) {
		if (patchSetNumber > 0 && patchSetNumber <= reviewSetSections.size()) {
			int index = patchSetNumber - 1;
			ReviewSetContentSection patchSetSection = reviewSetSections.get(index);
			if (!patchSetSection.getSection().isExpanded()) {
				CommonFormUtil.setExpanded(patchSetSection.getSection(), true);
			}
			CommonFormUtil.ensureVisible(patchSetSection.getSection());
		}
	}

	@Override
	protected Control createContent(FormToolkit toolkit, Composite parent) {
		Control content = super.createContent(toolkit, parent);
		for (IReviewItem item : getReview().getSets()) {
			if (item instanceof IReviewItemSet set) {
				ReviewSetContentSection subSection = new ReviewSetContentSection(this, set, getTaskEditorPage());
				reviewSetSections.add(subSection);
			}
		}
		revealPatchSet(reviewSetSections.size());
		getTaskEditorPage().reflow();
		return content;
	}

	@Override
	protected boolean shouldExpandOnCreate() {
		return true;
	}

	protected abstract AbstractReviewItemSetUiFactoryProvider getUiFactoryProvider();

	@Override
	public TaskRepository getTaskRepository() {
		return getEditor().getTaskEditorInput().getTaskRepository();
	}

	@Override
	public void dispose() {
		super.dispose();
		if (labelProvider != null) {
			labelProvider.dispose();
		}
		for (ReviewSetContentSection section : reviewSetSections) {
			section.dispose();
		}
	}
}
