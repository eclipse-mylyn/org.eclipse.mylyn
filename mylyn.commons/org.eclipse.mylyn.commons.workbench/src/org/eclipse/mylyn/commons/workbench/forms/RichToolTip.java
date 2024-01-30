/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Kevin Barnes, IBM Corporation - fix for bug 277974
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench.forms;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.mylyn.commons.ui.GradientToolTip;
import org.eclipse.mylyn.commons.ui.PlatformUiUtil;
import org.eclipse.mylyn.internal.commons.workbench.CommonsWorkbenchPlugin;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.IFormColors;

/**
 * @author Mik Kersten
 * @author Eric Booth
 * @author Leo Dos Santos
 * @author Steffen Pingel
 * @since 3.7
 */
public abstract class RichToolTip extends GradientToolTip {

	private final static int X_SHIFT = PlatformUiUtil.getToolTipXShift();

	private final static int Y_SHIFT = 1;

	private final Control control;

	private Object data;

	private boolean enabled;

	private boolean triggeredByMouse = true;

	private ColumnViewer viewer;

	private boolean visible;

	public RichToolTip(Control control) {
		super(control);
		this.control = control;
		setEnabled(true);
		setShift(new Point(1, 1));
	}

	@Override
	protected void afterHideToolTip(Event event) {
		triggeredByMouse = true;
		visible = false;
	}

	protected Object computeData(Widget hoverWidget) {
		return hoverWidget.getData();
	}

	private boolean contains(int x, int y) {
		if (control instanceof Scrollable) {
			return ((Scrollable) control).getClientArea().contains(x, y);
		} else {
			return control.getBounds().contains(x, y);
		}
	}

	public void dispose() {
		hide();
	}

	private Rectangle getBounds(Widget widget) {
		if (widget instanceof ToolItem w) {
			return w.getBounds();
		}
		if (widget instanceof TableItem w) {
			return w.getBounds();
		}
		if (widget instanceof TreeItem w) {
			return w.getBounds();
		}
		return null;
	}

	public Object getData() {
		return data;
	}

	@Override
	public Point getLocation(Point tipSize, Event event) {
		Widget widget = getTipWidget(event);
		if (widget != null) {
			Rectangle bounds = getBounds(widget);
			if (bounds != null) {
				if (data instanceof ViewerCell) {
					bounds = ((ViewerCell) data).getBounds();
				}
				return control.toDisplay(bounds.x + X_SHIFT, bounds.y + bounds.height + Y_SHIFT);
			}
		}
		return super.getLocation(tipSize, event);//control.toDisplay(event.x + xShift, event.y + yShift);
	}

	protected Widget getTipWidget(Event event) {
		Point widgetPosition = new Point(event.x, event.y);
		Widget widget = event.widget;
		if (widget instanceof ToolBar w) {
			return w.getItem(widgetPosition);
		}
		if (widget instanceof Table w) {
			return w.getItem(widgetPosition);
		}
		if (widget instanceof Tree w) {
			return w.getItem(widgetPosition);
		}

		return widget;
	}

	protected Color getTitleColor() {
		return CommonsWorkbenchPlugin.getDefault().getFormColors(control.getDisplay()).getColor(IFormColors.TITLE);
	}

	public ColumnViewer getViewer() {
		return viewer;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isTriggeredByMouse() {
		return triggeredByMouse;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setViewer(ColumnViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	protected boolean shouldCreateToolTip(Event event) {
		data = null;

		if (isTriggeredByMouse() && !enabled) {
			return false;
		}

		if (super.shouldCreateToolTip(event)) {
			Widget tipWidget = getTipWidget(event);
			if (tipWidget != null) {
				if (viewer != null) {
					data = viewer.getCell(new Point(event.x, event.y));
				} else {
					Rectangle bounds = getBounds(tipWidget);
					if (tipWidget instanceof ScalingHyperlink) {
						data = computeData(tipWidget);
					} else if (bounds != null && contains(bounds.x, bounds.y)) {
						data = computeData(tipWidget);
					}
				}
			}
		}

		if (data == null) {
			hide();
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void show(Point location) {
		triggeredByMouse = false;
		super.show(location);
	}

}
