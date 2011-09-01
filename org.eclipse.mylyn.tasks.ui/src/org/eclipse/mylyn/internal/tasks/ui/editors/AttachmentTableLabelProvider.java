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
 *     Perforce - fixes for bug 318505
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonThemes;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorToolkit;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 * @author Kevin Sawicki
 */
public class AttachmentTableLabelProvider extends ColumnLabelProvider {

	private final TaskDataModel model;

	private final AttributeEditorToolkit attributeEditorToolkit;

	private final AttachmentColumnDefinition[] definitions;

	public AttachmentTableLabelProvider(TaskDataModel model, AttributeEditorToolkit attributeEditorToolkit,
			AttachmentColumnDefinition[] definitions) {
		this.model = model;
		this.attributeEditorToolkit = attributeEditorToolkit;
		this.definitions = definitions;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		ITaskAttachment attachment = (ITaskAttachment) element;
		return definitions[columnIndex].getColumnImage(attachment, columnIndex);
	}

	public String getColumnText(Object element, int columnIndex) {
		ITaskAttachment attachment = (ITaskAttachment) element;
		return definitions[columnIndex].getColumnText(attachment, columnIndex);
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
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
			IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
			return themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_COMPLETED);
		}
		return super.getForeground(element);
	}

	@Override
	public String getToolTipText(Object element) {
		ITaskAttachment attachment = (ITaskAttachment) element;
		StringBuilder sb = new StringBuilder();
		sb.append(Messages.AttachmentTableLabelProvider_File_);
		sb.append(attachment.getFileName());
		if (attachment.getContentType() != null) {
			sb.append("\n"); //$NON-NLS-1$
			sb.append(Messages.AttachmentTableLabelProvider_Type_);
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
		if (model != null && attributeEditorToolkit != null) {
			ITaskAttachment attachment = (ITaskAttachment) element;
			if (model.hasIncomingChanges(attachment.getTaskAttribute())) {
				return attributeEditorToolkit.getColorIncoming();
			}
		}
		return null;
	}

}