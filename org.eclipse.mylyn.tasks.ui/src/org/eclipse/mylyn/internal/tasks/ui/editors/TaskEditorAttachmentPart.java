/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.io.File;
import java.util.Date;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.actions.AbstractTaskEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.AttachAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.AttachScreenshotAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyAttachmentToClipboardJob;
import org.eclipse.mylyn.internal.tasks.ui.actions.DownloadAttachmentJob;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.RepositoryAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.internal.ObjectActionContributorManager;
import org.eclipse.ui.internal.WorkbenchImages;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Jeff Pound (Attachment work)
 * @author Steffen Pingel
 */
public class TaskEditorAttachmentPart extends AbstractTaskEditorPart {

	private static final String ATTACHMENT_DEFAULT_NAME = "attachment";

	private static final String CTYPE_ZIP = "zip";

	private static final String CTYPE_OCTET_STREAM = "octet-stream";

	private static final String CTYPE_TEXT = "text";

	private static final String CTYPE_HTML = "html";

	private static final String LABEL_TEXT_EDITOR = "Text Editor";

	private static final String LABEL_COPY_URL_TO_CLIPBOARD = "Copy &URL";

	private static final String LABEL_COPY_TO_CLIPBOARD = "Copy Contents";

	private static final String LABEL_SAVE = "Save...";

	private static final String LABEL_BROWSER = "Browser";

	private static final String LABEL_DEFAULT_EDITOR = "Default Editor";

	private final String[] attachmentsColumns = { "Name", "Description", "Type", "Size", "Creator", "Created" };

	private final int[] attachmentsColumnWidths = { 140, 160, 100, 70, 100, 100 };

	private Table attachmentsTable;

	private TableViewer attachmentsTableViewer;

	private boolean supportsDelete;

	public TaskEditorAttachmentPart(AbstractTaskEditorPage taskEditorPage) {
		super(taskEditorPage);
	}

