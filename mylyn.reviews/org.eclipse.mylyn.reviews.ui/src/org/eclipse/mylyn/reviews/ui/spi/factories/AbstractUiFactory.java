/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.factories;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.reviews.core.spi.remote.review.IReviewRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.ui.spi.editor.AbstractReviewSection;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * Support UI context and implementation neutral creation of controls for a single component that modifies the state of
 * a model object and it's related remote objects. (For convenience, the factory delegates back to the supplied UI
 * context.)
 * 
 * @author Miles Parker
 */
public abstract class AbstractUiFactory<EObjectType> implements IUiContext {

	String name;

	private final EObjectType object;

	private final IUiContext context;

	private Button button;

	public AbstractUiFactory(String name, IUiContext context, EObjectType object) {
		this.context = context;
		this.name = name;
		this.object = object;
	}

	/**
	 * Creates a control.
	 * 
	 * @param context
	 * @param parent
	 * @param toolkit
	 * @return the created control; may be null in the case where the factory isn't executable.
	 */
	public Control createControl(IUiContext context, Composite parent, FormToolkit toolkit) {
		button = toolkit.createButton(parent, name, SWT.PUSH);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleExecute();
			}
		});
		button.setEnabled(!isExecutableStateKnown() || isExecutable());
		return button;
	}

	private void handleExecute() {
		if (isExecutableStateKnown()) {
			execute();
		} else {
			handleExecutionStateError();
		}
	}

	protected void handleExecutionStateError() {
		String message = NLS.bind(
				"Cannot {0}. Try re-synchronizing the review task. If that fails, there may be a problem with your repository connection.", //$NON-NLS-1$
				StringUtils.removeEnd(name, "...")); //$NON-NLS-1$
		StatusManager.getManager()
				.handle(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID, message),
						StatusManager.SHOW | StatusManager.LOG);
	}

	protected abstract boolean isExecutableStateKnown();

	public abstract boolean isExecutable();

	public abstract void execute();

	public EObjectType getModelObject() {
		return object;
	}

	public Shell getShell() {
		return context.getShell();
	}

	public ITask getTask() {
		return context.getTask();
	}

	public TaskData getTaskData() {
		return context.getTaskData();
	}

	public TaskEditor getEditor() {
		return context.getEditor();
	}

	public TaskRepository getTaskRepository() {
		return context.getTaskRepository();
	}

	public IReviewRemoteFactoryProvider getFactoryProvider() {
		return context.getFactoryProvider();
	}

	public IUiContext getContext() {
		return context;
	}

	/**
	 * May return null, e.g. in the case where a factory was used outside of an editor context.
	 * 
	 * @return
	 */
	public AbstractTaskEditorPage getTaskEditorPage() {
		if (getContext() instanceof AbstractTaskEditorPage) {
			return (AbstractTaskEditorPage) getContext();
		} else if (getContext() instanceof AbstractReviewSection) {
			return ((AbstractReviewSection) getContext()).getReviewEditorPage();
		}
		return null;
	}
}
