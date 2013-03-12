/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.factories;

import org.eclipse.mylyn.reviews.core.spi.remote.AbstractRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.ui.spi.editor.AbstractReviewSection;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

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
		if (isExecutable()) {
			Button button = toolkit.createButton(parent, name, SWT.PUSH);
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					execute();
				}
			});
			return button;
		}
		return null;
	}

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

	public AbstractRemoteFactoryProvider getRemoteFactoryProvider() {
		return context.getRemoteFactoryProvider();
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
