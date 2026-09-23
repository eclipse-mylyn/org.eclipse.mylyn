/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.commons.ui.compatibility.CommonThemes;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Mik Kersten
 * @author Frank Becker
 * @since 3.7
 */
public abstract class GradientDrawer {

	private final Listener CATEGORY_GRADIENT_DRAWER = new Listener() {
		@Override
		public void handleEvent(Event event) {
			GC gc = event.gc;
			// keep the native selection, its text color is unreadable on the category background
			if (shouldApplyGradient(event) && gc != null && (event.detail & SWT.SELECTED) == 0) {
				Scrollable scrollable = (Scrollable) event.widget;

				Rectangle area = scrollable.getClientArea();
				Rectangle rect = event.getBounds();

				/* Paint the selection beyond the end of last column */
				expandRegion(event, scrollable, gc, area);

				Color oldBackground = gc.getBackground();
				gc.setBackground(categoryBackground);
				gc.fillRectangle(0, rect.y, area.width, rect.height);
				gc.setBackground(oldBackground);

				/* Mark as Background being handled */
				event.detail &= ~SWT.BACKGROUND;
			}
		}

		private void expandRegion(Event event, Scrollable scrollable, GC gc, Rectangle area) {
			int columnCount;
			if (scrollable instanceof Table) {
				columnCount = ((Table) scrollable).getColumnCount();
			} else {
				columnCount = ((Tree) scrollable).getColumnCount();
			}

			if (event.index == columnCount - 1 || columnCount == 0) {
				int width = area.x + area.width - event.x;
				if (width > 0) {
					Region region = new Region();
					gc.getClipping(region);
					region.add(event.x, event.y, width, event.height);
					gc.setClipping(region);
					region.dispose();
				}
			}
		}
	};

	protected abstract boolean shouldApplyGradient(Event event);

	private final IPropertyChangeListener THEME_CHANGE_LISTENER = event -> {
		if (event.getProperty().equals(IThemeManager.CHANGE_CURRENT_THEME)
				|| event.getProperty().equals(CommonThemes.COLOR_SCHEDULED_PAST)
				|| event.getProperty().equals(CommonThemes.COLOR_COMPLETED_TODAY)
				|| event.getProperty().equals(CommonThemes.COLOR_COMPLETED)
				|| event.getProperty().equals(CommonThemes.COLOR_OVERDUE)
				|| event.getProperty().equals(CommonThemes.COLOR_SCHEDULED_TODAY)
				|| event.getProperty().equals(CommonThemes.COLOR_SCHEDULED_PAST)
				|| event.getProperty().equals(CommonThemes.COLOR_SCHEDULED_TODAY)
				|| event.getProperty().equals(CommonThemes.COLOR_SCHEDULED_THIS_WEEK)
				|| event.getProperty().equals(CommonThemes.COLOR_TASK_ACTIVE)
				|| CommonThemes.isCommonTheme(event.getProperty())) {
			configureGradientColors();
		}
	};

	private boolean gradientListenerAdded = false;

	private final IThemeManager themeManager;

	private Color categoryBackground;

	private final TreeViewer treeViewer;

	public GradientDrawer(IThemeManager theThemeManager, TreeViewer treeViewer) {
		themeManager = theThemeManager;
		this.treeViewer = treeViewer;
		treeViewer.getControl().addDisposeListener(e -> {
			if (themeManager != null) {
				themeManager.removePropertyChangeListener(THEME_CHANGE_LISTENER);
			}
		});
		configureGradientColors();
		themeManager.addPropertyChangeListener(THEME_CHANGE_LISTENER);
	}

	private void configureGradientColors() {
		categoryBackground = themeManager.getCurrentTheme()
				.getColorRegistry()
				.get(CommonThemes.COLOR_CATEGORY);
		if (!gradientListenerAdded && categoryBackground != null) {
			getViewer().getTree().addListener(SWT.EraseItem, CATEGORY_GRADIENT_DRAWER);
			gradientListenerAdded = true;
		}
	}

	public TreeViewer getViewer() {
		return treeViewer;
	}

}
