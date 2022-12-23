/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.bugzilla.ui.wizard;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class NewBugzillaTaskWizard extends NewTaskWizard implements INewWizard {

	private IStructuredSelection selection;

	public NewBugzillaTaskWizard(TaskRepository taskRepository, ITaskMapping taskSelection) {
		super(taskRepository, taskSelection);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	/**
	 * @since 3.0
	 */
	@Override
	protected ITaskMapping getInitializationData() {
		if (getTaskSelection() != null) {
			return getTaskSelection();
		}

		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null) {
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			if (window != null) {
				ISelection sel = window.getSelectionService().getSelection();
				if (sel instanceof IStructuredSelection) {
					selection = (IStructuredSelection) sel;
				}
			}
		}

		final AtomicReference<String> product = new AtomicReference<String>();
		final AtomicReference<String> component = new AtomicReference<String>();

		if (selection == null || selection.isEmpty()) {
			product.set(getTaskRepository().getProperty(IBugzillaConstants.LAST_PRODUCT_SELECTION));
			component.set(getTaskRepository().getProperty(IBugzillaConstants.LAST_COMPONENT_SELECTION));
		} else {
			Object element = selection.getFirstElement();
			extractMapping(element, product, component);
		}

		if (product.get() != null) {
			return new TaskMapping() {
				@Override
				public String getProduct() {
					return product.get();
				}

				@Override
				public String getComponent() {
					return component.get();
				}
			};
		}

		return null;
	}

	private void extractMapping(Object element, AtomicReference<String> product, AtomicReference<String> component) {
		if (element instanceof ITask) {
			ITask task = (ITask) element;
			product.set(task.getAttribute(BugzillaAttribute.PRODUCT.getKey()));
		} else {
			IRepositoryQuery query = null;
			if (element instanceof IRepositoryQuery) {
				query = (IRepositoryQuery) element;
			}

			if (query != null && query.getConnectorKind().equals(BugzillaCorePlugin.CONNECTOR_KIND)) {
				String queryUrl = query.getUrl();
				queryUrl = queryUrl.substring(queryUrl.indexOf("?") + 1); //$NON-NLS-1$
				String[] options = queryUrl.split("&"); //$NON-NLS-1$

				for (String option : options) {
					int index = option.indexOf("="); //$NON-NLS-1$
					if (index != -1) {
						String key = option.substring(0, index);
						if ("product".equals(key) && product.get() == null) { //$NON-NLS-1$
							try {
								product.set(URLDecoder.decode(option.substring(index + 1),
										getTaskRepository().getCharacterEncoding()));
							} catch (UnsupportedEncodingException ex) {
								// ignore
							}
						} else if ("component".equals(key) && component.get() == null) { //$NON-NLS-1$
							try {
								component.set(URLDecoder.decode(option.substring(index + 1),
										getTaskRepository().getCharacterEncoding()));
							} catch (UnsupportedEncodingException ex) {
								// ignore
							}
						}
					}
				}
			} else {
				if (element instanceof IAdaptable) {
					IAdaptable adaptable = (IAdaptable) element;
					ITask task = (ITask) adaptable.getAdapter(ITask.class);
					if (task != null) {
						product.set(task.getAttribute(BugzillaAttribute.PRODUCT.getKey()));
					}
				}
			}
		}
	}

}
