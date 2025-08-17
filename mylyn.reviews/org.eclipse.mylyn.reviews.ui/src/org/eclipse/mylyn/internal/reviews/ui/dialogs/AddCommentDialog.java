/*******************************************************************************
 * Copyright (c) 2011, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.workbench.editors.CommonTextSupport;
import org.eclipse.mylyn.internal.reviews.ui.Messages;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorExtensions;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.spi.ReviewsConnector;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.review.IReviewRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.ui.ProgressDialog;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorExtension;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Steffen Pingel
 * @author Miles Parker
 */
public class AddCommentDialog extends ProgressDialog {

	private RichTextEditor commentEditor;

	private final ILocation location;

	private final ReviewBehavior reviewBehavior;

	protected final ITask task;

	protected FormToolkit toolkit;

	private final IReviewItem item;

	private CommonTextSupport textSupport;

	public AddCommentDialog(Shell parentShell, ReviewBehavior reviewBehavior, IReviewItem item, ILocation location) {
		super(parentShell);
		this.reviewBehavior = reviewBehavior;
		this.item = item;
		this.location = location;
		task = reviewBehavior.getTask();
	}

	@Override
	public boolean close() {
		if (getReturnCode() == OK) {
			boolean shouldClose = performOperation(getComment());
			if (!shouldClose) {
				return false;
			}
		}
		if (textSupport != null) {
			textSupport.dispose();
		}
		return super.close();
	}

	public ILocation getLocation() {
		return location;
	}

	public ITask getTask() {
		return task;
	}

	private IComment getComment() {
		IComment comment = item.createComment(getLocation(), commentEditor.getText());
		return comment;
	}

	private boolean performOperation(final IComment comment) {
		final AtomicReference<IStatus> result = new AtomicReference<>();
		try {
			run(true, true, monitor -> result.set(reviewBehavior.addComment(item, comment, monitor)));
		} catch (InvocationTargetException e) {
			StatusManager.getManager()
					.handle(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID,
							"Unexpected error during execution of operation", e), //$NON-NLS-1$
							StatusManager.SHOW | StatusManager.LOG);
			return false;
		} catch (InterruptedException e) {
			// cancelled
			return false;
		}

		if (result.get().getSeverity() == IStatus.CANCEL) {
			return false;
		}

		if (result.get().isOK()) {
			item.getComments().add(comment);
			IFileItem file = null;
			if (item instanceof IFileItem) {
				file = (IFileItem) item;
			} else if (item instanceof IFileVersion) {
				file = ((IFileVersion) item).getFile();
			}
			if (file != null && file.getReview() != null) {
				//Update any review item set observers IFF we belong to a review. (The set might represent a compare, in which case we won't have a relevant model object.)
				TaskRepository taskRepository = TasksUi.getRepositoryManager()
						.getRepository(reviewBehavior.getTask().getConnectorKind(),
								reviewBehavior.getTask().getRepositoryUrl());
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
			return true;
		} else {
			StatusManager.getManager().handle(result.get(), StatusManager.SHOW | StatusManager.LOG);
			return false;

		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		toolkit = new FormToolkit(TasksUiPlugin.getDefault().getFormColors(parent.getDisplay()));
		return super.createDialogArea(parent);
	}

	@Override
	protected Control createPageControls(Composite parent) {
		getShell().setText(Messages.Reviews_AddCommentDialog_Title);
		setTitle(Messages.Reviews_AddCommentDialog_Title);
		setMessage(NLS.bind(Messages.Reviews_AddCommentDialog_Message, new Path(item.getName()).lastSegment(),
				location.getIndex()));

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		commentEditor = createRichTextEditor(composite, ""); //$NON-NLS-1$
		GridDataFactory.fillDefaults().grab(true, true).applyTo(commentEditor.getControl());

		return composite;
	}

	protected RichTextEditor createRichTextEditor(Composite composite, String value) {
		int style = SWT.FLAT | SWT.BORDER | SWT.MULTI | SWT.WRAP;

		TaskRepository repository = TasksUi.getRepositoryManager()
				.getRepository(task.getConnectorKind(), task.getRepositoryUrl());
		AbstractTaskEditorExtension extension = TaskEditorExtensions.getTaskEditorExtension(repository);

		final RichTextEditor editor = new RichTextEditor(repository, style, null, extension, task);
		editor.setText(value);
		editor.setSpellCheckingEnabled(true);
		editor.createControl(composite, toolkit);
		IHandlerService handlerService = PlatformUI.getWorkbench().getService(IHandlerService.class);
		if (handlerService != null) {
			textSupport = new CommonTextSupport(handlerService);
			textSupport.install(editor.getViewer(), true);
		}

		// HACK: this is to make sure that we can't have multiple things highlighted
		editor.getViewer().getTextWidget().addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				editor.getViewer().getTextWidget().setSelection(0);
			}
		});

		return editor;
	}

	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.RESIZE);
		setBlockOnOpen(false);
	}
}
