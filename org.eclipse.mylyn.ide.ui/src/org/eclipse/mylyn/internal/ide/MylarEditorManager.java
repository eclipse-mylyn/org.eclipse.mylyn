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

package org.eclipse.mylar.internal.ide;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.editors.TaskEditorInput;
import org.eclipse.mylar.internal.ui.MylarUiPrefContstants;
import org.eclipse.mylar.provisional.core.IMylarContext;
import org.eclipse.mylar.provisional.core.IMylarContextListener;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.IMylarStructureBridge;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.ui.IMylarUiBridge;
import org.eclipse.mylar.provisional.ui.MylarUiPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class MylarEditorManager implements IMylarContextListener {

	private boolean previousCloseEditorsSetting = Workbench.getInstance().getPreferenceStore().getBoolean(IPreferenceConstants.REUSE_EDITORS_BOOLEAN);
	
	public void contextActivated(IMylarContext context) {
		if (MylarUiPlugin.getPrefs().getBoolean(MylarUiPrefContstants.AUTO_MANAGE_EDITORS)) {
			previousCloseEditorsSetting = Workbench.getInstance().getPreferenceStore().getBoolean(IPreferenceConstants.REUSE_EDITORS_BOOLEAN);
			Workbench.getInstance().getPreferenceStore().setValue(IPreferenceConstants.REUSE_EDITORS_BOOLEAN, false);
			
			Workbench workbench = (Workbench) PlatformUI.getWorkbench();
			try {
				MylarPlugin.getContextManager().setContextCapturePaused(true);
				for (IMylarUiBridge bridge : MylarUiPlugin.getDefault().getUiBridges()) {
					bridge.setContextCapturePaused(true);
				}
				workbench.largeUpdateStart();

				List<IMylarElement> documents = MylarPlugin.getContextManager().getInterestingDocuments();
				int opened = 0;
				int threshold = MylarUiPlugin.getPrefs().getInt(MylarUiPrefContstants.AUTO_MANAGE_EDITORS_OPEN_NUM);
				for (Iterator iter = documents.iterator(); iter.hasNext() && opened < threshold - 1; opened++) {
					IMylarElement document = (IMylarElement) iter.next();
					IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridge(document.getContentType());
					bridge.restoreEditor(document);
					opened++;
				}
				IMylarElement activeNode = context.getActiveNode();
				if (activeNode != null) {
					MylarUiPlugin.getDefault().getUiBridge(activeNode.getContentType()).open(activeNode);
				}
			} catch (Exception e) {
				MylarStatusHandler.fail(e, "failed to open editors on activation", false);
			} finally {
				MylarPlugin.getContextManager().setContextCapturePaused(false);
				for (IMylarUiBridge bridge : MylarUiPlugin.getDefault().getUiBridges()) {
					bridge.setContextCapturePaused(false);
				}
				workbench.largeUpdateEnd();
			}
		}
	}

	public void contextDeactivated(IMylarContext context) {
		if (MylarUiPlugin.getPrefs().getBoolean(MylarUiPrefContstants.AUTO_MANAGE_EDITORS)) {
			Workbench.getInstance().getPreferenceStore().setValue(IPreferenceConstants.REUSE_EDITORS_BOOLEAN, previousCloseEditorsSetting);
			closeAllEditors();
		}
	}

	public void closeAllEditors() {
		try {     
			if (PlatformUI.getWorkbench().isClosing()) {
				return;  
			}
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page != null) {
				IEditorReference[] references = page.getEditorReferences();
				List<IEditorReference> toClose = new ArrayList<IEditorReference>();
				for (int i = 0; i < references.length; i++) {
					if (!isActiveTaskEditor(references[i])) {
						toClose.add(references[i]);
					}
				}
				page.closeEditors(toClose.toArray(new IEditorReference[toClose.size()]), true);
			}
		} catch (Throwable t) {
			MylarStatusHandler.fail(t, "Could not auto close editor.", false);
		}
	}

	private boolean isActiveTaskEditor(IEditorReference editorReference) {
		ITask activeTask = MylarTaskListPlugin.getTaskListManager().getTaskList().getActiveTask();
		try {
			IEditorInput input = editorReference.getEditorInput();
			if (input instanceof TaskEditorInput) {
				TaskEditorInput taskEditorInput = (TaskEditorInput)input;
				if (activeTask != null && taskEditorInput.getTask() != null
					&& taskEditorInput.getTask().getHandleIdentifier().equals(activeTask.getHandleIdentifier())) {
					return true;
				}
			}
		} catch (PartInitException e) {
			// ignore
		}
		return false;
	}

	public void presentationSettingsChanging(UpdateKind kind) {
		// ignore
	}

	public void presentationSettingsChanged(UpdateKind kind) {
		// ignore
	}

	public void interestChanged(IMylarElement element) {
		if (MylarUiPlugin.getPrefs().getBoolean(MylarUiPrefContstants.AUTO_MANAGE_EDITORS)) {
			if (!element.getInterest().isInteresting()) {
				IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(element.getContentType());
				if (bridge.isDocument(element.getHandleIdentifier())) {
					MylarUiPlugin.getDefault().getUiBridge(element.getContentType()).close(element);
				}
			}
		}
	}

	public void interestChanged(List<IMylarElement> elements) {
		for (IMylarElement element : elements) {
			interestChanged(element);
		}
	}

	public void nodeDeleted(IMylarElement node) {
		// ignore
	}

	public void landmarkAdded(IMylarElement node) {
		// ignore
	}

	public void landmarkRemoved(IMylarElement node) {
		// ignore
	}

	public void edgesChanged(IMylarElement node) {
		// ignore
	}

}
