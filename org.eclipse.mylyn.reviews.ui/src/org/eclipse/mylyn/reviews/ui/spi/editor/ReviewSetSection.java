/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies, Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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

/**
 * @author Steffen Pingel
 * @author Sascha Scholz
 * @author Miles Parker
 */
public abstract class ReviewSetSection extends AbstractReviewSection {

	protected ReviewsLabelProvider labelProvider;

	private List<ReviewSetContentSection> reviewSetSections;

// XXX drafts added after the publish detail was refreshed from server
	protected int addedDrafts;

	public ReviewSetSection() {
		setPartName("Patch Sets");
	}

	@Override
	public void initialize(AbstractTaskEditorPage taskEditorPage) {
		reviewSetSections = new ArrayList<ReviewSetContentSection>();
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
	public void createModelContent() {
		for (IReviewItem item : getReview().getSets()) {
			if (item instanceof IReviewItemSet) {
				IReviewItemSet set = (IReviewItemSet) item;
				ReviewSetContentSection subSection = new ReviewSetContentSection(this, set);
				reviewSetSections.add(subSection);
			}
		}
		revealPatchSet(reviewSetSections.size());
		getTaskEditorPage().reflow();
	}

	@Override
	protected void updateModelContent() {
		for (ReviewSetContentSection section : reviewSetSections) {
			section.updateReview();
		}
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
