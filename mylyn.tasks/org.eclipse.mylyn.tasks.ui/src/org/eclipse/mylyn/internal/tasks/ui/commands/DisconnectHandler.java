/*******************************************************************************
 * Copyright (c) 2012, 2013 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.menus.UIElement;

/**
 * @author Frank Becker
 * @author Steffen Pingel
 */
public class DisconnectHandler extends AbstractHandler implements IElementUpdater {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Command command0 = event.getCommand();

		boolean oldValue = HandlerUtil.toggleCommandState(command0);
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			for (Iterator<?> iter = ((IStructuredSelection) selection).iterator(); iter.hasNext();) {
				Object item = iter.next();
				if (item instanceof TaskRepository) {
					((TaskRepository) item).setOffline(!oldValue);
					TasksUiPlugin.getRepositoryManager().notifyRepositorySettingsChanged((TaskRepository) item);
				}
			}
		}
		return null;
	}

	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
		IWorkbenchWindow window = (IWorkbenchWindow) element.getServiceLocator().getService(IWorkbenchWindow.class);
		if (window != null) {
			IWorkbenchPage activePage = ((WorkbenchWindow) window).getActivePage();
			if (activePage != null) {
				IWorkbenchPart activePart = activePage.getActivePart();
				if (activePart != null) {
					ISelectionProvider selectionProvider = activePart.getSite().getSelectionProvider();
					if (selectionProvider != null) {
						ISelection selection = selectionProvider.getSelection();
						if (selection instanceof IStructuredSelection) {
							// only for enabled Handlers the updateElement is called
							// so we only need the first repository for set the state
							Object firstRepository = ((IStructuredSelection) selection).getFirstElement();
							if (firstRepository instanceof TaskRepository) {
								boolean checked = ((TaskRepository) firstRepository).isOffline();
								element.setChecked(checked);
							}
						}
					}
				}
			}
		}
	}
}
