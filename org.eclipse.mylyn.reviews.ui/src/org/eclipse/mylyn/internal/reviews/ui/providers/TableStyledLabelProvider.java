/*******************************************************************************
 * Copyright (c) 2012 Ericsson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.providers;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

/**
 * Supports independent styling of individual table columns.
 * 
 * @author Miles Parker
 */
public abstract class TableStyledLabelProvider extends LabelProvider implements IStyledLabelProvider,
		ITableLabelProvider {

	public static class TableColumnProvider extends LabelProvider implements IStyledLabelProvider {

		private final String title;

		private final int weight;

		private final int minimumSize;

		private final boolean fill;

		public TableColumnProvider(String title, int weight, int minimumSize, boolean fill) {
			this.title = title;
			this.weight = weight;
			this.minimumSize = minimumSize;
			this.fill = fill;
		}

		public String getTitle() {
			return title;
		}

		public int getWeight() {
			return weight;
		}

		public int getMinimumSize() {
			return minimumSize;
		}

		public boolean isFillAvailable() {
			return fill;
		}

		public StyledString getStyledText(Object element) {
			String columnText = getText(element);
			if (columnText == null) {
				columnText = "";
			}
			return new StyledString(columnText);
		}

		public boolean isStyled() {
			return false;
		}

		public String getToolTipText(Object element) {
			return null;
		}
	}

	public static abstract class AdaptingTableColumnProvider extends TableColumnProvider {
		private final TableColumnProvider targetProvider;

		public AdaptingTableColumnProvider(TableColumnProvider targetProvider, String title, int weight,
				int minimumSize, boolean fill) {
			super(title, weight, minimumSize, fill);
			this.targetProvider = targetProvider;
		}

		@Override
		public StyledString getStyledText(Object element) {
			Object columnObject = adapt(element);
			if (columnObject != null) {
				return targetProvider.getStyledText(columnObject);
			}
			return new StyledString();
		};

		@Override
		public Image getImage(Object element) {
			Object columnObject = adapt(element);
			if (columnObject != null) {
				return targetProvider.getImage(columnObject);
			}
			return null;
		};

		@Override
		public String getToolTipText(Object element) {
			Object columnObject = adapt(element);
			if (columnObject != null) {
				return targetProvider.getToolTipText(columnObject);
			}
			return targetProvider.getToolTipText(element);
		};

		@Override
		public String getText(Object element) {
			Object columnObject = adapt(element);
			if (columnObject != null) {
				return targetProvider.getText(columnObject);
			}
			return "";
		};

		public abstract Object adapt(Object element);
	}

	/**
	 * Noop. Note: Subclasses must manage disposal of any member resources by overriding {@link #doDispose()}. Viewers
	 * are expected to call {@link #doDispose()} from viewer. This is necessary because internal delegating providers
	 * and viewers will call dispose, preventing reuse of this label provider.
	 */
	@Override
	public final void dispose() {
	}

	/**
	 * Override to manage resource disposal.
	 */
	public void doDispose() {
		super.dispose();
	}

	public Image getColumnImage(Object element, int columnIndex) {
		TableColumnProvider columnProvider = getColumnProviders()[columnIndex];
		return columnProvider.getImage(element);
	}

	public String getColumnText(Object element, int columnIndex) {
		TableColumnProvider columnProvider = getColumnProviders()[columnIndex];
		return columnProvider.getText(element);
	}

	public abstract TableColumnProvider[] getColumnProviders();
}
