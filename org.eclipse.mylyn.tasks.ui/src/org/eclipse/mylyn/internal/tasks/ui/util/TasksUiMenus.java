/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;

/**
 * @author Steffen Pingel
 */
public class TasksUiMenus {

//	private static final String ATTACHMENT_DEFAULT_NAME = "attachment";
	//
//		private static final String CTYPE_ZIP = "zip";
	//
//		private static final String CTYPE_OCTET_STREAM = "octet-stream";
	//
//		private static final String CTYPE_TEXT = "text";
	//
//		private static final String CTYPE_HTML = "html";
	//
//		private static final String LABEL_TEXT_EDITOR = "Text Editor";
	//
//		private static final String LABEL_COPY_URL_TO_CLIPBOARD = "Copy &URL";
	//
//		private static final String LABEL_COPY_TO_CLIPBOARD = "Copy Contents";
	//
//		private static final String LABEL_SAVE = "Save...";
	//
//		private static final String LABEL_BROWSER = "Browser";
	//
//		private static final String LABEL_DEFAULT_EDITOR = "Default Editor";

//	private void createAttachmentTableMenu() {
	// FIXME EDITOR
//		final Action openWithBrowserAction = new Action(LABEL_BROWSER) {
//			@Override
//			public void run() {
//				TaskAttachment attachment = (TaskAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
//				if (attachment != null) {
//					TasksUiUtil.openUrl(attachment.getUrl());
//				}
//			}
//		};
//
//		final Action openWithDefaultAction = new Action(LABEL_DEFAULT_EDITOR) {
//			@Override
//			public void run() {
//				// browser shortcut
//				TaskAttachment attachment = (TaskAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
//				if (attachment == null) {
//					return;
//				}
//
//				if (attachment.getContentType().endsWith(CTYPE_HTML)) {
//					TasksUiUtil.openUrl(attachment.getUrl());
//					return;
//				}
//
//				IStorageEditorInput input = new RepositoryAttachmentEditorInput(getTaskRepository(), attachment);
//				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//				if (page == null) {
//					return;
//				}
//				IEditorDescriptor desc = PlatformUI.getWorkbench()
//						.getEditorRegistry()
//						.getDefaultEditor(input.getName());
//				try {
//					page.openEditor(input, desc.getId());
//				} catch (PartInitException e) {
//					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unable to open editor for: "
//							+ attachment.getDescription(), e));
//				}
//			}
//		};
//
//		final Action openWithTextEditorAction = new Action(LABEL_TEXT_EDITOR) {
//			@Override
//			public void run() {
//				TaskAttachment attachment = (TaskAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
//				IStorageEditorInput input = new RepositoryAttachmentEditorInput(getTaskRepository(), attachment);
//				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//				if (page == null) {
//					return;
//				}
//
//				try {
//					page.openEditor(input, "org.eclipse.ui.DefaultTextEditor");
//				} catch (PartInitException e) {
//					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unable to open editor for: "
//							+ attachment.getDescription(), e));
//				}
//			}
//		};
//
//		final Action saveAction = new Action(LABEL_SAVE) {
//			@Override
//			public void run() {
//				TaskAttachment attachment = (TaskAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
//				/* Launch Browser */
//				FileDialog fileChooser = new FileDialog(attachmentsTable.getShell(), SWT.SAVE);
//				String fname = attachment.getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_FILENAME);
//				// Default name if none is found
//				if (fname.equals("")) {
//					String ctype = attachment.getContentType();
//					if (ctype.endsWith(CTYPE_HTML)) {
//						fname = ATTACHMENT_DEFAULT_NAME + ".html";
//					} else if (ctype.startsWith(CTYPE_TEXT)) {
//						fname = ATTACHMENT_DEFAULT_NAME + ".txt";
//					} else if (ctype.endsWith(CTYPE_OCTET_STREAM)) {
//						fname = ATTACHMENT_DEFAULT_NAME;
//					} else if (ctype.endsWith(CTYPE_ZIP)) {
//						fname = ATTACHMENT_DEFAULT_NAME + "." + CTYPE_ZIP;
//					} else {
//						fname = ATTACHMENT_DEFAULT_NAME + "." + ctype.substring(ctype.indexOf("/") + 1);
//					}
//				}
//				fileChooser.setFileName(fname);
//				String filePath = fileChooser.open();
//				// Check if the dialog was canceled or an error occurred
//				if (filePath == null) {
//					return;
//				}
//
//				DownloadAttachmentJob job = new DownloadAttachmentJob(attachment, new File(filePath));
//				job.setUser(true);
//				job.schedule();
//			}
//		};
//
//		final Action copyURLToClipAction = new Action(LABEL_COPY_URL_TO_CLIPBOARD) {
//			@Override
//			public void run() {
//				TaskAttachment attachment = (TaskAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
//				Clipboard clip = new Clipboard(PlatformUI.getWorkbench().getDisplay());
//				clip.setContents(new Object[] { attachment.getUrl() }, new Transfer[] { TextTransfer.getInstance() });
//				clip.dispose();
//			}
//		};
//
//		final Action copyToClipAction = new Action(LABEL_COPY_TO_CLIPBOARD) {
//			@Override
//			public void run() {
//				TaskAttachment attachment = (TaskAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
//				CopyAttachmentToClipboardJob job = new CopyAttachmentToClipboardJob(attachment);
//				job.setUser(true);
//				job.schedule();
//			}
//		};
//
//		final MenuManager popupMenu = new MenuManager();
//		final Menu menu = popupMenu.createContextMenu(attachmentsTable);
//		attachmentsTable.setMenu(menu);
//		final MenuManager openMenu = new MenuManager("Open With");
//		popupMenu.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager manager) {
//				popupMenu.removeAll();
//
//				ISelection selection = attachmentsTableViewer.getSelection();
//				if (selection.isEmpty()) {
//					return;
//				}
//
//				TaskAttachment att = (TaskAttachment) ((StructuredSelection) selection).getFirstElement();
//
//				// reinitialize menu
//				popupMenu.add(openMenu);
//				openMenu.removeAll();
//				IStorageEditorInput input = new RepositoryAttachmentEditorInput(getTaskRepository(), att);
//				IEditorDescriptor desc = PlatformUI.getWorkbench()
//						.getEditorRegistry()
//						.getDefaultEditor(input.getName());
//				if (desc != null) {
//					openMenu.add(openWithDefaultAction);
//				}
//				openMenu.add(openWithBrowserAction);
//				openMenu.add(openWithTextEditorAction);
//
//				popupMenu.add(new Separator());
//				popupMenu.add(saveAction);
//
//				popupMenu.add(copyURLToClipAction);
//				if (att.getContentType().startsWith(CTYPE_TEXT) || att.getContentType().endsWith("xml")) {
//					popupMenu.add(copyToClipAction);
//				}
//				popupMenu.add(new Separator("actions"));
//
//				// TODO: use workbench mechanism for this?
//				ObjectActionContributorManager.getManager().contributeObjectActions(getTaskEditorPage(), popupMenu,
//						attachmentsTableViewer);
//			}
//		});
//	}

	public static void fillTaskAttachmentMenu(IMenuManager manager) {
		manager.add(new Separator("group.open"));
		manager.add(new Separator("group.save"));
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

}
