/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.state;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.ContextAwareEditorInput;
import org.eclipse.mylyn.context.ui.ContextUi;
import org.eclipse.mylyn.context.ui.IContextAwareEditor;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.IContextUiPreferenceContstants;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.IWorkbenchConstants;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchPage;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class EditorStateParticipant extends ContextStateParticipant {

	public static final String MEMENTO_EDITORS = "org.eclipse.mylyn.context.ui.editors"; //$NON-NLS-1$

	private static final String KEY_MONITORED_WINDOW_OPEN_EDITORS = "MonitoredWindowOpenEditors"; //$NON-NLS-1$

	private static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$

	private static final String ATTRIBUTE_NUMER = "number"; //$NON-NLS-1$

	private static final String ATTRIBUTE_IS_LAUNCHING = "isLaunching"; //$NON-NLS-1$

	private static final String ATTRIBUTE_IS_ACTIVE = "isActive"; //$NON-NLS-1$

	private boolean previousCloseEditorsSetting = Workbench.getInstance()
			.getPreferenceStore()
			.getBoolean(IPreferenceConstants.REUSE_EDITORS_BOOLEAN);

	private boolean enabled;

	public EditorStateParticipant() {
		this.enabled = true;
	}

	@Override
	public void saveDefaultState(ContextState memento) {
		// save platform preference setting
		Workbench workbench = (Workbench) PlatformUI.getWorkbench();
		previousCloseEditorsSetting = workbench.getPreferenceStore().getBoolean(
				IPreferenceConstants.REUSE_EDITORS_BOOLEAN);
		workbench.getPreferenceStore().setValue(IPreferenceConstants.REUSE_EDITORS_BOOLEAN, false);
	}

	@Override
	public void restoreState(ContextState state) {
		if (Workbench.getInstance().isStarting()) {
			return;
		}

		boolean wasPaused = ContextCore.getContextManager().isContextCapturePaused();
		try {
			if (!wasPaused) {
				ContextCore.getContextManager().setContextCapturePaused(true);
			}

			// restore editors from memento
			String mementoString = null;
			IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			try {
				IMemento memento = state.getMemento(MEMENTO_EDITORS);
				if (memento != null) {
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

			// refresh window
			activeWindow.setActivePage(activeWindow.getActivePage());

			// open last active context node, guarantees that something is opened even if no editor memento is available
			IInteractionElement activeNode = state.getContext().getActiveNode();
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
			int windowNumber = getNumber(window);
			if (window.getClass().getCanonicalName().equals(windowToRestoreClassName)
					&& windowNumber == windowToRestorenumber) {
				return (WorkbenchPage) window.getActivePage();
			}
		}

		// we don't have a good match here, try to make an educated guess
		Boolean isActive = memento.getBoolean(ATTRIBUTE_IS_ACTIVE);
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

	@Override
	public void saveState(ContextState state, boolean allowModifications) {
		if (PlatformUI.getWorkbench().isClosing()) {
			return;
		}

		if (allowModifications) {
			closeContextAwareEditors(state.getContextHandle());
		}

		IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchWindow launchingWindow = MonitorUi.getLaunchingWorkbenchWindow();
		Set<IWorkbenchWindow> monitoredWindows = MonitorUi.getMonitoredWindows();

		try {
			IMemento rootMemento = state.createMemento(MEMENTO_EDITORS);
			for (IWorkbenchWindow window : monitoredWindows) {
				IMemento memento = rootMemento.createChild(KEY_MONITORED_WINDOW_OPEN_EDITORS);

				memento.putString(ATTRIBUTE_CLASS, window.getClass().getCanonicalName());
				int number = getNumber(window);
				memento.putInteger(ATTRIBUTE_NUMER, number);
				memento.putBoolean(ATTRIBUTE_IS_LAUNCHING, window == launchingWindow);
				memento.putBoolean(ATTRIBUTE_IS_ACTIVE, window == activeWindow);
				saveEditors_e_3_x((WorkbenchPage) window.getActivePage(), memento);
			}
		} catch (Exception e) {
			// FIXME fall back to workbench API
		}
	}

	private void saveEditors_e_3_x(WorkbenchPage page, IMemento memento) throws Exception {
		Method getEditorManagerMethod = WorkbenchPage.class.getDeclaredMethod("getEditorManager");
		Object editorManager = getEditorManagerMethod.invoke(page);

		Method getEditorsMethod = editorManager.getClass().getDeclaredMethod("saveState", IMemento.class);
		getEditorsMethod.invoke(editorManager, memento);
	}

	private int getNumber(IWorkbenchWindow window) {
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			if (windows[i] == window) {
				return i;
			}
		}
		return 0;
	}

	@Override
	public void restoreDefaultState(ContextState memento) {
		Workbench.getInstance()
				.getPreferenceStore()
				.setValue(IPreferenceConstants.REUSE_EDITORS_BOOLEAN, previousCloseEditorsSetting);
		closeAllEditors();
	}

	@Override
	public void clearState(String contextHandle, boolean activeContext) {
//		if (activeContext) {
//			closeContextAwareEditors(contextHandle);
//		}
//		Workbench.getInstance()
//				.getPreferenceStore()
//				.setValue(IPreferenceConstants.REUSE_EDITORS_BOOLEAN, previousCloseEditorsSetting);
//		if (activeContext) {
//			closeAllEditors();
//		}
		if (activeContext) {
			closeAllEditors();
		}
	}

	/**
	 * HACK: will fail to restore different parts with same name
	 */
	private void restoreEditors(WorkbenchPage page, IMemento memento, boolean isActiveWindow) {
		final ArrayList<?> visibleEditors = new ArrayList<Object>(5);
		final IEditorReference activeEditor[] = new IEditorReference[1];
		final MultiStatus result = new MultiStatus(PlatformUI.PLUGIN_ID, IStatus.OK, "", null); //$NON-NLS-1$

		try {
			IMemento[] editorMementos = memento.getChildren(IWorkbenchConstants.TAG_EDITOR);
			Set<IMemento> editorMementoSet = new LinkedHashSet<IMemento>();
			editorMementoSet.addAll(Arrays.asList(editorMementos));
			// HACK: same parts could have different editors

			try {
				restoreEditors_e_3_x(page, visibleEditors, activeEditor, result, editorMementoSet);
			} catch (Exception e) {
				// FIXME fall back to workbench API
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

	private void restoreEditors_e_3_x(WorkbenchPage page, final ArrayList<?> visibleEditors,
			final IEditorReference[] activeEditor, final MultiStatus result, Set<IMemento> editorMementoSet)
			throws Exception {
		Method getEditorManagerMethod = WorkbenchPage.class.getDeclaredMethod("getEditorManager");
		Object editorManager = getEditorManagerMethod.invoke(page);

		Method getEditorsMethod = editorManager.getClass().getDeclaredMethod("getEditors");

		List<IEditorReference> alreadyVisibleEditors = Arrays.asList((IEditorReference[]) getEditorsMethod.invoke(editorManager));
		Set<String> restoredPartNames = new HashSet<String>();
		for (IEditorReference editorReference : alreadyVisibleEditors) {
			restoredPartNames.add(editorReference.getPartName());
		}

		Method restoreEditorStateMethod = editorManager.getClass().getDeclaredMethod("restoreEditorState",
				IMemento.class, ArrayList.class, IEditorReference[].class, MultiStatus.class);

		for (IMemento editorMemento : editorMementoSet) {
			String partName = editorMemento.getString(IWorkbenchConstants.TAG_PART_NAME);
			if (!restoredPartNames.contains(partName)) {
				restoreEditorStateMethod.invoke(editorManager, editorMemento, visibleEditors, activeEditor, result);
			} else {
				restoredPartNames.add(partName);
			}
		}

		Method setVisibleEditorMethod = editorManager.getClass().getDeclaredMethod("setVisibleEditor",
				IEditorReference.class, boolean.class);
		for (int i = 0; i < visibleEditors.size(); i++) {
			setVisibleEditorMethod.invoke(editorManager, visibleEditors.get(i), false);
		}
	}

	public void closeContextAwareEditors(String contextHandle) {
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
								if (shouldForceClose(input, contextHandle)) {
									toClose.add(reference);
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

	private boolean shouldForceClose(final IEditorInput input, final String contextHandle) {
		final AtomicBoolean result = new AtomicBoolean();
		SafeRunnable.run(new ISafeRunnable() {
			public void run() throws Exception {
				ContextAwareEditorInput inputContext = (ContextAwareEditorInput) input.getAdapter(ContextAwareEditorInput.class);
				result.set(inputContext != null && inputContext.forceClose(contextHandle));
			}

			public void handleException(Throwable e) {
				StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
						"Failed to verify editor status", e)); //$NON-NLS-1$
			}
		});
		return result.get();
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

	@Override
	public boolean isEnabled() {
		return enabled
				&& ContextUiPlugin.getDefault()
						.getPreferenceStore()
						.getBoolean(IContextUiPreferenceContstants.AUTO_MANAGE_EDITORS);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
