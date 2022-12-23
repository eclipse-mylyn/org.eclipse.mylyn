/*******************************************************************************
 * Copyright (c) 2014, 2015 Ericsson AB and others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   Guy Perron - original version
 *
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.annotations;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.source.LineRange;
import org.eclipse.mylyn.internal.reviews.ui.annotations.InlineCommentEditor.CommentEditorState;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILineRange;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.spi.ReviewsConnector;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.review.IReviewRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.widgets.Display;

public class InlineCommentSubmitter {

	public static final String JOB_FAMILY = "CommentPopupDialogJobFamily"; // For Testing purposes //$NON-NLS-1$

	private final IReviewItem reviewitem;

	private final LineRange range;

	private final CommentAnnotationHoverInput annotationInput;

	private final InlineCommentEditor commentEditor;

	/**
	 * Creates a comment submitter
	 * 
	 * @param reviewitem
	 *            the submitted comment's review item
	 * @param range
	 *            the submitted comment's line range
	 * @param annotationInput
	 *            the {@link CommentAnnotationHoverInput} associated with the dialog
	 * @param commentEditor
	 *            the comment editor that this submitter is dependent on
	 */
	public InlineCommentSubmitter(IReviewItem reviewitem, LineRange range, CommentAnnotationHoverInput annotationInput,
			InlineCommentEditor commentEditor) {
		this.reviewitem = reviewitem;
		this.range = range;
		this.annotationInput = annotationInput;
		this.commentEditor = commentEditor;
	}

	/**
	 * Creates a new comment, sets the comment's parameters based on the editor's current comment and text, and submits
	 * the comment
	 */
	public void saveComment() {
		if (!commentEditor.getState().equals(CommentEditorState.VIEW)
				&& (commentEditor.getState().equals(CommentEditorState.DISCARD)
						|| StringUtils.isNotEmpty(commentEditor.getCommentEditorText().getText().trim()))) {
			IComment comment = IReviewsFactory.INSTANCE.createComment();
			comment.setDescription(commentEditor.getCommentEditorText().getText().trim());

			if (commentEditor.getCurrentComment().isDraft()) {
				comment.setId(commentEditor.getCurrentComment().getId());
			}

			comment.setDraft(true);
			comment.setAuthor(getCurrentUser());
			comment.setCreationDate(new Date());

			ILineLocation location = getSelectedLineLocation();
			if (location != null) {
				comment.getLocations().add(location);
			}
			performOperation(comment);

			commentEditor.forceDispose();
		}
	}

	/**
	 * Performs a save or discard action
	 * 
	 * @param comment
	 *            the comment draft that will be added, edited or discarded from the comment thread
	 */
	private void performOperation(final IComment comment) {
		final IReviewItem item = reviewitem;
		final AtomicReference<IStatus> result = new AtomicReference<IStatus>();
		final ReviewBehavior reviewBehavior = annotationInput.getBehavior();

		if (!commentEditor.getState().equals(CommentEditorState.VIEW)) {
			final Job job = new Job(Messages.CommandServerOperation) {

				@Override
				public boolean belongsTo(Object family) {
					return family.equals(JOB_FAMILY);
				}

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					IStatus status = null;

					switch (commentEditor.getState()) {
					case REPLY:
					case EDIT:
						status = reviewBehavior.addComment(item, comment, monitor);
						if (status.isOK()) {
							result.set(status);
							updateClient(comment, item);
							return Status.OK_STATUS;
						}
						break;
					case DISCARD:
						status = reviewBehavior.discardComment(item, comment, monitor);
						if (status.isOK()) {
							result.set(status);
							updateClient(comment, item);
							return Status.OK_STATUS;
						}
						break;
					default:
						Assert.isTrue(false, "Unknown state " + commentEditor.getState()); //$NON-NLS-1$
					}
					processServerError(status.getMessage());

					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
	}

	/**
	 * Updates the UI with the result of the comment operation (either a new comment draft, draft comment edit or
	 * discard comment action)
	 * 
	 * @param comment
	 *            the comment that the operation was done on
	 * @param item
	 *            the item that will be updated
	 */
	private void updateClient(final IComment comment, final IReviewItem item) {
		comment.setAuthor(getCurrentUser());

		if (commentEditor.getState().equals(CommentEditorState.REPLY)) {
			item.getComments().add(comment);
		} else if (commentEditor.getState().equals(CommentEditorState.DISCARD)) {
			List<IComment> commentlist = item.getComments();
			for (Iterator<IComment> iter = commentlist.iterator(); iter.hasNext();) {
				IComment element = iter.next();
				if (element.getId() != null && element.getId().equals(commentEditor.getCurrentComment().getId())) {
					iter.remove();
					break;
				}
			}
		} else {
			List<IComment> commentlist = item.getComments();
			for (int i = 0; i < commentlist.size(); i++) {
				if (commentlist.get(i).getId() != null
						&& commentlist.get(i).getId().equals(commentEditor.getCurrentComment().getId())) {
					item.getComments().set(i, comment);
					break;
				}
			}

		}

		IFileItem file = null;
		if (item instanceof IFileItem) {
			file = (IFileItem) item;
		} else if (item instanceof IFileVersion) {
			file = ((IFileVersion) item).getFile();
		}
		if (file != null && file.getReview() != null) {
			// Update any review item set observers IFF we belong to a review. (The set might represent a compare,
			// in which case we won't have a relevant model object.)
			updateConsumer(file);

		}
	}

	/**
	 * Creates an error dialog for unsuccessful comment actions
	 * 
	 * @param message
	 *            the message that will be displayed to the user
	 */
	private void processServerError(final String message) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				final MessageDialog dialog = new MessageDialog(null, Messages.CommentPopupDialog_ServerError, null,
						message, MessageDialog.ERROR, new String[] { IDialogConstants.CANCEL_LABEL }, 0);
				dialog.open();
			}
		});
	}

	/**
	 * Updates the file with the new changes
	 * 
	 * @param file
	 *            the file that will be updated
	 */
	private void updateConsumer(IFileItem file) {
		final ReviewBehavior reviewBehavior = annotationInput.getBehavior();
		TaskRepository taskRepository = TasksUi.getRepositoryManager()
				.getRepository(reviewBehavior.getTask().getConnectorKind(),
						reviewBehavior.getTask().getRepositoryUrl());
		@SuppressWarnings("restriction")
		ReviewsConnector connector = (ReviewsConnector) TasksUiPlugin
				.getConnector(reviewBehavior.getTask().getConnectorKind());
		IReviewRemoteFactoryProvider factoryProvider = (IReviewRemoteFactoryProvider) connector
				.getReviewClient(taskRepository)
				.getFactoryProvider();

		RemoteEmfConsumer<IReviewItemSet, List<IFileItem>, String, ?, ?, Long> consumer = factoryProvider
				.getReviewItemSetContentFactory()
				.getConsumerForLocalKey(file.getSet(), file.getSet().getId());
		consumer.updateObservers();
		consumer.release();
	}

	/**
	 * @return the current {@link IUser} for this repository
	 */
	private IUser getCurrentUser() {
		if (reviewitem != null && reviewitem.getReview() != null && reviewitem.getReview().getRepository() != null) {
			return reviewitem.getReview().getRepository().getAccount();
		} else {
			return null;
		}
	}

	/**
	 * @return a {@ILineLocation} created from the {@link ILineRange} provided in the constructor
	 */
	protected ILineLocation getSelectedLineLocation() {
		ILineLocation location = IReviewsFactory.INSTANCE.createLineLocation();
		ILineRange lineRange = IReviewsFactory.INSTANCE.createLineRange();
		lineRange.setStart(range.getStartLine());
		lineRange.setEnd(range.getStartLine() + range.getNumberOfLines());
		location.getRanges().add(lineRange);
		return location;
	}

}
