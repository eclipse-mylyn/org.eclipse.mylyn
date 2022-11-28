/*******************************************************************************
 * Copyright (c) 2022 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.core;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.monitor.ui.IMonitoredWindow;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.ui.AbstractUserInteractionMonitor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * @since 3.26
 */
public abstract class AbstractContextInteractionMonitor extends AbstractUserInteractionMonitor {

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part.getSite() != null && part.getSite().getWorkbenchWindow() != null) {
			IWorkbenchWindow window = part.getSite().getWorkbenchWindow();
			if (window instanceof IMonitoredWindow && !((IMonitoredWindow) window).isMonitored()) {
				return;
			}
		}
		if (selection == null || selection.isEmpty()) {
			return;
		}
		if (!ContextCore.getContextManager().isContextActive()) {
			handleWorkbenchPartSelection(part, selection, false);
		} else {
			handleWorkbenchPartSelection(part, selection, true);
		}
	}

	/**
	 * Intended to be called back by subclasses.
	 */
	@Override
	protected void handleNavigation(IWorkbenchPart part, Object targetElement, String kind,
			boolean contributeToContext) {
		handleNavigation(part.getSite().getId(), targetElement, kind, contributeToContext);
	}

	/**
	 * Intended to be called back by subclasses. *
	 *
	 * @since 3.1
	 */
	@Override
	protected void handleNavigation(String partId, Object targetElement, String kind, boolean contributeToContext) {
		AbstractContextStructureBridge adapter = ContextCore.getStructureBridge(targetElement);
		if (adapter.getContentType() != null) {
			String handleIdentifier = adapter.getHandleIdentifier(targetElement);
			InteractionEvent navigationEvent = new InteractionEvent(InteractionEvent.Kind.SELECTION,
					adapter.getContentType(), handleIdentifier, partId, kind);
			if (handleIdentifier != null && contributeToContext) {
				ContextCore.getContextManager().processInteractionEvent(navigationEvent);
			}
			MonitorUiPlugin.getDefault().notifyInteractionObserved(navigationEvent);
		}
	}

	/**
	 * Intended to be called back by subclasses.
	 *
	 * @since 3.1
	 */
	@Override
	protected void handleElementEdit(String partId, Object selectedElement, boolean contributeToContext) {
		if (selectedElement == null) {
			return;
		}
		AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(selectedElement);
		String handleIdentifier = bridge.getHandleIdentifier(selectedElement);
		InteractionEvent editEvent = new InteractionEvent(InteractionEvent.Kind.EDIT, bridge.getContentType(),
				handleIdentifier, partId);
		if (handleIdentifier != null && contributeToContext) {
			ContextCore.getContextManager().processInteractionEvent(editEvent);
		}
		MonitorUiPlugin.getDefault().notifyInteractionObserved(editEvent);
	}

	/**
	 * Intended to be called back by subclasses. *
	 *
	 * @since 3.1
	 */
	@Override
	protected InteractionEvent handleElementSelection(String partId, Object selectedElement,
			boolean contributeToContext) {
		if (selectedElement == null || selectedElement.equals(lastSelectedElement)) {
			return null;
		}
		AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(selectedElement);
		String handleIdentifier = bridge.getHandleIdentifier(selectedElement);
		InteractionEvent selectionEvent;
		if (bridge.getContentType() != null) {
			selectionEvent = new InteractionEvent(InteractionEvent.Kind.SELECTION, bridge.getContentType(),
					handleIdentifier, partId);
		} else {
			selectionEvent = new InteractionEvent(InteractionEvent.Kind.SELECTION, null, null, partId);
		}
		if (handleIdentifier != null && contributeToContext) {
			ContextCore.getContextManager().processInteractionEvent(selectionEvent);
		}
		MonitorUiPlugin.getDefault().notifyInteractionObserved(selectionEvent);
		return selectionEvent;
	}

	/**
	 * Intended to be called back by subclasses.
	 */
	@Override
	protected InteractionEvent handleElementSelection(IWorkbenchPart part, Object selectedElement,
			boolean contributeToContext) {
		return handleElementSelection(part.getSite().getId(), selectedElement, contributeToContext);
	}

	/**
	 * Intended to be called back by subclasses.
	 */
	@Override
	protected void handleElementEdit(IWorkbenchPart part, Object selectedElement, boolean contributeToContext) {
		handleElementEdit(part.getSite().getId(), selectedElement, contributeToContext);
	}

}
