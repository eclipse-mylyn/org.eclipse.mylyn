/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - indicate deprecated attachments, bug 215549
 *     Perforce - fixes for bug 318505
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.ui.TableColumnDescriptor;
import org.eclipse.mylyn.commons.workbench.CommonImageManger;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 * @author Kevin Sawicki
 */
public class AttachmentTableLabelProvider extends StyledCellLabelProvider {

	private final AttachmentSizeFormatter sizeFormatter = AttachmentSizeFormatter.getInstance();

	private final CommonImageManger imageManager = new CommonImageManger();

	@Override
	public void update(ViewerCell cell) {
		cell.setImage(getColumnImage((ITaskAttachment) cell.getElement(), cell.getColumnIndex()));
		StyledString str = buildTextFromEventIndex(cell.getColumnIndex(), (ITaskAttachment) cell.getElement());
		cell.setText(str.getString());
		cell.setStyleRanges(str.getStyleRanges());
		super.update(cell);
	}

	@Override
	protected void measure(Event event, Object element) {
		super.measure(event, element);
		measure4MulitlineColumn(event, element);
	}

	protected void measure4MulitlineColumn(Event event, Object element) {
		ColumnViewer viewer = getViewer();
		if (viewer instanceof TableViewer) {
			TableColumn col = ((TableViewer) viewer).getTable().getColumn(event.index);
			TableColumnDescriptor colDes = (TableColumnDescriptor) col
					.getData(TableColumnDescriptor.TABLE_COLUMN_DESCRIPTOR_KEY);
			if (colDes != null && !colDes.isAutoSize()) {
				return;
			}
			event.width = ((TableViewer) viewer).getTable().getColumn(event.index).getWidth();
			if (event.width == 0) {
				return;
			}

			ITaskAttachment attachment = (ITaskAttachment) element;
			Point size = event.gc.textExtent(buildTextFromEventIndex(event.index, attachment).getString());
			event.height = size.y;
			if (event.index == 0 || event.index == 3) {
				size.x = size.x + 22;

			} else {
				size.x = size.x + 3;
			}
			if (size.x > event.width) {
				event.width = size.x;
				((TableViewer) viewer).getTable().getColumn(event.index).setWidth(size.x);
			}
		}
	}

	public Image getColumnImage(Object element, int columnIndex) {
		ITaskAttachment attachment = (ITaskAttachment) element;
		if (columnIndex == 0) {
			if (AttachmentUtil.isContext(attachment)) {
				return imageManager.getImage(TasksUiImages.CONTEXT_TRANSFER);
			} else if (attachment.isPatch()) {
				return imageManager.getImage(TasksUiImages.TASK_ATTACHMENT_PATCH);
			} else {
				return imageManager.getFileImage(attachment.getFileName());
			}
		} else if (columnIndex == 3 && attachment.getAuthor() != null) {
			return getAuthorImage(attachment.getAuthor(), attachment.getTaskRepository());
		}
		return null;
	}

	/**
	 * Get author image for a specified repository person and task repository
	 *
	 * @param person
	 * @param repository
	 * @return author image
	 */
	protected Image getAuthorImage(IRepositoryPerson person, TaskRepository repository) {
		if (repository != null && person != null && person.matchesUsername(repository.getUserName())) {
			return imageManager.getImage(CommonImages.PERSON_ME);
		} else {
			return imageManager.getImage(CommonImages.PERSON);
		}
	}

	public StyledString buildTextFromEventIndex(int index, ITaskAttachment attachment) {
		StyledString text = new StyledString();
		switch (index) {
		case 0:
			if (AttachmentUtil.isContext(attachment)) {
				text.append(Messages.AttachmentTableLabelProvider_Task_Context);
			} else if (attachment.isPatch()) {
				text.append(Messages.AttachmentTableLabelProvider_Patch);
			} else {
				text.append(" " + attachment.getFileName()); //$NON-NLS-1$
			}
			break;
		case 1:
			if (attachment.getDescription() != null) {
				text.append(attachment.getDescription());
			}
			break;
		case 2:
			long length = attachment.getLength();
			if (length < 0) {
				text.append("-"); //$NON-NLS-1$
			}
			text.append(sizeFormatter.format(length));
			break;
		case 3:
			String autherText = (attachment.getAuthor() != null) ? attachment.getAuthor().toString() : ""; //$NON-NLS-1$
			String[] autherPart = autherText.split(" <"); //$NON-NLS-1$
			text.append(autherPart[0] + " ", StyledString.COUNTER_STYLER); //$NON-NLS-1$
			if (autherPart.length > 1) {
				text.append("<" + autherPart[1], StyledString.COUNTER_STYLER); //$NON-NLS-1$
			}
			break;
		case 4:
			text.append((attachment.getCreationDate() != null)
					? EditorUtil.formatDateTime(attachment.getCreationDate())
					: ""); //$NON-NLS-1$
			break;
		case 5:
			// FIXME add id to ITaskAttachment
			text.append(getAttachmentId(attachment));
			break;
		}
		return text;
	}

	public static String getAttachmentId(ITaskAttachment attachment) {
		String a = attachment.getUrl();
		if (a != null) {
			int i = a.indexOf("?id="); //$NON-NLS-1$
			if (i != -1) {
				return a.substring(i + 4);
			}
		}
		return ""; //$NON-NLS-1$
	}

}
