/*******************************************************************************
 * Copyright (c) 2010, 2013 Peter Stibrany and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Peter Stibrany - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;

/**
 * @author Peter Stibrany
 */
public class TaskAttachmentViewerManager {

	public ITaskAttachmentViewer getBrowserViewer(ITaskAttachment attachment) {
		if (attachment.getUrl() != null && attachment.getUrl().trim().length() > 0) {
			return new TaskAttachmentBrowserViewer();
		}
		return null;
	}

	public List<ITaskAttachmentViewer> getWorkbenchViewers(ITaskAttachment attachment) {
		List<ITaskAttachmentViewer> result = new ArrayList<ITaskAttachmentViewer>();
		IEditorRegistry registry = PlatformUI.getWorkbench().getEditorRegistry();

		IEditorDescriptor defaultEditor = registry.getDefaultEditor(AttachmentUtil.getAttachmentFilename(attachment));
		if (defaultEditor != null) {
			result.add(new TaskAttachmentEditorViewer(defaultEditor, true));
		}

		IEditorDescriptor defaultTextEditor = registry.findEditor(EditorsUI.DEFAULT_TEXT_EDITOR_ID); // may be null
		if (defaultTextEditor != null
				&& (defaultEditor == null || !defaultTextEditor.getId().equals(defaultEditor.getId()))) {
			result.add(new TaskAttachmentEditorViewer(defaultTextEditor));
		}

		IEditorDescriptor[] descriptors = registry.getEditors(AttachmentUtil.getAttachmentFilename(attachment));
		for (IEditorDescriptor ied : descriptors) {
			if (defaultEditor == null || !ied.getId().equals(defaultEditor.getId())) {
				result.add(new TaskAttachmentEditorViewer(ied));
			}
		}

		return result;
	}

	public List<ITaskAttachmentViewer> getSystemViewers(ITaskAttachment attachment) {
		List<ITaskAttachmentViewer> result = new ArrayList<ITaskAttachmentViewer>();
		IEditorRegistry registry = PlatformUI.getWorkbench().getEditorRegistry();

		// Don't check whether system external editor is available (IEditorRegistry.isSystemExternalEditorAvailable) ...
		// At least Windows can handle even unknown files, and offers user to choose correct program to open file with
		IEditorDescriptor extern = registry.findEditor(IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
		result.add(new TaskAttachmentEditorViewer(extern, false, true));

		if (registry.isSystemInPlaceEditorAvailable(AttachmentUtil.getAttachmentFilename(attachment))) {
			IEditorDescriptor inplace = registry.findEditor(IEditorRegistry.SYSTEM_INPLACE_EDITOR_ID);
			result.add(new TaskAttachmentEditorViewer(inplace, false, true));
		}

		return result;
	}

	public List<ITaskAttachmentViewer> getTaskAttachmentViewers(ITaskAttachment attachment) {
		List<ITaskAttachmentViewer> result = new ArrayList<ITaskAttachmentViewer>();
		ITaskAttachmentViewer browserViewer = getBrowserViewer(attachment);
		if (browserViewer != null) {
			result.add(browserViewer);
		}
		result.addAll(getWorkbenchViewers(attachment));
		result.addAll(getSystemViewers(attachment));
		return result;
	}

	/**
	 * @param attachment
	 * @return preferred attachment viewers, or null if no suitable viewer can be found
	 */
	public ITaskAttachmentViewer getPreferredViewer(ITaskAttachment attachment) {
		/*
		 * Find viewers in order of preference: preferred, workbench default, system editor, first editor in list
		 */
		List<ITaskAttachmentViewer> viewers = getTaskAttachmentViewers(attachment);
		ITaskAttachmentViewer defaultViewer = null;
		String preferred = getPreferredViewerID(attachment);
		for (ITaskAttachmentViewer viewer : viewers) {
			if ((preferred != null && preferred.equals(viewer.getId()))) {
				return viewer;
			} else if (viewer.isWorkbenchDefault()) {
				defaultViewer = viewer;
			} else if (defaultViewer == null && Platform.getOS().equals(Platform.OS_WIN32)
					&& IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID.equals(viewer.getId())) {
				defaultViewer = viewer;
			}
		}
		if (defaultViewer == null && !viewers.isEmpty()) {
			defaultViewer = viewers.get(0);
		}

		return defaultViewer;
	}

	public String getPreferredViewerID(ITaskAttachment attachment) {
		String ext = getExtension(attachment);
		if (ext == null) {
			return null;
		}

		return getPreferencesStore().getString(
				ITasksUiPreferenceConstants.PREFERRED_TASK_ATTACHMENT_VIEWER_ID + "_" + ext);//$NON-NLS-1$
	}

	private IPreferenceStore getPreferencesStore() {
		return TasksUiPlugin.getDefault().getPreferenceStore();
	}

	public void savePreferredViewerID(ITaskAttachment attachment, String handlerID) {
		String ext = getExtension(attachment);
		if (ext == null) {
			return;
		}

		getPreferencesStore().putValue(
				ITasksUiPreferenceConstants.PREFERRED_TASK_ATTACHMENT_VIEWER_ID + "_" + ext, handlerID); //$NON-NLS-1$
	}

	private String getExtension(ITaskAttachment attachment) {
		if (attachment == null) {
			return null;
		}

		String fname = AttachmentUtil.getAttachmentFilename(attachment);
		int dot = fname.lastIndexOf('.');
		if (dot < 0) {
			return null;
		}

		return fname.substring(dot + 1);
	}

}
