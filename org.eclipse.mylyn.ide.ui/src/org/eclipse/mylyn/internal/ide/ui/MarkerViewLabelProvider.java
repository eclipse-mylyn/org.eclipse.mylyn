/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ui;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.ui.ContextUiPrefContstants;
import org.eclipse.mylyn.internal.context.ui.UiUtil;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.markers.internal.ConcreteMarker;
import org.eclipse.ui.views.markers.internal.TableViewLabelProvider;

/**
 * @author Mik Kersten
 */
public class MarkerViewLabelProvider implements ITableLabelProvider, IColorProvider, IFontProvider {

	private TableViewLabelProvider provider;

	public MarkerViewLabelProvider(TableViewLabelProvider provider) {
		this.provider = provider;
	}

	public Font getFont(Object element) {
		if (element instanceof ConcreteMarker) {
			String handle = ContextCorePlugin.getDefault().getStructureBridge(
					((ConcreteMarker) element).getResource().getFileExtension()).getHandleForOffsetInObject(
					(element), 0);
			IInteractionElement node = ContextCorePlugin.getContextManager().getElement(handle);
			if (node != null) {
				if (node.getInterest().isLandmark() && !node.getInterest().isPropagated()) {
					return ContextUiPrefContstants.BOLD;
				}
			}
		} 
		return null;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return provider.getColumnImage(element, columnIndex);
	}

	public String getColumnText(Object element, int columnIndex) {
		return provider.getColumnText(element, columnIndex);
	}

	public Color getForeground(Object element) {
		if (element instanceof ConcreteMarker) {
			String handle = ContextCorePlugin.getDefault().getStructureBridge(
					((ConcreteMarker) element).getResource().getFileExtension()).getHandleForOffsetInObject(
					(element), 0);
			return UiUtil.getForegroundForElement(ContextCorePlugin.getContextManager().getElement(handle));
		} else {
			return null;
		}
	}

	public Color getBackground(Object element) {
		if (element instanceof ConcreteMarker) {
			String handle = ContextCorePlugin.getDefault().getStructureBridge(
					((ConcreteMarker) element).getResource().getFileExtension()).getHandleForOffsetInObject(
					(element), 0);
			return UiUtil.getBackgroundForElement(ContextCorePlugin.getContextManager().getElement(handle));
		} else {
			return null;
		}
	}

	/**
	 * TODO: handle listeners?
	 */
	public void addListener(ILabelProviderListener listener) {
		// provider.addListener(listener);
	}

	public void dispose() {
		provider.dispose();
	}

	public boolean isLabelProperty(Object element, String property) {
		return provider.isLabelProperty(element, property);
	}

	public void removeListener(ILabelProviderListener listener) {
		// removeListener(listener);
	}
}