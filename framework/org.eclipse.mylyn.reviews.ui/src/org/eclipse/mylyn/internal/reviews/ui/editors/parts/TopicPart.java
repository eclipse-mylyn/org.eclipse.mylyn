/*******************************************************************************
 * Copyright (c) 2009 Atlassian and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlassian - initial API and implementation
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.editors.parts;

import org.eclipse.mylyn.internal.reviews.ui.IReviewActionListener;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * A UI part to represent a general comment in a review
 * 
 * @author Shawn Minto
 */
public class TopicPart {

	private Composite composite;

	private IReview review;

	private final ITopic topic;

	private IReviewActionListener actionListener;

	public TopicPart(ITopic topic) {
		this.topic = topic;
	}

	public void hookCustomActionRunListener(IReviewActionListener actionRunListener) {
		this.actionListener = actionRunListener;
	}

	public IReviewActionListener getActionListener() {
		return actionListener;
	}

	public Control createControl(Composite parent, FormToolkit toolkit) {
		composite = toolkit.createComposite(parent);
		composite.setLayout(new GridLayout());
		for (IComment comment : topic.getComments()) {
			CommentPart part = new CommentPart(comment);
			part.createControl(composite, toolkit);
		}
		return composite;
	}

}
