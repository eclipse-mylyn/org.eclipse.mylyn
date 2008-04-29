/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.monitor.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.monitor.ui.IMonitoredWindow;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.InteractionEvent.Kind;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Self-registering on construction. Encapsulates users' interaction with the context model.
 * 
 * @author Mik Kersten
 * @author Shawn Minto
 * @since 2.0
 */
public abstract class AbstractUserInteractionMonitor implements ISelectionListener {

	protected Object lastSelectedElement = null;

	/**
	 * Requires workbench to be active.
	 */
	public AbstractUserInteractionMonitor() {
		try {
			MonitorUiPlugin.getDefault().addWindowPostSelectionListener(this);
		} catch (NullPointerException e) {
			StatusHandler.log(new Status(IStatus.WARNING, MonitorUiPlugin.ID_PLUGIN,
					"Monitors can not be instantiated until the workbench is active", e));
		}
	}

	public void dispose() {
		try {
			MonitorUiPlugin.getDefault().removeWindowPostSelectionListener(this);
		} catch (NullPointerException e) {
			StatusHandler.log(new Status(IStatus.WARNING, MonitorUiPlugin.ID_PLUGIN, "Could not dispose monitor", e));
		}
	}

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

	protected abstract void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection,
			boolean contributeToContext);

	/**
	 * Intended to be called back by subclasses.
	 */
	protected InteractionEvent handleElementSelection(IWorkbenchPart part, Object selectedElement,
			boolean contributeToContext) {
		if (selectedElement == null || selectedElement.equals(lastSelectedElement)) {
			return null;
		}
		AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(selectedElement);
		String handleIdentifier = bridge.getHandleIdentifier(selectedElement);
		InteractionEvent selectionEvent;
		if (bridge.getContentType() != null) {
			selectionEvent = new InteractionEvent(InteractionEvent.Kind.SELECTION, bridge.getContentType(),
					handleIdentifier, part.getSite().getId());
		} else {
			selectionEvent = new InteractionEvent(InteractionEvent.Kind.SELECTION, null, null, part.getSite().getId());
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
	protected void handleElementEdit(IWorkbenchPart part, Object selectedElement, boolean contributeToContext) {
		if (selectedElement == null) {
			return;
		}
		AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(selectedElement);
		String handleIdentifier = bridge.getHandleIdentifier(selectedElement);
		InteractionEvent editEvent = new InteractionEvent(InteractionEvent.Kind.EDIT, bridge.getContentType(),
				handleIdentifier, part.getSite().getId());
		if (handleIdentifier != null && contributeToContext) {
			ContextCore.getContextManager().processInteractionEvent(editEvent);
		}
		MonitorUiPlugin.getDefault().notifyInteractionObserved(editEvent);
	}

	/**
	 * Intended to be called back by subclasses.
	 */
	protected void handleNavigation(IWorkbenchPart part, Object targetElement, String kind, boolean contributeToContext) {
		AbstractContextStructureBridge adapter = ContextCore.getStructureBridge(targetElement);
		if (adapter.getContentType() != null) {
			String handleIdentifier = adapter.getHandleIdentifier(targetElement);
			InteractionEvent navigationEvent = new InteractionEvent(InteractionEvent.Kind.SELECTION,
					adapter.getContentType(), handleIdentifier, part.getSite().getId(), kind);
			if (handleIdentifier != null && contributeToContext) {
				ContextCore.getContextManager().processInteractionEvent(navigationEvent);
			}
			MonitorUiPlugin.getDefault().notifyInteractionObserved(navigationEvent);
		}
	}

	public Kind getEventKind() {
		return InteractionEvent.Kind.SELECTION;
	}
}
