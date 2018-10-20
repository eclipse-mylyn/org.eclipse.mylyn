/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.gist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.OperationCanceledException;
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
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.ui.TableViewerSupport;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskAttachment;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.commands.OpenTaskAttachmentHandler;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorAttachmentPart;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiMenus;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskAttachmentWizard.Mode;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * Gist editor attachment part. Modeled after {@link TaskEditorAttachmentPart}
 * but with less columns.
 */
public class GistAttachmentPart extends AbstractTaskEditorPart {

	private static final String ID_POPUP_MENU = "org.eclipse.mylyn.tasks.ui.editor.menu.attachments"; //$NON-NLS-1$

	private final String[] attachmentsColumns = {
			org.eclipse.mylyn.internal.tasks.ui.editors.Messages.TaskEditorAttachmentPart_Name,
			org.eclipse.mylyn.internal.tasks.ui.editors.Messages.TaskEditorAttachmentPart_Size,
			org.eclipse.mylyn.internal.tasks.ui.editors.Messages.TaskEditorAttachmentPart_Creator };

	private final int[] attachmentsColumnWidths = { 150, 70, 100 };

	private List<TaskAttribute> attachments;

	private boolean hasIncoming;

	private MenuManager menuManager;

	private Composite attachmentsComposite;

	private Table attachmentsTable;

