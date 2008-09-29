/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - indicate deprecated attachments, bug 215549
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonThemes;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorToolkit;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class AttachmentTableLabelProvider2 extends ColumnLabelProvider {

	private final AttachmentSizeFormatter sizeFormatter = AttachmentSizeFormatter.getInstance();

	private final IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();

	private static final String[] IMAGE_EXTENSIONS = { "jpg", "gif", "png", "tiff", "tif", "bmp" };

	private final TaskDataModel model;

	private final AttributeEditorToolkit attributeEditorToolkit;

	public AttachmentTableLabelProvider2(TaskDataModel model, AttributeEditorToolkit attributeEditorToolkit) {
		this.model = model;
		this.attributeEditorToolkit = attributeEditorToolkit;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		ITaskAttachment attachment = (ITaskAttachment) element;
		if (columnIndex == 0) {
			if (AttachmentUtil.isContext(attachment)) {
				return CommonImages.getImage(TasksUiImages.CONTEXT_TRANSFER);
			} else if (attachment.isPatch()) {
				return CommonImages.getImage(TasksUiImages.TASK_ATTACHMENT_PATCH);
			} else {
				String filename = attachment.getFileName();
				if (filename != null) {
					int dotIndex = filename.lastIndexOf('.');
					if (dotIndex != -1) {
						String fileType = filename.substring(dotIndex + 1);
						for (String element2 : IMAGE_EXTENSIONS) {
							if (element2.equalsIgnoreCase(fileType)) {
								return CommonImages.getImage(CommonImages.IMAGE_FILE);
							}
						}
					}
				}
				return WorkbenchImages.getImage(ISharedImages.IMG_OBJ_FILE);
			}
		} else {
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		ITaskAttachment attachment = (ITaskAttachment) element;
		switch (columnIndex) {
		case 0:
			if (AttachmentUtil.isContext(attachment)) {
				return " Task Context";
			} else if (attachment.isPatch()) {
				return " Patch";
			} else {
				return " " + attachment.getFileName();
			}
		case 1:
			return attachment.getDescription();
		case 2:
			Long length = attachment.getLength();
			if (length < 0) {
				return "-";
			}
			return sizeFormatter.format(length);
		case 3:
			return (attachment.getAuthor() != null) ? attachment.getAuthor().toString() : "";
		case 4:
			return (attachment.getCreationDate() != null) ? EditorUtil.formatDateTime(attachment.getCreationDate())
					: "";
		}
		return "unrecognized column";
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// ignore
	}

	@Override
	public void dispose() {
		// ignore
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// ignore
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// ignore
	}

	@Override
	public Color getForeground(Object element) {
		ITaskAttachment att = (ITaskAttachment) element;
		if (att.isDeprecated()) {
			return themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_COMPLETED);
		}
		return super.getForeground(element);
	}

	@Override
	public String getToolTipText(Object element) {
		ITaskAttachment attachment = (ITaskAttachment) element;
		StringBuilder sb = new StringBuilder();
		sb.append("File: ");
		sb.append(attachment.getFileName());
		if (attachment.getContentType() != null) {
			sb.append("\n");
			sb.append("Type: ");
			sb.append(attachment.getContentType());
		}
		return sb.toString();
		/*"\nFilename\t\t"  + attachment.getAttributeValue("filename")
			  +"ID\t\t\t"        + attachment.getAttributeValue("attachid")
		      + "\nDate\t\t\t"    + attachment.getAttributeValue("date")
		      + "\nDescription\t" + attachment.getAttributeValue("desc")
		      + "\nCreator\t\t"   + attachment.getCreator()
		      + "\nType\t\t\t"    + attachment.getAttributeValue("type")
		      + "\nURL\t\t\t"     + attachment.getAttributeValue("task.common.attachment.url");*/
	}

	@Override
	public Point getToolTipShift(Object object) {
		return new Point(5, 5);
	}

	@Override
	public int getToolTipDisplayDelayTime(Object object) {
		return 200;
	}

	@Override
	public int getToolTipTimeDisplayed(Object object) {
		return 5000;
	}

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		cell.setText(getColumnText(element, cell.getColumnIndex()));
		Image image = getColumnImage(element, cell.getColumnIndex());
		cell.setImage(image);
		cell.setBackground(getBackground(element));
		cell.setForeground(getForeground(element));
		cell.setFont(getFont(element));
	}

	@Override
	public Color getBackground(Object element) {
		ITaskAttachment attachment = (ITaskAttachment) element;
		if (model.hasIncomingChanges(attachment.getTaskAttribute())) {
			return attributeEditorToolkit.getColorIncoming();
		} else {
			return null;
		}
	}
}