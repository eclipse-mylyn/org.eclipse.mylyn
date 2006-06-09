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
/*
 * Created on Apr 6, 2005
 */
package org.eclipse.mylar.internal.bugs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTaskEditor;
import org.eclipse.mylar.internal.tasklist.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasklist.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylar.internal.tasklist.ui.editors.RepositoryTaskOutlinePage;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.ui.IMylarUiBridge;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class BugzillaUiBridge implements IMylarUiBridge {

	protected BugzillaContextLabelProvider labelProvider = new BugzillaContextLabelProvider();

	public void open(IMylarElement node) {
		String handle = node.getHandleIdentifier();
		String bugHandle = handle;
		String server = handle.substring(0, handle.indexOf(";"));

		handle = handle.substring(handle.indexOf(";") + 1);
		int next = handle.indexOf(";");

		int bugId;
//		int commentNumer = -1;
		if (next == -1) {
			bugId = Integer.parseInt(handle);
		} else {
			bugId = Integer.parseInt(handle.substring(0, handle.indexOf(";")));
//			commentNumer = Integer.parseInt(handle.substring(handle.indexOf(";") + 1));
			bugHandle = bugHandle.substring(0, next);
		}

		ITask task = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(handle);
		if (task != null) {
			TaskUiUtil.openEditor(task, false);
//			BugzillaTask bugzillaTask = (BugzillaTask) task;
//			bugzillaTask.openTask(commentNumer, true);
		} else {
			String bugUrl = BugzillaRepositoryUtil.getBugUrlWithoutLogin(server, bugId);
			TaskUiUtil.openRepositoryTask(server, "" + bugId, bugUrl);

//			List<BugzillaOpenStructure> openList = new ArrayList<BugzillaOpenStructure>(1);
//			openList.add(new BugzillaOpenStructure(server, bugId, commentNumer));
//			ViewBugzillaAction viewBugs = new ViewBugzillaAction("Display bugs in editor", openList);
//			viewBugs.schedule();
		}
	}

	public ILabelProvider getLabelProvider() {
		return labelProvider;
	}

	public void close(IMylarElement node) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null) {
			IEditorReference[] references = page.getEditorReferences();
			for (int i = 0; i < references.length; i++) {
				IEditorPart part = references[i].getEditor(false);
				if (part != null) {
					if (part instanceof AbstractRepositoryTaskEditor) {
						((AbstractRepositoryTaskEditor) part).close();
					} else if (part instanceof BugzillaTaskEditor) {
						((BugzillaTaskEditor) part).close();
					}
				}
			}
		}
	}

	public boolean acceptsEditor(IEditorPart editorPart) {
		return editorPart instanceof AbstractRepositoryTaskEditor;
	}

	public List<TreeViewer> getContentOutlineViewers(IEditorPart editor) {
		List<TreeViewer> viewers = new ArrayList<TreeViewer>();
		TreeViewer outline = getOutlineTreeViewer(editor);
		if (outline != null)
			viewers.add(outline);
		return viewers;
	}

	protected TreeViewer getOutlineTreeViewer(IEditorPart editor) {
		if (editor instanceof AbstractRepositoryTaskEditor) {
			AbstractRepositoryTaskEditor abe = (AbstractRepositoryTaskEditor) editor;
			RepositoryTaskOutlinePage outline = abe.getOutline();
			if (outline != null)
				return outline.getOutlineTreeViewer();
		}
		return null;
	}

	public Object getObjectForTextSelection(TextSelection selection, IEditorPart editor) {
		return null;
	}

	public void restoreEditor(IMylarElement document) {
		// ignore
	}

	public IMylarElement getElement(IEditorInput input) {
		return null;
	}
}
