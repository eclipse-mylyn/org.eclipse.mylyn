package org.eclipse.mylyn.reviews.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.reviews.ui.IPatchCreator;
import org.eclipse.mylyn.reviews.ui.PatchCreator;
import org.eclipse.mylyn.reviews.ui.editors.Messages;
import org.eclipse.mylyn.reviews.ui.editors.TableLabelProvider;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class ReviewScopeWizardPage extends  WizardPage {
	private IPatchCreator selectedPatch;

	protected ReviewScopeWizardPage() {
		super("pagename");
		setTitle("Review scope");
		setDescription("Select the scope of the review");
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1,true));
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
							setPageComplete(true);

						}
					}
				});
//		patchTable.addDoubleClickListener(new IDoubleClickListener() {
//
//			public void doubleClick(DoubleClickEvent event) {
//
//				IPatchCreator patchCreator = (IPatchCreator) ((IStructuredSelection) event
//						.getSelection()).getFirstElement();
//				openReviewEditorForPatch(patchCreator);
//			}
//		});
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
		patchTable.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		patchTable.setInput(parseAttachmentForPatches());
		setControl(composite);
	}

@Override
public void setVisible(boolean visible) {
System.err.println("setVisible " + visible);
	super.setVisible(visible);
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

			TaskDataModel model = ((CreateReviewWizard)getWizard()).getModel();
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
	public IPatchCreator getSelectedPatch() {
		return selectedPatch;
	}
}
