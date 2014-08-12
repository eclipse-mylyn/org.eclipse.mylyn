/*******************************************************************************
 * Copyright (c) 2014 Ericsson AB and others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Guy Perron - original version
 *
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.dialogs;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.source.LineRange;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILineRange;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.spi.ReviewsConnector;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.review.IReviewRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.internal.core.model.Comment;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsFactory;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * This class implements the dialog used to fill-in the Comment element details.
 * 
 * @author Guy Perron
 */
public class CommentInputDialog extends FormDialog {

	private Text fCommentInputTextField;

	private static final int BUTTON_REPLY_ID = 2;

	private static final int BUTTON_REPLY_DONE_ID = 3;

	private static final int BUTTON_DISCARD_ID = 4;

	private static final int BUTTON_EDIT_ID = 5;

	private static final int BUTTON_SAVE_ID = 6;

	private static final String RESIZABLE_DIALOG_SETTINGS = "MyResizableDialogSettings"; //$NON-NLS-1$

	private final ReviewBehavior reviewBehavior;

	private final IReviewItem reviewitem;

	private final LineRange range;

	private List<Comment> commentList;

	private boolean isUpdate = false;

	private boolean isDiscard = false;

	private boolean isSave = false;

	private boolean isDone = false;

	private boolean isReply = false;

	private String currentUuid;

	private Composite buttonparent;

	private Composite buttonBarParent;

	private final Shell parent;

	public CommentInputDialog(Shell aParentShell, ReviewBehavior reviewBehavior, IReviewItem reviewitm, LineRange range) {
		super(aParentShell);
		if (!isWindowPlatform()) {
			setShellStyle(SWT.MODELESS | SWT.SHELL_TRIM | SWT.BORDER);
		}
		this.reviewBehavior = reviewBehavior;
		this.reviewitem = reviewitm;
		this.range = range;
		this.parent = aParentShell;

	}

	private boolean isWindowPlatform() {
		return Platform.getOS().equals(Platform.WS_WIN32);
	}

	@Override
	protected IDialogSettings getDialogBoundsSettings() {
		IDialogSettings settings = ReviewsUiPlugin.getDefault().getDialogSettings();

		IDialogSettings section = settings.getSection(RESIZABLE_DIALOG_SETTINGS);
		if (section == null) {
			return settings.addNewSection(RESIZABLE_DIALOG_SETTINGS);
		}
		return section;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			super.buttonPressed(IDialogConstants.OK_ID);
			parent.setFocus();
			return;
		}

		IComment comment = ReviewsFactory.eINSTANCE.createComment();
		comment.setDescription(fCommentInputTextField.getText().trim());
		switch (buttonId) {
		case BUTTON_EDIT_ID:
			comment.setId(currentUuid);
			disposeButtons();
			createSaveDiscard();
			return;
		case BUTTON_DISCARD_ID:
			comment.setId(currentUuid);
			isDiscard = true;
			break;
		case BUTTON_REPLY_ID:
			comment.setId(currentUuid);
			isSave = true;
			isReply = true;
			disposeButtons();
			createSave();
			fCommentInputTextField.setText(""); //$NON-NLS-1$
			break;
		case BUTTON_REPLY_DONE_ID:
			isSave = true;
			isDone = true;
			comment.setDescription(Messages.CommentInputDialog_Done);
			comment.setId(""); //$NON-NLS-1$
			break;
		case BUTTON_SAVE_ID:
			comment.setId(currentUuid);
			isSave = true;
			if (isReply) {
				isDone = true;
				comment.setId(""); //$NON-NLS-1$
				isReply = false;
			}
		}
		comment.setDraft(true);
		comment.setAuthor(getCurrentUser());
		comment.setCreationDate(new Date());

