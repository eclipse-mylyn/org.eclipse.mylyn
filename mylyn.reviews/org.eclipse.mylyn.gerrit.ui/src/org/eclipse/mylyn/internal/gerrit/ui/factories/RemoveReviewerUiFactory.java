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

package org.eclipse.mylyn.internal.gerrit.ui.factories;

import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.gerrit.ui.operations.RemoveReviewerDialog;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.model.ReviewStatus;
import org.eclipse.mylyn.reviews.ui.spi.editor.ReviewDetailSection;
import org.eclipse.mylyn.reviews.ui.spi.factories.AbstractUiFactory;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

public class RemoveReviewerUiFactory extends AbstractUiFactory<IUser> {

	public RemoveReviewerUiFactory(IUiContext context, IUser object) {
		super(Messages.RemoveReviewerUiFactory_Remove_Reviewer, context, object);
	}

	@Override
	public Control createControl(IUiContext context, Composite parent, FormToolkit toolkit) {
		ImageHyperlink removeLink = toolkit.createImageHyperlink(parent, SWT.TOP);
		removeLink.setToolTipText(
				NLS.bind(Messages.RemoveReviewerUiFactory_Remove_Reviewer_Name, getModelObject().getDisplayName()));
		removeLink.setImage(CommonImages.getImage(CommonImages.REMOVE));
		removeLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				execute();
			}
		});
		removeLink.setEnabled(!isExecutableStateKnown() || isExecutable());
		return removeLink;
	}

	@Override
	protected boolean isExecutableStateKnown() {
		return true;
	}

	@Override
	public boolean isExecutable() {
		ReviewStatus currentStatus = getReview().getState();
		return getModelObject() != null && getModelObject().getId() != null && currentStatus != ReviewStatus.ABANDONED
				&& currentStatus != ReviewStatus.MERGED;
	}

	@Override
	public void execute() {
		new RemoveReviewerDialog(getShell(), getTask(), getModelObject()).open(getEditor());
	}

	private IReview getReview() {
		return ((ReviewDetailSection) getContext()).getReview();
	}

}
