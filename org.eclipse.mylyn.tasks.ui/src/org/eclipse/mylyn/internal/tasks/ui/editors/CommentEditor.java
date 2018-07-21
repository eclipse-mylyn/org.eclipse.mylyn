/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.mylyn.commons.workbench.editors.CommonTextSupport;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorExtension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ActiveShellExpression;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;

public abstract class CommentEditor {
	private RichTextEditor textEditor;

	private IContextService contextService;

	private IContextActivation commentContext;

	private CommonTextSupport textSupport;

	private final ITask task;

	private final TaskRepository taskRepository;

	public CommentEditor(ITask task, TaskRepository taskRepository) {
		this.task = task;
		this.taskRepository = taskRepository;
	}

	public void createControl(Composite composite) {
		AbstractTaskEditorExtension extension = TaskEditorExtensions.getTaskEditorExtension(taskRepository);
		if (extension != null) {
			String contextId = extension.getEditorContextId();
			if (contextId != null) {
				contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
				if (contextService != null) {
					commentContext = contextService.activateContext(contextId,
							new ActiveShellExpression(composite.getShell()));
				}
			}
		}

		textEditor = new RichTextEditor(taskRepository, SWT.V_SCROLL | SWT.BORDER | SWT.WRAP, contextService,
				extension, task) {
			@Override
			protected void valueChanged(String value) {
				CommentEditor.this.valueChanged(value);
			};
		};
		textEditor.createControl(composite, null);
		textEditor.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
		if (handlerService != null) {
			textSupport = new CommonTextSupport(handlerService);
			textSupport.install(textEditor.getViewer(), true);
		}
	}

	protected abstract void valueChanged(String value);

	public RichTextEditor getTextEditor() {
		return textEditor;
	}

	public String getText() {
		return textEditor.getText();
	}

	public void dispose() {
		if (contextService != null && commentContext != null) {
			contextService.deactivateContext(commentContext);
			commentContext = null;
		}
		if (textSupport != null) {
			textSupport.dispose();
		}
	}
}
