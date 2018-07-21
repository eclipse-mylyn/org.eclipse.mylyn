/*******************************************************************************
 * Copyright (c) 2009, 2013 Atlassian and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Atlassian - initial API and implementation
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.editors.parts;

import org.eclipse.mylyn.internal.reviews.ui.IReviewActionListener;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class CommentPart extends AbstractCommentPart<CommentPart> {

	private Composite composite;

	private IReviewActionListener actionListener;

	public CommentPart(IComment comment, ReviewBehavior behavior) {
		super(comment, behavior);
	}

	@Override
	public void hookCustomActionRunListener(IReviewActionListener actionRunListener) {
		this.actionListener = actionRunListener;
	}

	@Override
	public IReviewActionListener getActionListener() {
		return actionListener;
	}

	@Override
	protected boolean represents(IComment comment) {
		return this.comment.getId().equals(comment.getId());
	}

	@Override
	protected Control update(Composite parentComposite, FormToolkit toolkit, IComment newComment) {
		this.comment = newComment;
		Control createControl = createOrUpdateControl(parentComposite, toolkit);
		return createControl;
	}

	@Override
	protected CommentPart createChildPart(IComment comment) {
		return new CommentPart(comment, getBehavior());
	}

	@Override
	protected Composite createSectionContents(Section section, FormToolkit toolkit) {
		composite = super.createSectionContents(section, toolkit);

		updateChildren(composite, toolkit, false, comment.getReplies());
		return composite;
	}

}
