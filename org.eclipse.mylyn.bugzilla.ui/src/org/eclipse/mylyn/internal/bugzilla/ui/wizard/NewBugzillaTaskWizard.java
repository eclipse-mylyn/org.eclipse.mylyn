/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.wizard;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

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
		if (selection == null || selection.isEmpty()) {
			final String lastSelection = getTaskRepository().getProperty(IBugzillaConstants.LAST_PRODUCT_SELECTION);
			return new TaskMapping() {
				@Override
				public String getProduct() {
					return lastSelection;
				}
			};

		}

		final ArrayList<String> products = new ArrayList<String>();

		Object element = (selection).getFirstElement();
		if (element instanceof ITask) {
			ITask bugzillaTask = (ITask) element;
			if (bugzillaTask.getAttribute(BugzillaAttribute.PRODUCT.getKey()) != null) {
				products.add(bugzillaTask.getAttribute(BugzillaAttribute.PRODUCT.getKey()));
			}
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
						if ("product".equals(key)) { //$NON-NLS-1$
							try {
								products.add(URLDecoder.decode(option.substring(index + 1),
										getTaskRepository().getCharacterEncoding()));
								// TODO: list box only accepts a single selection so
								// we break on first found
								break;
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
						ITask bugzillaTask = (ITask) element;
						if (bugzillaTask.getAttribute(BugzillaAttribute.PRODUCT.getKey()) != null) {
							products.add(bugzillaTask.getAttribute(BugzillaAttribute.PRODUCT.getKey()));
						}
					}
				}
			}
		}

		if (products.size() > 0) {
			return new TaskMapping() {
				@Override
				public String getProduct() {
					return products.get(0);
				}
			};
		}
		return null;
	}

}
