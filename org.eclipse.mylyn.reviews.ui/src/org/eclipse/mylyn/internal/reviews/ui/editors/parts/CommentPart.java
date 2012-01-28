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

import java.text.DateFormat;

import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class CommentPart extends AbstractCommentPart<CommentPart> {

	private Composite composite;

	public CommentPart(IComment comment) {
		super(comment);
	}

	@Override
	protected String getSectionHeaderText() {
		String headerText = comment.getAuthor().getDisplayName() + "   ";
		headerText += DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(
				comment.getCreationDate());
		return headerText;
	}

	// TODO could be moved to a util method
	private String getCommentText() {
		String commentText = comment.getDescription();

		String customFieldsString = "";
		if (customFieldsString.length() > 0) {
			commentText += "  " + customFieldsString;
		}
		return commentText;
	}

	@Override
	protected String getAnnotationText() {
		String text = "";
		if (comment.isDraft()) {
			text = "DRAFT ";
		}
		return text;
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
		return new CommentPart(comment);
	}

	@Override
	protected Composite createSectionContents(Section section, FormToolkit toolkit) {
		composite = super.createSectionContents(section, toolkit);

		updateChildren(composite, toolkit, false, comment.getReplies());
		return composite;
	}

}