		ILineLocation location = getSelectedLineLocation();
		if (location != null) {
			comment.getLocations().add(location);
		}
		if (buttonId != BUTTON_REPLY_ID) {
			performOperation(comment);
			super.buttonPressed(0);
		}

	}

	private ILineLocation getSelectedLineLocation() {
		ILineLocation location = ReviewsFactory.eINSTANCE.createLineLocation();
		ILineRange lineRange = ReviewsFactory.eINSTANCE.createLineRange();
		lineRange.setStart(range.getStartLine());
		lineRange.setEnd(range.getStartLine() + range.getNumberOfLines());
		location.getRanges().add(lineRange);
		return location;
	}

	private IUser getCurrentUser() {
		if (reviewitem != null && reviewitem.getReview() != null && reviewitem.getReview().getRepository() != null) {
			return reviewitem.getReview().getRepository().getAccount();
		} else {
			return null;
		}
	}

	private boolean performOperation(final IComment comment) {
		final IReviewItem item = reviewitem;
		final AtomicReference<IStatus> result = new AtomicReference<IStatus>();

		if (isSave || isDiscard) {
			final Job job = new Job(Messages.CommandServerOperation) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					IStatus status = null;
					if (isSave) {
						status = reviewBehavior.addComment(item, comment, monitor);
						if (status.isOK()) {
							result.set(status);
							updateClient(comment, item);
							return Status.OK_STATUS;
						}
					} else {
						status = reviewBehavior.discardComment(item, comment, monitor);
						if (status.isOK()) {
							result.set(status);
							updateClient(comment, item);
							return Status.OK_STATUS;
						}
					}
					processServerError(status.getMessage());

					return Status.OK_STATUS;
				}

			};
			job.setUser(true);
			job.schedule();
		}

		return true;

	}

	private void updateClient(final IComment comment, final IReviewItem item) {
		comment.setAuthor(getCurrentUser());

		if (!isUpdate || isDone) {
			item.getComments().add(comment);
		} else if (isDiscard) {
			List<IComment> commentlist = item.getComments();
			for (Iterator<IComment> iter = commentlist.iterator(); iter.hasNext();) {
				IComment element = iter.next();
				if (element.getId() != null && element.getId().equals(currentUuid)) {
					iter.remove();
					break;
				}
			}
		} else {
			List<IComment> commentlist = item.getComments();
			for (int i = 0; i < commentlist.size(); i++) {
				if (commentlist.get(i).getId() != null && commentlist.get(i).getId().equals(currentUuid)) {
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

	private void processServerError(final String message) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				final MessageDialog dialog = new MessageDialog(null, Messages.CommentInputDialog_ServerError, null,
						message, MessageDialog.ERROR, new String[] { IDialogConstants.CANCEL_LABEL }, 0);
				dialog.open();
			}
		});
	}

	private void updateConsumer(IFileItem file) {
		TaskRepository taskRepository = TasksUi.getRepositoryManager().getRepository(
				reviewBehavior.getTask().getConnectorKind(), reviewBehavior.getTask().getRepositoryUrl());
		@SuppressWarnings("restriction")
		ReviewsConnector connector = (ReviewsConnector) TasksUiPlugin.getConnector(reviewBehavior.getTask()
				.getConnectorKind());
		IReviewRemoteFactoryProvider factoryProvider = (IReviewRemoteFactoryProvider) connector.getReviewClient(
				taskRepository).getFactoryProvider();

		RemoteEmfConsumer<IReviewItemSet, List<IFileItem>, String, ?, ?, Long> consumer = factoryProvider.getReviewItemSetContentFactory()
				.getConsumerForLocalKey(file.getSet(), file.getSet().getId());
		consumer.updateObservers();
		consumer.release();
	}

	@Override
	protected void createFormContent(final IManagedForm mform) {
		final ScrolledForm scrolledform = mform.getForm();
		scrolledform.setExpandVertical(true);
		scrolledform.setExpandHorizontal(true);
		final Composite composite = scrolledform.getBody();

		scrolledform.setContent(composite);

		final GridLayout layout = new GridLayout(1, false);

		composite.setLayout(layout);
		GridData textGridData = null;

		for (Comment comment : commentList) {
			final Button button = new Button(composite, SWT.RADIO);
			button.setBackground(composite.getBackground());

			final String uuid = comment.getId();
			final boolean isDraft = comment.isDraft();
			final String commentText = comment.getDescription();
			final String authorAndDate;
			IUser author = comment.getAuthor();
			if (author != null) {
				authorAndDate = comment.getAuthor().getDisplayName() + " " //$NON-NLS-1$
						+ comment.getCreationDate().toString();
			} else {
				authorAndDate = Messages.CommentInputDialog_No_author + " " //$NON-NLS-1$
						+ comment.getCreationDate().toString();
			}

			String commentPrefix = StringUtils.abbreviate(comment.getDescription(), 50);
			if (isDraft) {
				button.setText(NLS.bind(Messages.CommentInputDialog_Draft, authorAndDate, commentPrefix));
			} else {
				button.setText(authorAndDate + " " + commentPrefix); //$NON-NLS-1$
			}

			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					disposeButtons();
					if (isDraft) {
						createEdit();
					} else {
						createReplyReplyDone();
					}
					currentUuid = uuid;
					fCommentInputTextField.setText(commentText);
					isUpdate = true;
				}
			});
			GridDataFactory.fillDefaults().span(2, 1).applyTo(button);
		}

		fCommentInputTextField = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP);

		textGridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		textGridData.horizontalSpan = 2;
		textGridData.minimumHeight = fCommentInputTextField.getLineHeight() * 4;
		fCommentInputTextField.setToolTipText(Messages.ReviewsCommentToolTip);
		fCommentInputTextField.setLayoutData(textGridData);
		fCommentInputTextField.setEnabled(false);

		//Set default focus
		fCommentInputTextField.setFocus();

		if (!isWindowPlatform()) {
			getShell().addShellListener(new ShellAdapter() {
				@Override
				public void shellDeactivated(ShellEvent e) {
					boolean isExit = MessageDialog.openQuestion(getShell(),
							Messages.CommentInputDialog_ConfirmExitCaption, Messages.CommentInputDialog_ConfirmExit);
					if (isExit) {
						buttonPressed(IDialogConstants.CANCEL_ID);
					} else {
						getShell().setFocus();
					}
				}
			});
		}

		this.setHelpAvailable(false);

	}

	@Override
	protected Control createButtonBar(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		buttonBarParent = parent;

		GridLayoutFactory.fillDefaults().spacing(0, 0).applyTo(composite);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false));
		composite.setFont(parent.getFont());

		final Control buttonSection = super.createButtonBar(composite);
		((GridData) buttonSection.getLayoutData()).grabExcessHorizontalSpace = true;
		((GridData) buttonSection.getLayoutData()).grabExcessVerticalSpace = false;
		buttonSection.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				buttonBar.pack();
			}
		});

		return composite;

	}

	@Override
	public void createButtonsForButtonBar(Composite parent) {
		buttonparent = parent;
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	public void setComments(List<Comment> commentList) {
		this.commentList = commentList;
	}

	private void disposeButtons() {
		for (int buttonId : new int[] { BUTTON_REPLY_ID, BUTTON_REPLY_DONE_ID, BUTTON_DISCARD_ID, BUTTON_EDIT_ID,
				BUTTON_SAVE_ID }) {
			Button button = getButton(buttonId);
			if (button != null) {
				button.dispose();
			}
		}
		fCommentInputTextField.setEnabled(false);

	}

	private void createEdit() {
		createButton(buttonparent, BUTTON_EDIT_ID, Messages.CommentInputDialog_Edit, true);
		buttonBar.pack();
	}

	private void createReplyReplyDone() {
		createButton(buttonparent, BUTTON_REPLY_ID, Messages.CommentInputDialog_Reply, true);
		createButton(buttonparent, BUTTON_REPLY_DONE_ID, Messages.CommentInputDialog_ReplyDone, true);
		buttonBar.pack();
	}

	private void createSaveDiscard() {
		createButton(buttonparent, BUTTON_SAVE_ID, Messages.CommentInputDialog_Save, true);
		createButton(buttonparent, BUTTON_DISCARD_ID, Messages.CommentInputDialog_Discard, true);
		fCommentInputTextField.setEnabled(true);
		buttonBar.pack();

	}

	private void createSave() {
		createButton(buttonparent, BUTTON_SAVE_ID, Messages.CommentInputDialog_Save, true);
		fCommentInputTextField.setEnabled(true);
		buttonBar.pack();

	}

}
