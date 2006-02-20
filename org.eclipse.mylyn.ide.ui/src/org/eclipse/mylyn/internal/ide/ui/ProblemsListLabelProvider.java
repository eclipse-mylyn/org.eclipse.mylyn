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

package org.eclipse.mylar.internal.ide.ui;

import org.eclipse.jface.viewers.*;
import org.eclipse.mylar.internal.ui.MylarUiPrefContstants;
import org.eclipse.mylar.internal.ui.UiUtil;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.swt.graphics.*;
import org.eclipse.ui.views.markers.internal.ProblemMarker;
import org.eclipse.ui.views.markers.internal.TableViewLabelProvider;

/**
 * @author Mik Kersten
 */
public class ProblemsListLabelProvider implements ITableLabelProvider, IColorProvider, IFontProvider {

	private TableViewLabelProvider provider;

	public ProblemsListLabelProvider(TableViewLabelProvider provider) {
		this.provider = provider;
	}

	public Font getFont(Object element) {
		if (element instanceof ProblemMarker) {
			String handle = MylarPlugin.getDefault().getStructureBridge(
					((ProblemMarker) element).getResource().getFileExtension()).getHandleForOffsetInObject(
					((ProblemMarker) element), 0);
			IMylarElement node = MylarPlugin.getContextManager().getElement(handle);
			if (node != null) {
				if (node.getInterest().isLandmark() && !node.getInterest().isPropagated()) {
					return MylarUiPrefContstants.BOLD;
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
		if (element instanceof ProblemMarker) {
			String handle = MylarPlugin.getDefault().getStructureBridge(
					((ProblemMarker) element).getResource().getFileExtension()).getHandleForOffsetInObject(
					((ProblemMarker) element), 0);
			return UiUtil.getForegroundForElement(MylarPlugin.getContextManager().getElement(handle));
		} else {
			return null;
		}
	}

	public Color getBackground(Object element) {
		if (element instanceof ProblemMarker) {
			String handle = MylarPlugin.getDefault().getStructureBridge(
					((ProblemMarker) element).getResource().getFileExtension()).getHandleForOffsetInObject(
					((ProblemMarker) element), 0);
			return UiUtil.getBackgroundForElement(MylarPlugin.getContextManager().getElement(handle));
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