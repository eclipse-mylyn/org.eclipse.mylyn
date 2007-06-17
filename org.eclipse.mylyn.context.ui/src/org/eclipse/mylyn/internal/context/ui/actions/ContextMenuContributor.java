/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.actions;

import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.IDynamicSubMenuContributor;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 */
public class ContextMenuContributor implements IDynamicSubMenuContributor {

	private static final String LABEL = "Context";

	public MenuManager getSubMenuManager(final List<AbstractTaskContainer> selectedElements) {
		final MenuManager subMenuManager = new MenuManager(LABEL);

		subMenuManager.setVisible(selectedElements.size() == 1 && selectedElements.get(0) instanceof AbstractTask);

		AbstractTask task = (AbstractTask) selectedElements.get(0);
		StructuredSelection selection = new StructuredSelection(task);
		if (!task.isLocal()) {
			ContextAttachAction attachAction = new ContextAttachAction();
			attachAction.selectionChanged(attachAction, selection);
			subMenuManager.add(attachAction);
			
			ContextRetrieveAction retrieveAction = new ContextRetrieveAction();
			retrieveAction.selectionChanged(retrieveAction, selection);
			subMenuManager.add(retrieveAction);
		}
		ContextCopyAction copyAction = new ContextCopyAction();
		copyAction.selectionChanged(copyAction, selection);
		subMenuManager.add(copyAction);
		ContextClearAction clearAction = new ContextClearAction();
		clearAction.selectionChanged(clearAction, selection);
		subMenuManager.add(clearAction);
		return subMenuManager;
	}

	/**
	 * public for testing
	 * 
	 * Deals with text where user has entered a '@' or tab character but which are not meant to be accelerators. from:
	 * Action#setText: Note that if you want to insert a '@' character into the text (but no accelerator, you can simply
	 * insert a '@' or a tab at the end of the text. see Action#setText
	 */
	public String handleAcceleratorKeys(String text) {
		if (text == null) {
			return null;
		}

		int index = text.lastIndexOf('\t');
		if (index == -1) {
			index = text.lastIndexOf('@');
		}
		if (index >= 0) {
			return text.concat("@");
		}
		return text;
	}

	/**
	 * @param selectedElements
	 * @param category
	 */
	private void moveToCategory(final List<AbstractTaskContainer> selectedElements, AbstractTaskCategory category) {
		for (AbstractTaskContainer element : selectedElements) {
			if (element instanceof AbstractTask) {
				TasksUiPlugin.getTaskListManager().getTaskList().moveToContainer((AbstractTask) element, category);
			}
		}
	}

}
