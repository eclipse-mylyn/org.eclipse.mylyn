/*******************************************************************************
 * Copyright (c) 2009, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Maarten Meijer - fix for bug 284559
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author David Shepherd
 * @author Steffen Pingel
 */
public class PriorityEditor {

	private Control control;

	private boolean ignoreNotification;

	private Label label;

	private Map<String, String> labelByValue;

	private Menu menu;

	private boolean readOnly;

	private ToolItem selectionButton;

	private ToolBar toolBar;

	private String value;

	private final TaskAttribute attribute;

	public PriorityEditor() {
		this(null);
	}

	public PriorityEditor(TaskAttribute attribute) {
		this.attribute = attribute;
	}

	public void createControl(final Composite parent, FormToolkit toolkit) {
		if (isReadOnly()) {
			label = toolkit.createLabel(parent, ""); //$NON-NLS-1$
			setControl(label);
		} else {
			toolBar = new ToolBar(parent, SWT.FLAT);
			selectionButton = new ToolItem(toolBar, SWT.DROP_DOWN);
			selectionButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (menu == null) {
						createMenu(toolBar);
					}
					Point location = parent.toDisplay(toolBar.getLocation());
					location.y = location.y + selectionButton.getBounds().height;
					if (value != null) {
						MenuItem[] items = menu.getItems();
						for (MenuItem item : items) {
							item.setSelection(value.equals(item.getData()));
						}
					}
					menu.setLocation(location);
					menu.setVisible(true);
				}
			});
			selectionButton.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					if (menu != null) {
						menu.dispose();
					}
				}
			});
			toolkit.adapt(toolBar);
			setControl(toolBar);
		}
	}

	private void createMenu(final ToolBar bar) {
		menu = new Menu(bar);
		for (String key : labelByValue.keySet()) {
			final MenuItem item = new MenuItem(menu, SWT.CHECK);
			item.setText(labelByValue.get(key));
			item.setData(key);
			item.setImage(getSmallImage(key));
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (!ignoreNotification) {
						value = (String) item.getData();
						valueChanged(value);
					}
				}
			});
		}
	}

	public Control getControl() {
		return control;
	}

	public Map<String, String> getLabelByValue() {
		return Collections.unmodifiableMap(labelByValue);
	}

	private ImageDescriptor getLargeImageDescriptor(PriorityLevel priorityLevel) {
		if (priorityLevel != null) {
			switch (priorityLevel) {
			case P1:
				return CommonImages.PRIORITY_1_LARGE;
			case P2:
				return CommonImages.PRIORITY_2_LARGE;
			case P3:
				return CommonImages.PRIORITY_3_LARGE;
			case P4:
				return CommonImages.PRIORITY_4_LARGE;
			case P5:
				return CommonImages.PRIORITY_5_LARGE;
			}
		}
		return CommonImages.PRIORITY_3_LARGE;
	}

	private Image getSmallImage(String value) {
		ImageDescriptor descriptor = getSmallImageDescriptor(value);
		if (descriptor != null) {
			return CommonImages.getImage(descriptor);
		}
		return null;
	}

	private ImageDescriptor getSmallImageDescriptor(String value) {
		PriorityLevel priorityLevel = getPriorityLevel(value);
		if (priorityLevel != null) {
			return TasksUiImages.getImageDescriptorForPriority(priorityLevel);
		}
		return null;
	}

	private PriorityLevel getPriorityLevel(String value) {
		if (attribute != null) {
			return attribute.getTaskData().getAttributeMapper().getPriorityLevel(attribute, value);
		}
		return PriorityLevel.fromString(value);
	}

	public String getToolTipText() {
		if (label != null) {
			return label.getToolTipText();
		}
		if (selectionButton != null) {
			return selectionButton.getToolTipText();
		}
		return null;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void select(String value, PriorityLevel level) {
		try {
			ignoreNotification = true;
			this.value = value;
			if (label != null) {
				label.setImage(CommonImages.getImage(getLargeImageDescriptor(level)));
			}
			if (selectionButton != null && toolBar != null) {
				selectionButton.setImage(CommonImages.getImage(getLargeImageDescriptor(level)));
			}
		} finally {
			ignoreNotification = false;
		}
	}

	private void setControl(Control control) {
		this.control = control;
	}

	public void setLabelByValue(Map<String, String> labelByValue) {
		this.labelByValue = new LinkedHashMap<String, String>(labelByValue);
		// the menu will be re-created with updated options when it is requested again
		if (menu != null) {
			menu.dispose();
		}
		menu = null;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setToolTipText(String text) {
		if (label != null) {
			label.setToolTipText(text);
		}
		if (selectionButton != null) {
			selectionButton.setToolTipText(text);
		}
	}

	protected void valueChanged(String key) {
	}

	public String getValue() {
		return value;
	}

}
