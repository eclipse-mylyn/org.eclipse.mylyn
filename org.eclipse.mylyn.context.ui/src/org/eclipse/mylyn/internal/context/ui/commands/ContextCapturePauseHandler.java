/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.commands;

import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

/**
 * @author Eugene Kuleshov
 */
public class ContextCapturePauseHandler extends AbstractHandler //
		implements IElementUpdater, IInteractionContextListener {

	public ContextCapturePauseHandler() {
		ContextCorePlugin.getContextManager().addListener(this);
	}

	@Override
	public void dispose() {
		ContextCorePlugin.getContextManager().removeListener(this);
		super.dispose();
	}

	@Override
	public Object execute(ExecutionEvent e) throws ExecutionException {
		if (ContextCorePlugin.getContextManager().isContextCapturePaused()) {
			resume();
		} else {
			pause();
		}
		return null;
	}

	public void resume() {
		ContextCorePlugin.getContextManager().setContextCapturePaused(false);
		if (TaskListView.getFromActivePerspective() != null) {
			TaskListView.getFromActivePerspective().indicatePaused(false);
		}
		refreshCommands();
	}

	public void pause() {
		ContextCorePlugin.getContextManager().setContextCapturePaused(true);
		TaskListView.getFromActivePerspective().indicatePaused(true);

		refreshCommands();
	}

	private void refreshCommands() {
		ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		if (service != null) {
			service.refreshElements("org.eclipse.mylyn.tasks.ui.command.previousTask", null);
			service.refreshElements("org.eclipse.mylyn.ui.context.capture.pause.command", null);
		}
	}

	// IElementUpdater

	@SuppressWarnings("unchecked")
	public void updateElement(UIElement element, Map parameters) {
		element.setChecked(ContextCorePlugin.getContextManager().isContextCapturePaused());
	}

	// IInteractionContextListener

	public void contextActivated(IInteractionContext context) {
		resume();
	}

	public void contextDeactivated(IInteractionContext context) {
		resume();
	}

	public void contextCleared(IInteractionContext context) {
		// ignore
	}

	public void elementDeleted(IInteractionElement element) {
		// ignore
	}

	public void interestChanged(List<IInteractionElement> elements) {
		// ignore
	}

	public void landmarkAdded(IInteractionElement element) {
		// ignore
	}

	public void landmarkRemoved(IInteractionElement element) {
		// ignore
	}

	public void relationsChanged(IInteractionElement element) {
		// ignore
	}

}