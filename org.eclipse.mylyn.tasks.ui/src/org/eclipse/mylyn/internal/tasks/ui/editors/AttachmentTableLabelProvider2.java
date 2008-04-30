/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonColorsAndFonts;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.data.TaskAttachment;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
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

	private final AttributeEditorToolkit attributeToolkit;

	public AttachmentTableLabelProvider2(AttributeEditorToolkit attributeToolkit) {
		this.attributeToolkit = attributeToolkit;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		TaskAttachment attachment = (TaskAttachment) element;
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
		TaskAttachment attachment = (TaskAttachment) element;
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
//			if (attachment.isPatch()) {
//				return "patch";
//			} else {
			return attachment.getContentType();
//			}
		case 3:
			return sizeFormatter.format(attachment.getLength());
		case 4:
			return (attachment.getAuthor() != null) ? attachment.getAuthor().toString() : "";
		case 5:
			return (attachment.getCreationDate() != null) ? attributeToolkit.formatDate(attachment.getCreationDate())
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

	public Color getForeground(Object element, int columnIndex) {
		TaskAttachment att = (TaskAttachment) element;
		if (att.isDeprecated()) {
			return themeManager.getCurrentTheme().getColorRegistry().get(CommonColorsAndFonts.THEME_COLOR_COMPLETED);
		}
		return super.getForeground(element);
	}

	public Color getBackground(Object element, int columnIndex) {
		return super.getBackground(element);
	}

	public Font getFont(Object element, int columnIndex) {
		return super.getFont(element);
	}

	@Override
	public String getToolTipText(Object element) {
		TaskAttachment attachment = (TaskAttachment) element;
		return "File: " + attachment.getFileName();
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
}