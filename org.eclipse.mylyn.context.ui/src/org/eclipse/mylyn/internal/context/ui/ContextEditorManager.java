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

package org.eclipse.mylyn.internal.context.ui;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.mylyn.context.ui.ContextUi;
import org.eclipse.mylyn.context.ui.IContextAwareEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskMigrator;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.internal.EditorManager;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.IWorkbenchConstants;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class ContextEditorManager extends AbstractContextListener {

	private static final String PREFS_PREFIX = "editors.task."; //$NON-NLS-1$

	private static final String KEY_CONTEXT_EDITORS = "ContextOpenEditors"; //$NON-NLS-1$

	private static final String KEY_MONITORED_WINDOW_OPEN_EDITORS = "MonitoredWindowOpenEditors"; //$NON-NLS-1$

	private static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$

	private static final String ATTRIBUTE_NUMER = "number"; //$NON-NLS-1$

	private static final String ATTRIBUTE_IS_LAUNCHING = "isLaunching"; //$NON-NLS-1$

	private static final String ATTRIBUTE_IS_ACTIVE = "isActive"; //$NON-NLS-1$

	private boolean previousCloseEditorsSetting = Workbench.getInstance().getPreferenceStore().getBoolean(
			IPreferenceConstants.REUSE_EDITORS_BOOLEAN);

	private final IPreferenceStore preferenceStore;

	public ContextEditorManager() {
		preferenceStore = new ScopedPreferenceStore(new InstanceScope(), "org.eclipse.mylyn.resources.ui"); //$NON-NLS-1$
	}

	@Override
	public void contextChanged(ContextChangeEvent event) {
		switch (event.getEventKind()) {
		case ACTIVATED:
			openEditorsFromMemento(event.getContext());
			break;
		case DEACTIVATED:
			closeEditorsAndSaveMemento(event.getContext());
			break;
		case INTEREST_CHANGED:
			for (IInteractionElement element : event.getElements()) {
				closeEditor(element, false);
			}
			break;
		case ELEMENTS_DELETED:
			for (IInteractionElement element : event.getElements()) {
				closeEditor(element, true);
			}
			break;
		case CLEARED:
			// use the handle since the context is null when it is cleared
			// bug 255588
			clearEditorMemento(event.getContextHandle(), event.isActiveContext());
			break;

		}
	}

	public void openEditorsFromMemento(IInteractionContext context) {
		if (!Workbench.getInstance().isStarting()
				&& ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
						IContextUiPreferenceContstants.AUTO_MANAGE_EDITORS) && !TaskMigrator.isActive()) {
			Workbench workbench = (Workbench) PlatformUI.getWorkbench();
			previousCloseEditorsSetting = workbench.getPreferenceStore().getBoolean(
					IPreferenceConstants.REUSE_EDITORS_BOOLEAN);
			workbench.getPreferenceStore().setValue(IPreferenceConstants.REUSE_EDITORS_BOOLEAN, false);
			boolean wasPaused = ContextCore.getContextManager().isContextCapturePaused();
			try {
				if (!wasPaused) {
					ContextCore.getContextManager().setContextCapturePaused(true);
				}
				String mementoString = null;
				// TODO change where memento is stored
				IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				try {
					mementoString = readEditorMemento(context);
					if (mementoString != null && !mementoString.trim().equals("")) { //$NON-NLS-1$
						IMemento memento = XMLMemento.createReadRoot(new StringReader(mementoString));
						IMemento[] children = memento.getChildren(KEY_MONITORED_WINDOW_OPEN_EDITORS);
						if (children.length > 0) {
							// This code supports restore from multiple windows
							for (IMemento child : children) {
								WorkbenchPage page = getWorkbenchPageForMemento(child, activeWindow);
								if (child != null && page != null) {
									restoreEditors(page, child, page.getWorkbenchWindow() == activeWindow);
								}
							}
						} else {
							// This code is for supporting the old editor management - only the active window
							WorkbenchPage page = (WorkbenchPage) activeWindow.getActivePage();
							if (memento != null) {
								restoreEditors(page, memento, true);
							}
						}
					}
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
							"Could not restore all editors, memento: \"" + mementoString + "\"", e)); //$NON-NLS-1$ //$NON-NLS-2$
				}
				activeWindow.setActivePage(activeWindow.getActivePage());
				IInteractionElement activeNode = context.getActiveNode();
				if (activeNode != null) {
					ContextUi.getUiBridge(activeNode.getContentType()).open(activeNode);
				}
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
						"Failed to open editors on activation", e)); //$NON-NLS-1$
			} finally {
				ContextCore.getContextManager().setContextCapturePaused(false);
			}
		}
	}

	private WorkbenchPage getWorkbenchPageForMemento(IMemento memento, IWorkbenchWindow activeWindow) {

		String windowToRestoreClassName = memento.getString(ATTRIBUTE_CLASS);
		if (windowToRestoreClassName == null) {
			windowToRestoreClassName = ""; //$NON-NLS-1$
		}
		Integer windowToRestorenumber = memento.getInteger(ATTRIBUTE_NUMER);
		if (windowToRestorenumber == null) {
			windowToRestorenumber = 0;
		}

		// try to match the open windows to the one that we want to restore
		Set<IWorkbenchWindow> monitoredWindows = MonitorUi.getMonitoredWindows();
		for (IWorkbenchWindow window : monitoredWindows) {
			int windowNumber = 0;
			if (window instanceof WorkbenchWindow) {
				windowNumber = ((WorkbenchWindow) window).getNumber();
			}
			if (window.getClass().getCanonicalName().equals(windowToRestoreClassName)
					&& windowNumber == windowToRestorenumber) {
				return (WorkbenchPage) window.getActivePage();
			}
		}

		// we don't have a good match here, try to make an educated guess
		// TODO e3.4 replace by memento.getBoolean()
		Boolean isActive = Boolean.valueOf(memento.getString(ATTRIBUTE_IS_ACTIVE));
		if (isActive == null) {
			isActive = false;
		}

		// both of these defaulting to true should ensure that all editors are opened even if their previous editor is not around
		boolean shouldRestoreUnknownWindowToActive = true; // TODO could add a preference here
		boolean shouldRestoreActiveWindowToActive = true; // TODO could add a preference here

		if (isActive && shouldRestoreActiveWindowToActive) {
			// if the window that we are trying to restore was the active window, restore it to the active window
			return (WorkbenchPage) activeWindow.getActivePage();
		}

		if (shouldRestoreUnknownWindowToActive) {
			// we can't find a good window, so restore it to the active one
			return (WorkbenchPage) activeWindow.getActivePage();
		}

		if (shouldRestoreActiveWindowToActive && shouldRestoreUnknownWindowToActive) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
					"Unable to find window to restore memento to.", new Exception())); //$NON-NLS-1$
		}

		// we dont have a window that will work, so don't restore the editors
		// we shouldn't get here if both *WindowToActive booleans are true
		return null;
	}

	private String readEditorMemento(IInteractionContext context) {
		return preferenceStore.getString(PREFS_PREFIX + context.getHandleIdentifier());
	}

	public void closeEditorsAndSaveMemento(IInteractionContext context) {
		if (!PlatformUI.getWorkbench().isClosing()
				&& ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
						IContextUiPreferenceContstants.AUTO_MANAGE_EDITORS) && !TaskMigrator.isActive()) {
			closeAllButActiveTaskEditor(context.getHandleIdentifier());

			XMLMemento rootMemento = XMLMemento.createWriteRoot(KEY_CONTEXT_EDITORS);

			IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchWindow launchingWindow = MonitorUi.getLaunchingWorkbenchWindow();
			Set<IWorkbenchWindow> monitoredWindows = MonitorUi.getMonitoredWindows();

			for (IWorkbenchWindow window : monitoredWindows) {
				IMemento memento = rootMemento.createChild(KEY_MONITORED_WINDOW_OPEN_EDITORS);

				memento.putString(ATTRIBUTE_CLASS, window.getClass().getCanonicalName());
				int number = 0;
				if (window instanceof WorkbenchWindow) {
					number = ((WorkbenchWindow) window).getNumber();
				}
				memento.putInteger(ATTRIBUTE_NUMER, number);
				// TODO e3.4 replace by memento.putBoolean()
				memento.putString(ATTRIBUTE_IS_LAUNCHING, (window == launchingWindow) ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
				memento.putString(ATTRIBUTE_IS_ACTIVE, (window == activeWindow) ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
				((WorkbenchPage) window.getActivePage()).getEditorManager().saveState(memento);
			}
			// TODO: avoid storing with preferences due to bloat?
			StringWriter writer = new StringWriter();
			try {
				rootMemento.save(writer);
				writeEditorMemento(context.getHandleIdentifier(), writer.getBuffer().toString());
			} catch (IOException e) {
				StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Could not store editor state", //$NON-NLS-1$
						e));
			}

			Workbench.getInstance().getPreferenceStore().setValue(IPreferenceConstants.REUSE_EDITORS_BOOLEAN,
					previousCloseEditorsSetting);
			closeAllEditors();
		}
	}

	public void writeEditorMemento(String contextHandle, String memento) {
		preferenceStore.setValue(PREFS_PREFIX + contextHandle, memento);
	}

	public void clearEditorMemento(String contextHandle, boolean closeEditors) {

		if (closeEditors) {
			closeAllButActiveTaskEditor(contextHandle);
		}

		XMLMemento memento = XMLMemento.createWriteRoot(KEY_CONTEXT_EDITORS);

		// TODO: avoid storing with preferences due to bloat?
		StringWriter writer = new StringWriter();
		try {
			memento.save(writer);
			writeEditorMemento(contextHandle, writer.getBuffer().toString());
		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Could not store editor state", e)); //$NON-NLS-1$
		}

		Workbench.getInstance().getPreferenceStore().setValue(IPreferenceConstants.REUSE_EDITORS_BOOLEAN,
				previousCloseEditorsSetting);
		if (closeEditors) {
			closeAllEditors();
		}
	}

	/**
	 * HACK: will fail to restore different parts with same name
	 */
	@SuppressWarnings("unchecked")
	private void restoreEditors(WorkbenchPage page, IMemento memento, boolean isActiveWindow) {
		EditorManager editorManager = page.getEditorManager();
		final ArrayList visibleEditors = new ArrayList(5);
		final IEditorReference activeEditor[] = new IEditorReference[1];
		final MultiStatus result = new MultiStatus(PlatformUI.PLUGIN_ID, IStatus.OK, "", null); //$NON-NLS-1$

		try {
			IMemento[] editorMementos = memento.getChildren(IWorkbenchConstants.TAG_EDITOR);
			Set<IMemento> editorMementoSet = new HashSet<IMemento>();
			editorMementoSet.addAll(Arrays.asList(editorMementos));
			// HACK: same parts could have different editors
			Set<String> restoredPartNames = new HashSet<String>();
			List<IEditorReference> alreadyVisibleEditors = Arrays.asList(editorManager.getEditors());
			for (IEditorReference editorReference : alreadyVisibleEditors) {
				restoredPartNames.add(editorReference.getPartName());
			}
			for (IMemento editorMemento : editorMementoSet) {
				String partName = editorMemento.getString(IWorkbenchConstants.TAG_PART_NAME);
				if (!restoredPartNames.contains(partName)) {
					editorManager.restoreEditorState(editorMemento, visibleEditors, activeEditor, result);
				} else {
					restoredPartNames.add(partName);
				}
			}

			for (int i = 0; i < visibleEditors.size(); i++) {
				editorManager.setVisibleEditor((IEditorReference) visibleEditors.get(i), false);
			}

			if (activeEditor[0] != null && isActiveWindow) {
				IWorkbenchPart editor = activeEditor[0].getPart(true);
				if (editor != null) {
					page.activate(editor);
				}
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Could not restore editors", e)); //$NON-NLS-1$
		}
	}

	public void closeAllButActiveTaskEditor(String taskHandle) {
		try {
			if (PlatformUI.getWorkbench().isClosing()) {
				return;
			}
			for (IWorkbenchWindow window : MonitorUi.getMonitoredWindows()) {
				IWorkbenchPage page = window.getActivePage();
				if (page != null) {
					IEditorReference[] references = page.getEditorReferences();
					List<IEditorReference> toClose = new ArrayList<IEditorReference>();
					for (IEditorReference reference : references) {
						if (canClose(reference)) {
							try {
								IEditorInput input = reference.getEditorInput();
								if (input instanceof TaskEditorInput) {
									ITask task = ((TaskEditorInput) input).getTask();
									if (task != null && task.getHandleIdentifier().equals(taskHandle)) {
										// do not close
									} else {
										toClose.add(reference);
									}
								}
							} catch (PartInitException e) {
								// ignore
							}
						}
					}
					page.closeEditors(toClose.toArray(new IEditorReference[toClose.size()]), true);
				}
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Could not auto close editor", t)); //$NON-NLS-1$
		}
	}

	public void closeAllEditors() {
		try {
			if (PlatformUI.getWorkbench().isClosing()) {
				return;
			}
			for (IWorkbenchWindow window : MonitorUi.getMonitoredWindows()) {
				IWorkbenchPage page = window.getActivePage();
				if (page != null) {
					IEditorReference[] references = page.getEditorReferences();
					List<IEditorReference> toClose = new ArrayList<IEditorReference>();
					for (IEditorReference reference : references) {
						if (canClose(reference)) {
							toClose.add(reference);
						}
					}
					page.closeEditors(toClose.toArray(new IEditorReference[toClose.size()]), true);
				}
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Could not auto close editor", t)); //$NON-NLS-1$
		}
	}

	private boolean canClose(final IEditorReference editorReference) {
		final IEditorPart editor = editorReference.getEditor(false);
		if (editor != null) {
			final boolean[] result = new boolean[1];
			result[0] = true;
			SafeRunnable.run(new ISafeRunnable() {
				public void run() throws Exception {
					if (editor instanceof IContextAwareEditor) {
						result[0] = ((IContextAwareEditor) editor).canClose();
					} else {
						IContextAwareEditor contextAware = (IContextAwareEditor) editor.getAdapter(IContextAwareEditor.class);
						if (contextAware != null) {
							result[0] = contextAware.canClose();
						}
					}
				}

				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
							"Failed to verify editor status", e)); //$NON-NLS-1$
				}
			});
			return result[0];
		}
		return true;
	}

	private void closeEditor(IInteractionElement element, boolean force) {
		if (ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
				IContextUiPreferenceContstants.AUTO_MANAGE_EDITORS)) {
			if (force || !element.getInterest().isInteresting()) {
				AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(element.getContentType());
				if (bridge.isDocument(element.getHandleIdentifier())) {
					AbstractContextUiBridge uiBridge = ContextUi.getUiBridge(element.getContentType());
					uiBridge.close(element);
				}
			}
		}
	}
}
