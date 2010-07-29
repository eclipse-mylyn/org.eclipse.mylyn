/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Jeff Pound - attachment support
 *     Frank Becker - improvements for bug 204051
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.TableSorter;
import org.eclipse.mylyn.internal.provisional.commons.ui.TableViewerSupport;
import org.eclipse.mylyn.internal.tasks.core.TaskAttachment;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.commands.OpenTaskAttachmentHandler;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiMenus;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskKeyComparator;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskAttachmentWizard.Mode;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TaskEditorAttachmentPart extends AbstractTaskEditorPart {

	private class AttachmentTableSorter extends TableSorter {

		TaskKeyComparator keyComparator = new TaskKeyComparator();

		@Override
		public int compare(TableViewer viewer, Object e1, Object e2, int columnIndex) {
			ITaskAttachment attachment1 = (ITaskAttachment) e1;
			ITaskAttachment attachment2 = (ITaskAttachment) e2;
			switch (columnIndex) {
			case 0:
				return compare(attachment1.getFileName(), attachment2.getFileName());
			case 1:
				String description1 = attachment1.getDescription();
				String description2 = attachment2.getDescription();
				return compare(description1, description2);
			case 2:
				return compare(attachment1.getLength(), attachment2.getLength());
			case 3:
				return compare(attachment1.getAuthor().toString(), attachment2.getAuthor().toString());
			case 4:
				return compare(attachment1.getCreationDate(), attachment2.getCreationDate());
			case 5:
				String key1 = AttachmentTableLabelProvider.getAttachmentId(attachment1);
				String key2 = AttachmentTableLabelProvider.getAttachmentId(attachment2);
				return keyComparator.compare2(key1, key2);
			}
			return super.compare(viewer, e1, e2, columnIndex);
		}

	}

	private static final String ID_POPUP_MENU = "org.eclipse.mylyn.tasks.ui.editor.menu.attachments"; //$NON-NLS-1$

	private final String[] attachmentsColumns = { Messages.TaskEditorAttachmentPart_Name,
			Messages.TaskEditorAttachmentPart_Description, /*"Type", */Messages.TaskEditorAttachmentPart_Size,
			Messages.TaskEditorAttachmentPart_Creator, Messages.TaskEditorAttachmentPart_Created,
			Messages.TaskEditorAttachmentPart_ID };

	private final int[] attachmentsColumnWidths = { 130, 150, /*100,*/70, 100, 100, 0 };

	private List<TaskAttribute> attachments;

	private boolean hasIncoming;

	private MenuManager menuManager;

	private Composite attachmentsComposite;

	public TaskEditorAttachmentPart() {
		setPartName(Messages.TaskEditorAttachmentPart_Attachments);
	}

	private void createAttachmentTable(FormToolkit toolkit, final Composite attachmentsComposite) {
		Table attachmentsTable = toolkit.createTable(attachmentsComposite, SWT.MULTI | SWT.FULL_SELECTION);
		attachmentsTable.setLinesVisible(true);
		attachmentsTable.setHeaderVisible(true);
		attachmentsTable.setLayout(new GridLayout());
		GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.FILL)
				.grab(true, false)
				.hint(500, SWT.DEFAULT)
				.applyTo(attachmentsTable);
		attachmentsTable.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);

		for (int i = 0; i < attachmentsColumns.length; i++) {
			TableColumn column = new TableColumn(attachmentsTable, SWT.LEFT, i);
			column.setText(attachmentsColumns[i]);
			column.setWidth(attachmentsColumnWidths[i]);
			if (i == 4) {
				attachmentsTable.setSortColumn(column);
				attachmentsTable.setSortDirection(SWT.DOWN);
			}
		}
		// size column
		attachmentsTable.getColumn(2).setAlignment(SWT.RIGHT);

		TableViewer attachmentsViewer = new TableViewer(attachmentsTable);
		attachmentsViewer.setUseHashlookup(true);
		attachmentsViewer.setColumnProperties(attachmentsColumns);
		ColumnViewerToolTipSupport.enableFor(attachmentsViewer, ToolTip.NO_RECREATE);

		attachmentsViewer.setSorter(new AttachmentTableSorter());

		List<ITaskAttachment> attachmentList = new ArrayList<ITaskAttachment>(attachments.size());
		for (TaskAttribute attribute : attachments) {
			TaskAttachment taskAttachment = new TaskAttachment(getModel().getTaskRepository(), getModel().getTask(),
					attribute);
			getTaskData().getAttributeMapper().updateTaskAttachment(taskAttachment, attribute);
			attachmentList.add(taskAttachment);
		}
		attachmentsViewer.setContentProvider(new ArrayContentProvider());
		attachmentsViewer.setLabelProvider(new AttachmentTableLabelProvider(getModel(),
				getTaskEditorPage().getAttributeEditorToolkit()));
		attachmentsViewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				openAttachments(event);
			}
		});
		attachmentsViewer.addSelectionChangedListener(getTaskEditorPage());
		attachmentsViewer.setInput(attachmentList.toArray());

		menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TasksUiMenus.fillTaskAttachmentMenu(manager);
			}
		});
		getTaskEditorPage().getEditorSite().registerContextMenu(ID_POPUP_MENU, menuManager, attachmentsViewer, true);
		Menu menu = menuManager.createContextMenu(attachmentsTable);
		attachmentsTable.setMenu(menu);

		new TableViewerSupport(attachmentsViewer, getStateFile());
	}

	private File getStateFile() {
		IPath stateLocation = Platform.getStateLocation(TasksUiPlugin.getDefault().getBundle());
		return stateLocation.append("TaskEditorAttachmentPart.xml").toFile(); //$NON-NLS-1$
	}

	private void createButtons(Composite attachmentsComposite, FormToolkit toolkit) {
		final Composite attachmentControlsComposite = toolkit.createComposite(attachmentsComposite);
		attachmentControlsComposite.setLayout(new GridLayout(2, false));
		attachmentControlsComposite.setLayoutData(new GridData(GridData.BEGINNING));

		Button attachFileButton = toolkit.createButton(attachmentControlsComposite,
				Messages.TaskEditorAttachmentPart_Attach_, SWT.PUSH);
		attachFileButton.setImage(CommonImages.getImage(CommonImages.FILE_PLAIN));
		attachFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EditorUtil.openNewAttachmentWizard(getTaskEditorPage(), Mode.DEFAULT, null);
			}
		});
		getTaskEditorPage().registerDefaultDropListener(attachFileButton);

		Button attachScreenshotButton = toolkit.createButton(attachmentControlsComposite,
				Messages.TaskEditorAttachmentPart_Attach__Screenshot, SWT.PUSH);
		attachScreenshotButton.setImage(CommonImages.getImage(CommonImages.IMAGE_CAPTURE));
		attachScreenshotButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EditorUtil.openNewAttachmentWizard(getTaskEditorPage(), Mode.SCREENSHOT, null);
			}
		});
		getTaskEditorPage().registerDefaultDropListener(attachScreenshotButton);
	}

	@Override
	public void createControl(Composite parent, final FormToolkit toolkit) {
		initialize();

		final Section section = createSection(parent, toolkit, hasIncoming);
		section.setText(getPartName() + " (" + attachments.size() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		if (hasIncoming) {
			expandSection(toolkit, section);
		} else {
			section.addExpansionListener(new ExpansionAdapter() {
				@Override
				public void expansionStateChanged(ExpansionEvent event) {
					if (attachmentsComposite == null) {
						expandSection(toolkit, section);
						getTaskEditorPage().reflow();
					}
				}
			});
		}
		setSection(toolkit, section);
	}

	private void expandSection(FormToolkit toolkit, Section section) {
		attachmentsComposite = toolkit.createComposite(section);
		attachmentsComposite.setLayout(EditorUtil.createSectionClientLayout());
		attachmentsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		getTaskEditorPage().registerDefaultDropListener(section);

		if (attachments.size() > 0) {
			createAttachmentTable(toolkit, attachmentsComposite);
		} else {
			Label label = toolkit.createLabel(attachmentsComposite, Messages.TaskEditorAttachmentPart_No_attachments);
			getTaskEditorPage().registerDefaultDropListener(label);
		}

		createButtons(attachmentsComposite, toolkit);

		toolkit.paintBordersFor(attachmentsComposite);
		section.setClient(attachmentsComposite);
	}

	@Override
	public void dispose() {
		if (menuManager != null) {
			menuManager.dispose();
		}
		super.dispose();
	}

	private void initialize() {
		attachments = getTaskData().getAttributeMapper().getAttributesByType(getTaskData(),
				TaskAttribute.TYPE_ATTACHMENT);
		for (TaskAttribute attachmentAttribute : attachments) {
			if (getModel().hasIncomingChanges(attachmentAttribute)) {
				hasIncoming = true;
				break;
			}
		}
	}

	@Override
	protected void fillToolBar(ToolBarManager toolBarManager) {
		Action attachFileAction = new Action() {
			@Override
			public void run() {
				EditorUtil.openNewAttachmentWizard(getTaskEditorPage(), Mode.DEFAULT, null);
			}
		};
		attachFileAction.setToolTipText(Messages.TaskEditorAttachmentPart_Attach_);
		attachFileAction.setImageDescriptor(CommonImages.FILE_PLAIN_SMALL);
		toolBarManager.add(attachFileAction);
	}

	protected void openAttachments(OpenEvent event) {
		List<ITaskAttachment> attachments = new ArrayList<ITaskAttachment>();

		StructuredSelection selection = (StructuredSelection) event.getSelection();

		List<?> items = selection.toList();
		for (Object item : items) {
			if (item instanceof ITaskAttachment) {
				attachments.add((ITaskAttachment) item);
			}
		}

		if (attachments.isEmpty()) {
			return;
		}

		IWorkbenchPage page = getTaskEditorPage().getSite().getWorkbenchWindow().getActivePage();

		OpenTaskAttachmentHandler.openAttachments(page, attachments);
	}
}
