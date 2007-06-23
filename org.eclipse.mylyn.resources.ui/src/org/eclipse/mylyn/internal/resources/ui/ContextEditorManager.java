/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.resources.ui;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.mylyn.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiPrefContstants;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.resources.ResourcesUiBridgePlugin;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.editors.NewTaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
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
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.WorkbenchPage;

/**
 * @author Mik Kersten
 */
public class ContextEditorManager implements IInteractionContextListener {

	private static final String PREFS_PREFIX = "editors.task.";

	private static final String KEY_CONTEXT_EDITORS = "ContextOpenEditors";

	private boolean previousCloseEditorsSetting = Workbench.getInstance().getPreferenceStore().getBoolean(
			IPreferenceConstants.REUSE_EDITORS_BOOLEAN);

	public void contextActivated(IInteractionContext context) {
		if (!Workbench.getInstance().isStarting()
				&& ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
						ContextUiPrefContstants.AUTO_MANAGE_EDITORS)) {
			Workbench workbench = (Workbench) PlatformUI.getWorkbench();
			previousCloseEditorsSetting = workbench.getPreferenceStore().getBoolean(
					IPreferenceConstants.REUSE_EDITORS_BOOLEAN);
			workbench.getPreferenceStore().setValue(IPreferenceConstants.REUSE_EDITORS_BOOLEAN, false);
			boolean wasPaused = ContextCorePlugin.getContextManager().isContextCapturePaused();
			try {
				if (!wasPaused) {
					ContextCorePlugin.getContextManager().setContextCapturePaused(true);
				}
				WorkbenchPage page = (WorkbenchPage) workbench.getActiveWorkbenchWindow().getActivePage();

				String mementoString = null;
				AbstractTask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(context.getHandleIdentifier());
				if (task != null) {
					try {
						mementoString = ResourcesUiBridgePlugin.getDefault().getPreferenceStore().getString(
								PREFS_PREFIX + task.getHandleIdentifier());
						if (mementoString != null && !mementoString.trim().equals("")) {
							IMemento memento = XMLMemento.createReadRoot(new StringReader(mementoString));
							if (memento != null) {
								restoreEditors(page, memento);
							}
						}
					} catch (Exception e) {
						StatusHandler.log(e, "Could not restore all editors, memento: " + mementoString + ".");
					}
				}
				IInteractionElement activeNode = context.getActiveNode();
				if (activeNode != null) {
					ContextUiPlugin.getDefault().getUiBridge(activeNode.getContentType()).open(activeNode);
				}
			} catch (Exception e) {
				StatusHandler.fail(e, "failed to open editors on activation", false);
			} finally {
				ContextCorePlugin.getContextManager().setContextCapturePaused(false);
			}
		}
	}

	public void contextDeactivated(IInteractionContext context) {
		if (!PlatformUI.getWorkbench().isClosing()
				&& ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
						ContextUiPrefContstants.AUTO_MANAGE_EDITORS)) {
			closeAllButActiveTaskEditor(context.getHandleIdentifier());
			
			XMLMemento memento = XMLMemento.createWriteRoot(KEY_CONTEXT_EDITORS);
			((WorkbenchPage) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()).getEditorManager()
					.saveState(memento);

			AbstractTask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(context.getHandleIdentifier());
			if (task != null) {
				// TODO: avoid storing with preferences due to bloat?
				StringWriter writer = new StringWriter();
				try {
					memento.save(writer);
					ResourcesUiBridgePlugin.getDefault().getPreferenceStore().setValue(
							PREFS_PREFIX + task.getHandleIdentifier(), writer.getBuffer().toString());
				} catch (IOException e) {
					StatusHandler.fail(e, "Could not store editor state", false);
				}

				Workbench.getInstance().getPreferenceStore().setValue(IPreferenceConstants.REUSE_EDITORS_BOOLEAN,
						previousCloseEditorsSetting);
			}
			closeAllEditors();
		}
	}

	public void contextCleared(IInteractionContext context) {
		if (context == null) {
			return;
		}
		closeAllButActiveTaskEditor(context.getHandleIdentifier());
		AbstractTask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(context.getHandleIdentifier());
		XMLMemento memento = XMLMemento.createWriteRoot(KEY_CONTEXT_EDITORS);
		
		if (task != null) {
			// TODO: avoid storing with preferneces due to bloat?
			StringWriter writer = new StringWriter();
			try {
				memento.save(writer);
				ResourcesUiBridgePlugin.getDefault().getPreferenceStore().setValue(
						PREFS_PREFIX + task.getHandleIdentifier(), writer.getBuffer().toString());
			} catch (IOException e) {
				StatusHandler.fail(e, "Could not store editor state", false);
			}

			Workbench.getInstance().getPreferenceStore().setValue(IPreferenceConstants.REUSE_EDITORS_BOOLEAN,
					previousCloseEditorsSetting);
		}
		closeAllEditors();
	}
	
	/**
	 * HACK: uses reflection for 3.2 compatibility. HACK: will fail to restore
	 * different parts with same name
	 */
	@SuppressWarnings("unchecked")
	private void restoreEditors(WorkbenchPage page, IMemento memento) {
		EditorManager editorManager = page.getEditorManager();
		final ArrayList visibleEditors = new ArrayList(5);
		final IEditorReference activeEditor[] = new IEditorReference[1];
		final MultiStatus result = new MultiStatus(PlatformUI.PLUGIN_ID, IStatus.OK,
				WorkbenchMessages.EditorManager_problemsRestoringEditors, null);

		// HACK: using reflection to gain accessibility
//		Class<?> clazz = editorManager.getClass();
		try {
//			Method method = clazz.getDeclaredMethod("restoreEditorState", IMemento.class, ArrayList.class,
//					IEditorReference[].class, MultiStatus.class);
//			method.setAccessible(true);

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
					// method.invoke(editorManager, new Object[] { editorMemento, visibleEditors,
					// activeEditor, result });
				} else {
					restoredPartNames.add(partName);
				}
			}

			for (int i = 0; i < visibleEditors.size(); i++) {
				editorManager.setVisibleEditor((IEditorReference) visibleEditors.get(i), false);
			}

			if (activeEditor[0] != null) {
				IWorkbenchPart editor = activeEditor[0].getPart(true);
				if (editor != null) {
					page.activate(editor);
				}
			}
		} catch (Exception e) {
			StatusHandler.fail(e, "Could not restore editors", false);
		}
	}

	public void closeAllButActiveTaskEditor(String taskHandle) {
		try {
			if (PlatformUI.getWorkbench().isClosing()) {
				return;
			}
			for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
				IWorkbenchPage page = window.getActivePage();
				if (page != null) {
					IEditorReference[] references = page.getEditorReferences();
					List<IEditorReference> toClose = new ArrayList<IEditorReference>();
					for (int i = 0; i < references.length; i++) {
						if (canClose(references[i])) {
							
							IEditorPart part = references[i].getEditor(false);
							if (part instanceof TaskEditor) {
								try {
									IEditorInput input = references[i].getEditorInput();
									if (input instanceof TaskEditorInput) {
										AbstractTask task = ((TaskEditorInput)input).getTask();
										if (task != null && task.getHandleIdentifier().equals(taskHandle)) {
											// do not close
										} else {
											toClose.add(references[i]);
										}
									}
								} catch (PartInitException e) {
									// ignore
								}
							}
						}
					}
					page.closeEditors(toClose.toArray(new IEditorReference[toClose.size()]), true);
				}
			}
		} catch (Throwable t) {
			StatusHandler.fail(t, "Could not auto close editor.", false);
		}
	}
	
	public void closeAllEditors() {
		try {
			if (PlatformUI.getWorkbench().isClosing()) {
				return;
			}
			for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
				IWorkbenchPage page = window.getActivePage();
				if (page != null) {
					IEditorReference[] references = page.getEditorReferences();
					List<IEditorReference> toClose = new ArrayList<IEditorReference>();
					for (int i = 0; i < references.length; i++) {
						if (canClose(references[i]) && !isUnsubmittedTaskEditor(references[i])) {
							toClose.add(references[i]);
						}
					}
					page.closeEditors(toClose.toArray(new IEditorReference[toClose.size()]), true);
				}
			}
		} catch (Throwable t) {
			StatusHandler.fail(t, "Could not auto close editor.", false);
		}
	}

	private boolean canClose(IEditorReference editorReference) {
		IEditorPart editor = editorReference.getEditor(false);
		if (editor instanceof IContextAwareEditor) {
			return ((IContextAwareEditor) editor).canClose();
		}
		return true;
	}
	
	private boolean isUnsubmittedTaskEditor(IEditorReference editorReference) {
		IEditorPart part = editorReference.getEditor(false);
		if (part instanceof TaskEditor) {
			try {
				IEditorInput input = editorReference.getEditorInput();
				if (input instanceof NewTaskEditorInput) {
					return true;
				}
			} catch (PartInitException e) {
				// ignore
			}
		}
		return false;
	}

	public void interestChanged(List<IInteractionElement> elements) {
		for (IInteractionElement element : elements) {
			if (ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
					ContextUiPrefContstants.AUTO_MANAGE_EDITORS)) {
				if (!element.getInterest().isInteresting()) {
					AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(
							element.getContentType());
					if (bridge.isDocument(element.getHandleIdentifier())) {
						AbstractContextUiBridge uiBridge = ContextUiPlugin.getDefault().getUiBridge(
								element.getContentType());
						uiBridge.close(element);
					}
				}
			}
		}
	}

	public void elementDeleted(IInteractionElement node) {
		// ignore
	}

	public void landmarkAdded(IInteractionElement node) {
		// ignore
	}

	public void landmarkRemoved(IInteractionElement node) {
		// ignore
	}

	public void relationsChanged(IInteractionElement node) {
		// ignore
	}
}
