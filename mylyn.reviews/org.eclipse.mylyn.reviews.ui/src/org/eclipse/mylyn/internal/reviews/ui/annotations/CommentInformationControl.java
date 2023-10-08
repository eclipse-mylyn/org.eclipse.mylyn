/*******************************************************************************
 * Copyright (c) 2009, 2016 Atlassian and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Atlassian - initial API and implementation
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.annotations;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.internal.text.html.HTMLTextPresenter;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

/**
 * A custom control to display on hover, or delegates to the default control to display if we aren't dealing with a
 * CrucibleCommentAnnotation.
 *
 * @author sminto
 */
public class CommentInformationControl extends DefaultInformationControl implements IInformationControlExtension2 {

	private Object input;

	private CommentPopupDialog commentPopupDialog;

	private final CommentInformationControlCreator informationControlCreator;

	private Job markAsReadJob;

	@SuppressWarnings("restriction")
	public CommentInformationControl(Shell parent, CommentInformationControlCreator crucibleInformationControlCreator) {
		super(parent, new HTMLTextPresenter(true));
		this.informationControlCreator = crucibleInformationControlCreator;
		commentPopupDialog = new CommentPopupDialog(parent, SWT.NO_FOCUS | SWT.ON_TOP, null, null, false);
		// Force create early so that listeners can be added at all times with API.
		commentPopupDialog.create();
		commentPopupDialog.setInformationControl(this);
	}

	public InformationPresenter getInformationPresenter() {
		return new InformationPresenter(informationControlCreator);
	}

	@Override
	public void setInformation(String content) {
		this.input = content;
		commentPopupDialog.setInput(input);
		super.setInformation(content);
	}

	public void setInput(Object input) {
		this.input = input;
		commentPopupDialog.setInput(input);
	}

	@Override
	public boolean hasContents() {
		return input != null || super.hasContents();
	}

	private void runMarkCommentAsReadJob(CommentAnnotationHoverInput input) {
		List<CommentAnnotation> annotations = input.getAnnotations();
		if (annotations == null || annotations.size() == 0) {
			return;
		}

		Set<IComment> comments = new HashSet<IComment>();
//		for (CommentAnnotation annotation : Iterables.filter(annotations, CommentAnnotation.class)) {
		for (CommentAnnotation annotation : annotations.stream()
				.filter(CommentAnnotation.class::isInstance)
				.collect(Collectors.toList())) {
			comments.addAll(getUnreadComments(annotation.getComment()));
		}

		if (comments.size() > 0) {
//			markAsReadJob = new MarkCommentsReadJob(comments.iterator().next().getReview(), comments, true);
//			markAsReadJob.schedule(MarkCommentsReadJob.DEFAULT_DELAY_INTERVAL);
		}
	}

	private Collection<? extends IComment> getUnreadComments(IComment comment) {
		// FIXME
		Set<IComment> result = new HashSet<IComment>();
//		if (comment.getReadState().equals(ReadState.UNREAD)) {
//			result.add(comment);
//		}
//		for (IComment reply : comment.getReplies()) {
//			result.addAll(getUnreadComments(reply));
//		}
		return result;
	}

	private void cancelMarkCommentAsReadJob() {
		if (markAsReadJob != null) {
			markAsReadJob.cancel();
			markAsReadJob = null;
		}
	}

	@Override
	public void setVisible(boolean visible) {
		cancelMarkCommentAsReadJob();
		if (input instanceof String) {
			setInformation((String) input);
			super.setVisible(visible);
		} else if (input instanceof CommentAnnotationHoverInput) {
			if (visible) {
				commentPopupDialog.open();
				runMarkCommentAsReadJob((CommentAnnotationHoverInput) input);
			} else if (commentPopupDialog.hasEdits()) {
				return;
			} else {
				commentPopupDialog.getShell().setVisible(false);
			}
		} else {
			super.setVisible(visible);
		}
	}

	@Override
	public void dispose() {
		if (!commentPopupDialog.hasEdits()) {
			cancelMarkCommentAsReadJob();

			commentPopupDialog.dispose(false);
			commentPopupDialog = null;
		}
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		commentPopupDialog.setSize(width, height);
	}

	@Override
	public void setLocation(Point location) {
		super.setLocation(location);
		commentPopupDialog.setLocation(location);
	}

	@Override
	public void setSizeConstraints(int maxWidth, int maxHeight) {
		super.setSizeConstraints(maxWidth, maxHeight);
		commentPopupDialog.setSizeConstraints(maxWidth, maxHeight);
	}

	@Override
	public Rectangle computeTrim() {
		if (input instanceof String) {
			return super.computeTrim();
		} else if (input instanceof CommentAnnotationHoverInput) {
			return commentPopupDialog.computeTrim();
		} else {
			return super.computeTrim();
		}

	}

	@Override
	public Rectangle getBounds() {
		if (input instanceof String) {
			return super.getBounds();
		} else if (input instanceof CommentAnnotationHoverInput) {
			return commentPopupDialog.getBounds();
		} else {
			return super.getBounds();
		}
	}

	@Override
	public void addDisposeListener(DisposeListener listener) {
		super.addDisposeListener(listener);
		if (commentPopupDialog != null) {
			commentPopupDialog.addDisposeListener(listener);
		}
	}

	@Override
	public void removeDisposeListener(DisposeListener listener) {
		super.removeDisposeListener(listener);
		commentPopupDialog.removeDisposeListener(listener);
	}

	@Override
	public void setForegroundColor(Color foreground) {
		super.setForegroundColor(foreground);
	}

	@Override
	public void setBackgroundColor(Color background) {
		super.setBackgroundColor(background);
	}

	@Override
	public boolean isFocusControl() {
		if (input instanceof String) {
			return super.isFocusControl();
		} else if (input instanceof CommentAnnotationHoverInput) {
			return commentPopupDialog.isFocusControl();
		} else {
			return super.isFocusControl();
		}

	}

	@Override
	public void setFocus() {
		if (input instanceof String) {
			super.setFocus();
		} else if (input instanceof CommentAnnotationHoverInput) {
			commentPopupDialog.setFocus();
		} else {
			super.setFocus();
		}
	}

	@Override
	public void addFocusListener(FocusListener listener) {
		super.addFocusListener(listener);
		commentPopupDialog.addFocusListener(listener);
	}

	@Override
	public void removeFocusListener(FocusListener listener) {
		super.removeFocusListener(listener);
		commentPopupDialog.removeFocusListener(listener);
	}

	@Override
	public Point computeSizeHint() {
		if (input instanceof String) {
			setInformation((String) input);
			return super.computeSizeHint();
		} else if (input instanceof CommentAnnotationHoverInput) {
			return commentPopupDialog.computeSizeHint();
		} else {
			return super.computeSizeHint();
		}
	}

	@Override
	public IInformationControlCreator getInformationPresenterControlCreator() {
		return new CommentInformationControlCreator();
	}

}