	private void createAttachmentTable(FormToolkit toolkit, final Composite attachmentsComposite) {
		attachmentsTable = toolkit.createTable(attachmentsComposite, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		attachmentsTable.setLinesVisible(true);
		attachmentsTable.setHeaderVisible(true);
		attachmentsTable.setLayout(new GridLayout());
		GridData tableGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		attachmentsTable.setLayoutData(tableGridData);

		for (int i = 0; i < attachmentsColumns.length; i++) {
			TableColumn column = new TableColumn(attachmentsTable, SWT.LEFT, i);
			column.setText(attachmentsColumns[i]);
			column.setWidth(attachmentsColumnWidths[i]);
		}
		attachmentsTable.getColumn(3).setAlignment(SWT.RIGHT);

		attachmentsTableViewer = new TableViewer(attachmentsTable);
		attachmentsTableViewer.setUseHashlookup(true);
		attachmentsTableViewer.setColumnProperties(attachmentsColumns);
		ColumnViewerToolTipSupport.enableFor(attachmentsTableViewer, ToolTip.NO_RECREATE);

		final AbstractTaskDataHandler taskDataHandler = getConnector().getTaskDataHandler();
		if (taskDataHandler != null) {
			attachmentsTableViewer.setSorter(new ViewerSorter() {
				@Override
				public int compare(Viewer viewer, Object e1, Object e2) {
					RepositoryAttachment attachment1 = (RepositoryAttachment) e1;
					RepositoryAttachment attachment2 = (RepositoryAttachment) e2;
					Date created1 = getTaskData().getAttributeFactory().getDateForAttributeType(
							RepositoryTaskAttribute.ATTACHMENT_DATE, attachment1.getDateCreated());
					Date created2 = getTaskData().getAttributeFactory().getDateForAttributeType(
							RepositoryTaskAttribute.ATTACHMENT_DATE, attachment2.getDateCreated());
					if (created1 != null && created2 != null) {
						return created1.compareTo(created2);
					} else if (created1 == null && created2 != null) {
						return -1;
					} else if (created1 != null && created2 == null) {
						return 1;
					} else {
						return 0;
					}
				}
			});
		}

		attachmentsTableViewer.setContentProvider(new AttachmentsTableContentProvider(getTaskData().getAttachments()));

		attachmentsTableViewer.setLabelProvider(new AttachmentTableLabelProvider(null, new LabelProvider(),
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));

		attachmentsTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (!event.getSelection().isEmpty()) {
					StructuredSelection selection = (StructuredSelection) event.getSelection();
					RepositoryAttachment attachment = (RepositoryAttachment) selection.getFirstElement();
					TasksUiUtil.openUrl(attachment.getUrl(), false);
				}
			}
		});

		attachmentsTableViewer.setInput(getTaskData());
	}

	private void createAttachmentTableMenu() {
		final Action openWithBrowserAction = new Action(LABEL_BROWSER) {
			@Override
			public void run() {
				RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
				if (attachment != null) {
					TasksUiUtil.openUrl(attachment.getUrl(), false);
				}
			}
		};

		final Action openWithDefaultAction = new Action(LABEL_DEFAULT_EDITOR) {
			@Override
			public void run() {
				// browser shortcut
				RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
				if (attachment == null) {
					return;
				}

				if (attachment.getContentType().endsWith(CTYPE_HTML)) {
					TasksUiUtil.openUrl(attachment.getUrl(), false);
					return;
				}

				IStorageEditorInput input = new RepositoryAttachmentEditorInput(getTaskRepository(), attachment);
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				if (page == null) {
					return;
				}
				IEditorDescriptor desc = PlatformUI.getWorkbench()
						.getEditorRegistry()
						.getDefaultEditor(input.getName());
				try {
					page.openEditor(input, desc.getId());
				} catch (PartInitException e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unable to open editor for: " + attachment.getDescription(), e));
				}
			}
		};

		final Action openWithTextEditorAction = new Action(LABEL_TEXT_EDITOR) {
			@Override
			public void run() {
				RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
				IStorageEditorInput input = new RepositoryAttachmentEditorInput(getTaskRepository(), attachment);
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				if (page == null) {
					return;
				}

				try {
					page.openEditor(input, "org.eclipse.ui.DefaultTextEditor");
				} catch (PartInitException e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unable to open editor for: " + attachment.getDescription(), e));
				}
			}
		};

		final Action saveAction = new Action(LABEL_SAVE) {
			@Override
			public void run() {
				RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
				/* Launch Browser */
				FileDialog fileChooser = new FileDialog(attachmentsTable.getShell(), SWT.SAVE);
				String fname = attachment.getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_FILENAME);
				// Default name if none is found
				if (fname.equals("")) {
					String ctype = attachment.getContentType();
					if (ctype.endsWith(CTYPE_HTML)) {
						fname = ATTACHMENT_DEFAULT_NAME + ".html";
					} else if (ctype.startsWith(CTYPE_TEXT)) {
						fname = ATTACHMENT_DEFAULT_NAME + ".txt";
					} else if (ctype.endsWith(CTYPE_OCTET_STREAM)) {
						fname = ATTACHMENT_DEFAULT_NAME;
					} else if (ctype.endsWith(CTYPE_ZIP)) {
						fname = ATTACHMENT_DEFAULT_NAME + "." + CTYPE_ZIP;
					} else {
						fname = ATTACHMENT_DEFAULT_NAME + "." + ctype.substring(ctype.indexOf("/") + 1);
					}
				}
				fileChooser.setFileName(fname);
				String filePath = fileChooser.open();
				// Check if the dialog was canceled or an error occurred
				if (filePath == null) {
					return;
				}

				DownloadAttachmentJob job = new DownloadAttachmentJob(attachment, new File(filePath));
				job.setUser(true);
				job.schedule();
			}
		};

		final Action copyURLToClipAction = new Action(LABEL_COPY_URL_TO_CLIPBOARD) {
			@Override
			public void run() {
				RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
				Clipboard clip = new Clipboard(PlatformUI.getWorkbench().getDisplay());
				clip.setContents(new Object[] { attachment.getUrl() }, new Transfer[] { TextTransfer.getInstance() });
				clip.dispose();
			}
		};

		final Action copyToClipAction = new Action(LABEL_COPY_TO_CLIPBOARD) {
			@Override
			public void run() {
				RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
				CopyAttachmentToClipboardJob job = new CopyAttachmentToClipboardJob(attachment);
				job.setUser(true);
				job.schedule();
			}
		};

		final MenuManager popupMenu = new MenuManager();
		final Menu menu = popupMenu.createContextMenu(attachmentsTable);
		attachmentsTable.setMenu(menu);
		final MenuManager openMenu = new MenuManager("Open With");
		popupMenu.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				popupMenu.removeAll();

				ISelection selection = attachmentsTableViewer.getSelection();
				if (selection.isEmpty()) {
					return;
				}

				RepositoryAttachment att = (RepositoryAttachment) ((StructuredSelection) selection).getFirstElement();

				// reinitialize menu
				popupMenu.add(openMenu);
				openMenu.removeAll();
				IStorageEditorInput input = new RepositoryAttachmentEditorInput(getTaskRepository(), att);
				IEditorDescriptor desc = PlatformUI.getWorkbench()
						.getEditorRegistry()
						.getDefaultEditor(input.getName());
				if (desc != null) {
					openMenu.add(openWithDefaultAction);
				}
				openMenu.add(openWithBrowserAction);
				openMenu.add(openWithTextEditorAction);

				popupMenu.add(new Separator());
				popupMenu.add(saveAction);

				popupMenu.add(copyURLToClipAction);
				if (att.getContentType().startsWith(CTYPE_TEXT) || att.getContentType().endsWith("xml")) {
					popupMenu.add(copyToClipAction);
				}
				popupMenu.add(new Separator("actions"));

				// TODO: use workbench mechanism for this?
				ObjectActionContributorManager.getManager().contributeObjectActions(getTaskEditorPage(), popupMenu,
						attachmentsTableViewer);
			}
		});
	}

	private void createButtons(Composite attachmentsComposite, FormToolkit toolkit) {
		final Composite attachmentControlsComposite = toolkit.createComposite(attachmentsComposite);
		attachmentControlsComposite.setLayout(new GridLayout(2, false));
		attachmentControlsComposite.setLayoutData(new GridData(GridData.BEGINNING));

		Button attachFileButton = toolkit.createButton(attachmentControlsComposite, AttachAction.LABEL, SWT.PUSH);
		attachFileButton.setImage(WorkbenchImages.getImage(ISharedImages.IMG_OBJ_FILE));

		Button attachScreenshotButton = toolkit.createButton(attachmentControlsComposite, AttachScreenshotAction.LABEL,
				SWT.PUSH);
		attachScreenshotButton.setImage(TasksUiImages.getImage(TasksUiImages.IMAGE_CAPTURE));

		final AbstractTask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(
				getTaskRepository().getUrl(), getTaskData().getId());
		if (task == null) {
			attachFileButton.setEnabled(false);
			attachScreenshotButton.setEnabled(false);
		}

		attachFileButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

			public void widgetSelected(SelectionEvent e) {
				AbstractTaskEditorAction attachFileAction = new AttachAction();
				attachFileAction.selectionChanged(new StructuredSelection(task));
				attachFileAction.setEditor(getTaskEditor());
				attachFileAction.run();
			}
		});

		attachScreenshotButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

			public void widgetSelected(SelectionEvent e) {
				AttachScreenshotAction attachScreenshotAction = new AttachScreenshotAction();
				attachScreenshotAction.selectionChanged(new StructuredSelection(task));
				attachScreenshotAction.setEditor(getTaskEditor());
				attachScreenshotAction.run();
			}
		});

		Button deleteAttachmentButton = null;
		if (supportsDelete()) {
			deleteAttachmentButton = toolkit.createButton(attachmentControlsComposite, "Delete Attachment...", SWT.PUSH);

			deleteAttachmentButton.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					// ignore
				}

				public void widgetSelected(SelectionEvent e) {
					AbstractTask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(
							getTaskRepository().getUrl(), getTaskData().getId());
					if (task == null) {
						// Should not happen
						return;
					}
					if (getTaskEditorPage().isDirty()
							|| task.getSynchronizationState().equals(RepositoryTaskSyncState.OUTGOING)) {
						MessageDialog.openInformation(getControl().getShell(), "Task not synchronized or dirty editor",
								"Commit edits or synchronize task before deleting attachments.");
						return;
					} else {
						if (attachmentsTableViewer != null
								&& attachmentsTableViewer.getSelection() != null
								&& ((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement() != null) {
							RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
							getTaskEditorPage().deleteAttachment(attachment);
							getTaskEditorPage().submitToRepository();
						}
					}
				}
			});

		}
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		final Composite attachmentsComposite = toolkit.createComposite(parent);
		attachmentsComposite.setLayout(new GridLayout(1, false));
		attachmentsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (getTaskData().getAttachments().size() > 0) {
			createAttachmentTable(toolkit, attachmentsComposite);
			createAttachmentTableMenu();
		} else {
			toolkit.createLabel(attachmentsComposite, "No attachments");
			// TODO EDITOR registerDropListener(label);
		}

		createButtons(attachmentsComposite, toolkit);

		// TODO EDITOR fix drop listener 
//		registerDropListener(section);
//		registerDropListener(attachmentsComposite);
//		registerDropListener(attachFileButton);
//		if (supportsAttachmentDelete()) {
//			registerDropListener(deleteAttachmentButton);
//		}
		
		setControl(attachmentsComposite);
	}

	public void setSupportsDelete(boolean supportsDelete) {
		this.supportsDelete = supportsDelete;
	}

	public boolean supportsDelete() {
		return supportsDelete;
	}

}
