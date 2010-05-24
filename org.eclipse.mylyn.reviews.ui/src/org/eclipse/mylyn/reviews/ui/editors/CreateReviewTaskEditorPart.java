/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.mylyn.reviews.ui.IPatchCreator;
import org.eclipse.mylyn.reviews.ui.Images;
import org.eclipse.mylyn.reviews.ui.PatchCreator;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/*
 * @author Kilian Matt
 */
public class CreateReviewTaskEditorPart extends AbstractTaskEditorPart {

	public static final String ID_PART_CREATEREVIEW = "org.eclipse.mylyn.reviews.ui.editors.parts.createreview"; //$NON-NLS-1$
	private IPatchCreator selectedPatch;

	public CreateReviewTaskEditorPart() {
		setPartName(Messages.CreateReviewTaskEditorPart_Patches);
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		try {
			Section section = createSection(parent, toolkit,
					ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR
							| ExpandableComposite.EXPANDED);
			section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			section.setLayout(new FillLayout());
			Composite composite = toolkit.createComposite(section);
			composite.setLayout(new GridLayout(1, false));
			TableViewer patchTable = new TableViewer(composite);
			patchTable.getTable().setHeaderVisible(true);
			patchTable.setContentProvider(ArrayContentProvider.getInstance());
			patchTable
					.addSelectionChangedListener(new ISelectionChangedListener() {

						public void selectionChanged(SelectionChangedEvent event) {
							IStructuredSelection selection = (IStructuredSelection) event
									.getSelection();
							if (selection.getFirstElement() instanceof PatchCreator) {
								selectedPatch = (IPatchCreator) selection
										.getFirstElement();

							}
						}
					});
			patchTable.addDoubleClickListener(new IDoubleClickListener() {

				public void doubleClick(DoubleClickEvent event) {

					IPatchCreator patchCreator = (IPatchCreator) ((IStructuredSelection) event
							.getSelection()).getFirstElement();
					openReviewEditorForPatch(patchCreator);
				}
			});
			createColumn(patchTable, Messages.CreateReviewTaskEditorPart_Header_Filename);
			createColumn(patchTable, Messages.CreateReviewTaskEditorPart_Header_Author);
			createColumn(patchTable, Messages.CreateReviewTaskEditorPart_Header_Date);
			patchTable.setLabelProvider(new TableLabelProvider() {
				final int COLUMN_FILENAME = 0;
				final int COLUMN_AUTHOR = 1;
				final int COLUMN_DATE = 2;

				@Override
				public String getColumnText(Object element, int columnIndex) {
					IPatchCreator patch = (IPatchCreator) element;
					switch (columnIndex) {
					case COLUMN_FILENAME:
						return patch.getFileName();
					case COLUMN_AUTHOR:
						return patch.getAuthor();
					case COLUMN_DATE:
						return patch.getCreationDate().toString();
					default:
						return null;
					}
				}

				@Override
				public Image getColumnImage(Object element, int columnIndex) {
					return null;
				}
			});
			patchTable.getControl().setLayoutData(
					new GridData(SWT.FILL, SWT.FILL, true, true));

			Button button = toolkit
					.createButton(composite,
							Messages.CreateReviewTaskEditorPart_Create_Review,
							SWT.PUSH);
			button.setImage(Images.ICON.createImage());
			button.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					if (selectedPatch != null) {
						openReviewEditorForPatch(selectedPatch);
					}
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetDefaultSelected(e);
				}
			});

			section.setClient(composite);
			patchTable.setInput(parseAttachmentForPatches());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void openReviewEditorForPatch(IPatchCreator selectedPatch) {
		try {
			ReviewTaskEditorInput input = new NewReviewTaskEditorInput(
					getModel(), selectedPatch.create());
			IWorkbenchWindow window = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();

			window.getActivePage().openEditor(input, ReviewEditor.ID, true);
		} catch (CoreException e1) {
			throw new RuntimeException(e1);
		}

	}

	private TableViewerColumn createColumn(TableViewer parent,
			String columnTitle) {
		TableViewerColumn column = new TableViewerColumn(parent, SWT.LEFT);
		column.getColumn().setText(columnTitle);
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		return column;
	}

	private List<PatchCreator> parseAttachmentForPatches() {
		try {

			TaskDataModel model = getModel();
			TaskData taskData = model.getTaskData();
			List<TaskAttribute> attributesByType = taskData
					.getAttributeMapper().getAttributesByType(taskData,
							TaskAttribute.TYPE_ATTACHMENT);

			List<PatchCreator> patchFiles = new ArrayList<PatchCreator>();
			for (TaskAttribute attribute : attributesByType) {
				if (attribute.getMappedAttribute(
						TaskAttribute.ATTACHMENT_IS_PATCH).getValue().equals(
						"1")) { //$NON-NLS-1$

					patchFiles.add(new PatchCreator(attribute));
				}

			}
			return patchFiles;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
}