	/**
	 * Create gist editor attachment part
	 */
	public GistAttachmentPart() {
		setPartName(Messages.GistAttachmentPart_PartName);
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart#createControl(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.forms.widgets.FormToolkit)
	 */
	@Override
	public void createControl(Composite parent, final FormToolkit toolkit) {
		initialize();

		final Section section = createSection(parent, toolkit, hasIncoming);
		section.setText(getPartName() + " (" + attachments.size() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		if (hasIncoming)
			expandSection(toolkit, section);
		else
			section.addExpansionListener(new ExpansionAdapter() {
				@Override
				public void expansionStateChanged(ExpansionEvent event) {
					if (attachmentsComposite == null) {
						expandSection(toolkit, section);
						getTaskEditorPage().reflow();
					}
				}
			});
		setSection(toolkit, section);
	}

	private void expandSection(FormToolkit toolkit, Section section) {
		attachmentsComposite = toolkit.createComposite(section);
		attachmentsComposite.setLayout(EditorUtil.createSectionClientLayout());
		attachmentsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		getTaskEditorPage().registerDefaultDropListener(section);

		if (attachments.size() > 0)
			createAttachmentTable(toolkit, attachmentsComposite);
		else {
			Label label = toolkit
					.createLabel(
							attachmentsComposite,
							org.eclipse.mylyn.internal.tasks.ui.editors.Messages.TaskEditorAttachmentPart_No_attachments);
			getTaskEditorPage().registerDefaultDropListener(label);
		}

		createButtons(attachmentsComposite, toolkit);

		toolkit.paintBordersFor(attachmentsComposite);
		section.setClient(attachmentsComposite);
	}

	/**
	 * @see org.eclipse.ui.forms.AbstractFormPart#dispose()
	 */
	@Override
	public void dispose() {
		if (menuManager != null)
			menuManager.dispose();
		super.dispose();
	}

	private void createAttachmentTable(FormToolkit toolkit,
			final Composite attachmentsComposite) {
		attachmentsTable = toolkit.createTable(attachmentsComposite, SWT.MULTI
				| SWT.FULL_SELECTION);
		attachmentsTable.setLinesVisible(true);
		attachmentsTable.setHeaderVisible(true);
		attachmentsTable.setLayout(new GridLayout());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL)
				.grab(true, false).hint(500, SWT.DEFAULT)
				.applyTo(attachmentsTable);
		attachmentsTable.setData(FormToolkit.KEY_DRAW_BORDER,
				FormToolkit.TREE_BORDER);

		for (int i = 0; i < attachmentsColumns.length; i++) {
			TableColumn column = new TableColumn(attachmentsTable, SWT.LEFT, i);
			column.setText(attachmentsColumns[i]);
			column.setWidth(attachmentsColumnWidths[i]);
			column.setMoveable(true);
			if (i == 0) {
				attachmentsTable.setSortColumn(column);
				attachmentsTable.setSortDirection(SWT.DOWN);
			}
		}
		// size column
		attachmentsTable.getColumn(1).setAlignment(SWT.RIGHT);

		TableViewer attachmentsViewer = new TableViewer(attachmentsTable);
		attachmentsViewer.setUseHashlookup(true);
		attachmentsViewer.setColumnProperties(attachmentsColumns);
		ColumnViewerToolTipSupport.enableFor(attachmentsViewer,
				ToolTip.NO_RECREATE);

		attachmentsViewer.setComparator(new GistAttachmentSorter());

		List<ITaskAttachment> attachmentList = new ArrayList<>(
				attachments.size());
		for (TaskAttribute attribute : attachments) {
			ITaskAttachment taskAttachment = new TaskAttachment(getModel()
					.getTaskRepository(), getModel().getTask(), attribute);
			getTaskData().getAttributeMapper().updateTaskAttachment(
					taskAttachment, attribute);
			attachmentList.add(taskAttachment);
		}
		attachmentsViewer.setContentProvider(new ArrayContentProvider());
		attachmentsViewer.setLabelProvider(new GistAttachmentTableLabelProvider(
				getModel(), getTaskEditorPage().getAttributeEditorToolkit()) {

			@Override
			public String getColumnText(Object element, int columnIndex) {
				if (columnIndex > 0)
					columnIndex++;
				return super.getColumnText(element, columnIndex);
			}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				if (columnIndex > 0)
					columnIndex++;
				return super.getColumnImage(element, columnIndex);
			}

		});
		attachmentsViewer.addOpenListener(new IOpenListener() {
			@Override
			public void open(OpenEvent event) {
				openAttachments(event);
			}
		});
		attachmentsViewer.addSelectionChangedListener(getTaskEditorPage());
		attachmentsViewer.setInput(attachmentList.toArray());

		menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				TasksUiMenus.fillTaskAttachmentMenu(manager);
			}
		});
		getTaskEditorPage().getEditorSite().registerContextMenu(ID_POPUP_MENU,
				menuManager, attachmentsViewer, true);
		Menu menu = menuManager.createContextMenu(attachmentsTable);
		attachmentsTable.setMenu(menu);

		new TableViewerSupport(attachmentsViewer, getStateFile());
	}

	private File getStateFile() {
		IPath stateLocation = Platform.getStateLocation(TasksUiPlugin
				.getDefault().getBundle());
		return stateLocation.append("GistAttachmentPart.xml").toFile(); //$NON-NLS-1$
	}

	private void createButtons(Composite attachmentsComposite,
			FormToolkit toolkit) {
		final Composite attachmentControlsComposite = toolkit
				.createComposite(attachmentsComposite);
		attachmentControlsComposite.setLayout(new GridLayout(2, false));
		attachmentControlsComposite.setLayoutData(new GridData(
				GridData.BEGINNING));

		Button attachFileButton = toolkit
				.createButton(
						attachmentControlsComposite,
						org.eclipse.mylyn.internal.tasks.ui.editors.Messages.TaskEditorAttachmentPart_Attach_,
						SWT.PUSH);
		attachFileButton.setImage(CommonImages
				.getImage(CommonImages.FILE_PLAIN));
		attachFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EditorUtil.openNewAttachmentWizard(getTaskEditorPage(),
						Mode.DEFAULT, null);
			}
		});
		getTaskEditorPage().registerDefaultDropListener(attachFileButton);
	}

	private void initialize() {
		attachments = getTaskData().getAttributeMapper().getAttributesByType(
				getTaskData(), TaskAttribute.TYPE_ATTACHMENT);
		for (TaskAttribute attachmentAttribute : attachments)
			if (getModel().hasIncomingChanges(attachmentAttribute)) {
				hasIncoming = true;
				break;
			}
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart#fillToolBar(org.eclipse.jface.action.ToolBarManager)
	 */
	@Override
	protected void fillToolBar(ToolBarManager toolBarManager) {
		Action attachFileAction = new Action() {
			@Override
			public void run() {
				EditorUtil.openNewAttachmentWizard(getTaskEditorPage(),
						Mode.DEFAULT, null);
			}
		};
		attachFileAction
				.setToolTipText(org.eclipse.mylyn.internal.tasks.ui.editors.Messages.TaskEditorAttachmentPart_Attach_);
		attachFileAction.setImageDescriptor(CommonImages.FILE_PLAIN_SMALL);
		toolBarManager.add(attachFileAction);
	}

	/**
	 * Open attachments from a task.
	 *
	 * @param event
	 */
	protected void openAttachments(OpenEvent event) {
		List<ITaskAttachment> attachments = new ArrayList<>();

		StructuredSelection selection = (StructuredSelection) event
				.getSelection();

		List<?> items = selection.toList();
		for (Object item : items)
			if (item instanceof ITaskAttachment)
				attachments.add((ITaskAttachment) item);

		if (attachments.isEmpty())
			return;

		IWorkbenchPage page = getTaskEditorPage().getSite()
				.getWorkbenchWindow().getActivePage();
		try {
			OpenTaskAttachmentHandler.openAttachments(page, attachments);
		} catch (OperationCanceledException e) {
			// canceled
		}
	}

	@Override
	public boolean setFormInput(Object input) {
		if (input instanceof String) {
			if (attachments != null)
				for (TaskAttribute attachmentAttribute : attachments) {
					if (input.equals(attachmentAttribute.getId())) {
						CommonFormUtil.setExpanded(
								(ExpandableComposite) getControl(), true);
						return selectReveal(attachmentAttribute);
					}
				}
		}
		return super.setFormInput(input);
	}

	/**
	 * Selects and shows in the table of attachments an attachment matching the
	 * given {@link TaskAttribute}.
	 *
	 * @param attachmentAttribute
	 *            to select
	 * @return whether an element was found and selected
	 */
	public boolean selectReveal(TaskAttribute attachmentAttribute) {
		if (attachmentAttribute == null || attachmentsTable == null)
			return false;

		TableItem[] attachments = attachmentsTable.getItems();
		int index = 0;
		for (TableItem attachment : attachments) {
			Object data = attachment.getData();
			if (data instanceof ITaskAttachment) {
				ITaskAttachment attachmentData = ((ITaskAttachment) data);
				if (attachmentData.getTaskAttribute().getValue()
						.equals(attachmentAttribute.getValue())) {
					attachmentsTable.deselectAll();
					attachmentsTable.select(index);
					IManagedForm mform = getManagedForm();
					ScrolledForm form = mform.getForm();
					EditorUtil.focusOn(form, attachmentsTable);
					return true;
				}
			}
			index++;
		}
		return false;
	}

}
