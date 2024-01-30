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
import org.eclipse.swt.widgets.Display;
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
			if (shouldApplyGradient(event) && gc != null) {
				Scrollable scrollable = (Scrollable) event.widget;

				Rectangle area = scrollable.getClientArea();
				Rectangle rect = event.getBounds();

				/* Paint the selection beyond the end of last column */
				expandRegion(event, scrollable, gc, area);

				/* Draw Gradient Rectangle */
				Color oldForeground = gc.getForeground();
				Color oldBackground = gc.getBackground();

				gc.setForeground(categoryGradientEnd);
				gc.drawLine(0, rect.y, area.width, rect.y);

				gc.setForeground(categoryGradientStart);
				gc.setBackground(categoryGradientEnd);

				// gc.setForeground(categoryGradientStart);
				// gc.setBackground(categoryGradientEnd);
				// gc.setForeground(new Clr(Display.getCurrent(), 255, 0, 0));

				gc.fillGradientRectangle(0, rect.y + 1, area.width, rect.height, true);

				/* Bottom Line */
				// gc.setForeground();
				gc.setForeground(categoryGradientEnd);
				gc.drawLine(0, rect.y + rect.height - 1, area.width, rect.y + rect.height - 1);

				gc.setForeground(oldForeground);
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

	private Color categoryGradientStart;

	private Color categoryGradientEnd;

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
		categoryGradientStart = themeManager.getCurrentTheme()
				.getColorRegistry()
				.get(CommonThemes.COLOR_CATEGORY_GRADIENT_START);
		categoryGradientEnd = themeManager.getCurrentTheme()
				.getColorRegistry()
				.get(CommonThemes.COLOR_CATEGORY_GRADIENT_END);

		boolean customized = true;
		if (categoryGradientStart != null && categoryGradientStart.getRed() == 240
				&& categoryGradientStart.getGreen() == 240 && categoryGradientStart.getBlue() == 240
				&& categoryGradientEnd != null && categoryGradientEnd.getRed() == 220
				&& categoryGradientEnd.getGreen() == 220 && categoryGradientEnd.getBlue() == 220) {
			customized = false;
		}

		if (!gradientListenerAdded && categoryGradientStart != null
				&& !categoryGradientStart.equals(categoryGradientEnd)) {
			getViewer().getTree().addListener(SWT.EraseItem, CATEGORY_GRADIENT_DRAWER);
			gradientListenerAdded = true;
			if (!customized) {
				// Set parent-based colors
				Color parentBackground = getViewer().getTree().getParent().getBackground();
				double GRADIENT_TOP;// = 1.05;// 1.02;
				double GRADIENT_BOTTOM;// = .995;// 1.035;

				// Constants to darken or lighten the default gradients
				if ("Windows 7".equals(System.getProperty("os.name"))) { //$NON-NLS-1$//$NON-NLS-2$
					GRADIENT_TOP = 1.05;
					GRADIENT_BOTTOM = 1.09;
				} else {
					GRADIENT_TOP = 1.05;
					GRADIENT_BOTTOM = .995;
				}

				int red = Math.max(0, Math.min(255, (int) (parentBackground.getRed() * GRADIENT_TOP)));
				int green = Math.max(0, Math.min(255, (int) (parentBackground.getGreen() * GRADIENT_TOP)));
				int blue = Math.max(0, Math.min(255, (int) (parentBackground.getBlue() * GRADIENT_TOP)));

				try {
					categoryGradientStart = new Color(Display.getDefault(), red, green, blue);
				} catch (Exception e) {
					categoryGradientStart = getViewer().getTree().getParent().getBackground();
//					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not set color: " + red //$NON-NLS-1$
//							+ ", " + green + ", " + blue, e)); //$NON-NLS-1$ //$NON-NLS-2$
				}
				red = Math.min(255, Math.max(0, (int) (parentBackground.getRed() / GRADIENT_BOTTOM)));
				green = Math.min(255, Math.max(0, (int) (parentBackground.getGreen() / GRADIENT_BOTTOM)));
				blue = Math.min(255, Math.max(0, (int) (parentBackground.getBlue() / GRADIENT_BOTTOM)));

				try {
					categoryGradientEnd = new Color(Display.getDefault(), red, green, blue);
				} catch (Exception e) {
					categoryGradientEnd = getViewer().getTree().getParent().getBackground();
//					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not set color: " + red //$NON-NLS-1$
//							+ ", " + green + ", " + blue, e)); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		} else if (categoryGradientStart != null && categoryGradientStart.equals(categoryGradientEnd)) {
			getViewer().getTree().removeListener(SWT.EraseItem, CATEGORY_GRADIENT_DRAWER);
			gradientListenerAdded = false;
		}
	}

	public TreeViewer getViewer() {
		return treeViewer;
	}

}
