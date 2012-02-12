/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorExtensions;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsFactory;
import org.eclipse.mylyn.reviews.ui.ProgressDialog;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorExtension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Steffen Pingel
 */
public class AddCommentDialog extends ProgressDialog {

	private RichTextEditor commentEditor;

	private final ILocation location;

	private final ReviewBehavior reviewBehavior;

	protected final ITask task;

	protected FormToolkit toolkit;

	private final IReviewItem item;

	public AddCommentDialog(Shell parentShell, ReviewBehavior reviewBehavior, IReviewItem item, ILocation location) {
		super(parentShell);
		this.reviewBehavior = reviewBehavior;
		this.item = item;
		this.location = location;
		this.task = reviewBehavior.getTask();
	}

	@Override
	public boolean close() {
		if (getReturnCode() == OK) {
			boolean shouldClose = performOperation(getTopic());
			if (!shouldClose) {
				return false;
			}
		}
		return super.close();
	}

	public ILocation getLocation() {
		return location;
	}

	public ITask getTask() {
		return task;
	}

	private ITopic getTopic() {
		ITopic topic = ReviewsFactory.eINSTANCE.createTopic();
		topic.setDraft(true);
		IUser user = ReviewsFactory.eINSTANCE.createUser();
		user.setDisplayName("Me");
		topic.setAuthor(user);
		topic.setDescription(commentEditor.getText());
		topic.setLocation(getLocation());
		topic.setItem(item);

		IComment comment = ReviewsFactory.eINSTANCE.createComment();
		comment.setDescription(topic.getDescription());
		comment.setAuthor(topic.getAuthor());
		comment.setCreationDate(new Date());
		topic.getComments().add(comment);

		return topic;
	}

	private boolean performOperation(final ITopic topic) {
		final AtomicReference<IStatus> result = new AtomicReference<IStatus>();
		try {
			run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					result.set(reviewBehavior.addTopic(item, topic, monitor));
				}
			});
		} catch (InvocationTargetException e) {
			StatusManager.getManager().handle(
					new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID,
							"Unexpected error during execution of operation", e),
					StatusManager.SHOW | StatusManager.LOG);
		} catch (InterruptedException e) {
			// cancelled
			return false;
		}

		if (result.get().getSeverity() == IStatus.CANCEL) {
			return false;
		}

		if (result.get().isOK()) {
			item.getTopics().add(topic);
			return true;
		} else {
			StatusManager.getManager().handle(result.get(), StatusManager.SHOW | StatusManager.LOG);
			return false;

		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		toolkit = new FormToolkit(TasksUiPlugin.getDefault().getFormColors(parent.getDisplay()));
		Control control = super.createDialogArea(parent);
		return control;
	}

	@Override
	protected Control createPageControls(Composite parent) {
		setTitle("Add Comment");
		setMessage("");

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		commentEditor = createRichTextEditor(composite, ""); //$NON-NLS-1$
		GridDataFactory.fillDefaults().grab(true, true).applyTo(commentEditor.getControl());

		return composite;
	}

	protected RichTextEditor createRichTextEditor(Composite composite, String value) {
		int style = SWT.FLAT | SWT.BORDER | SWT.MULTI | SWT.WRAP;

		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		AbstractTaskEditorExtension extension = TaskEditorExtensions.getTaskEditorExtension(repository);

		final RichTextEditor editor = new RichTextEditor(repository, style, null, extension, task);
		editor.setText(value);
		editor.createControl(composite, toolkit);

		// HACK: this is to make sure that we can't have multiple things highlighted
		editor.getViewer().getTextWidget().addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				editor.getViewer().getTextWidget().setSelection(0);
			}
		});

		return editor;
	}

}
