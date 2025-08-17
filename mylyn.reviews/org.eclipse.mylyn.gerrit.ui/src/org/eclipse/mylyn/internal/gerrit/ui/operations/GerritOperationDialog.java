/*******************************************************************************
 * Copyright (c) 2011, 2014 Tasktop Technologies.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Sascha Scholz (SAP) - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.operations;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.workbench.editors.CommonTextSupport;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.internal.gerrit.core.GerritOperationFactory;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.internal.gerrit.core.operations.GerritOperation;
import org.eclipse.mylyn.internal.gerrit.core.operations.RefreshConfigRequest;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.SynchronizeEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorExtensions;
import org.eclipse.mylyn.reviews.ui.ProgressDialog;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.ITasksUiFactory;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorExtension;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.statushandlers.StatusManager;

import com.google.gerrit.common.data.GerritConfig;

/**
 * @author Steffen Pingel
 * @author Benjamin Muskalla
 * @author Sascha Scholz
 * @author Miles Parker
 */
public abstract class GerritOperationDialog extends ProgressDialog {

	private boolean needsConfig;

	protected final ITask task;

	protected FormToolkit toolkit;

	private CommonTextSupport textSupport;

	public GerritOperationDialog(Shell parentShell, ITask task) {
		super(parentShell);
		this.task = task;
	}

	@Override
	public boolean close() {
		if (getReturnCode() == OK) {
			boolean shouldClose = performOperation(createOperation());
			if (!shouldClose) {
				return false;
			}
		}
		if (textSupport != null) {
			textSupport.dispose();
		}
		return super.close();
	}

	public abstract GerritOperation<?> createOperation();

	public GerritOperationFactory getOperationFactory() {
		return GerritUiPlugin.getDefault().getOperationFactory();
	}

	public ITask getTask() {
		return task;
	}

	public boolean needsConfig() {
		return needsConfig;
	}

	public void setNeedsConfig(boolean needsConfig) {
		this.needsConfig = needsConfig;
	}

	private boolean performOperation(final GerritOperation<?> operation) {
		final AtomicReference<IStatus> result = new AtomicReference<>();
		try {
			run(true, true, monitor -> result.set(operation.run(monitor)));
		} catch (InvocationTargetException e) {
			StatusManager.getManager()
					.handle(new Status(IStatus.ERROR, GerritUiPlugin.PLUGIN_ID,
							"Unexpected error during execution of Gerrit operation", e), //$NON-NLS-1$
							StatusManager.SHOW | StatusManager.LOG);
		} catch (InterruptedException e) {
			// cancelled
			return false;
		}

		if (result.get().getSeverity() == IStatus.CANCEL) {
			return false;
		}

		if (!result.get().isOK()) {
			StatusManager.getManager().handle(result.get(), StatusManager.SHOW | StatusManager.LOG);
			return false;
		}
		return processOperationResult(operation);
	}

	protected boolean processOperationResult(GerritOperation<?> operation) {
		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		toolkit = new FormToolkit(CommonFormUtil.getSharedColors());
		Control control = super.createDialogArea(parent);
		if (needsConfig()) {
			GerritConfig config = getOperationFactory().getClient(getTask()).getGerritConfig();
			if (config == null) {
				GerritOperation<GerritConfiguration> operation = getOperationFactory().createOperation(getTask(),
						new RefreshConfigRequest());
				performOperation(operation);
				config = operation.getOperationResult().getGerritConfig();
			}
			doRefresh(config);
		}
		return control;
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

	protected Text createPersonTextEditor(Composite composite, String value) {
		int style = SWT.FLAT | SWT.BORDER | SWT.MULTI | SWT.WRAP;
		Text editor = new Text(composite, style);
		if (value != null) {
			editor.setText(value);
		}
		TaskRepository repository = TasksUi.getRepositoryManager()
				.getRepository(task.getConnectorKind(), task.getRepositoryUrl());
		ITasksUiFactory uiFactory = TasksUi.getUiFactory();
		IContentProposalProvider proposalProvider = uiFactory.createPersonContentProposalProvider(repository);
		ILabelProvider proposalLabelProvider = uiFactory.createPersonContentProposalLabelProvider(repository);

		ContentAssistCommandAdapter adapter = new ContentAssistCommandAdapter(editor, new TextContentAdapter(),
				proposalProvider, IWorkbenchCommandConstants.EDIT_CONTENT_ASSIST, new char[0], true);
		adapter.setLabelProvider(proposalLabelProvider);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

		return editor;
	}

	protected void doRefresh(GerritConfig config) {
	}

	public int open(TaskEditor editor) {
		int open = open();
		if (open == Window.OK && editor != null) {
			SynchronizeEditorAction action = new SynchronizeEditorAction();
			action.selectionChanged(new StructuredSelection(editor));
			action.run();
		}
		return open;
	}
}
