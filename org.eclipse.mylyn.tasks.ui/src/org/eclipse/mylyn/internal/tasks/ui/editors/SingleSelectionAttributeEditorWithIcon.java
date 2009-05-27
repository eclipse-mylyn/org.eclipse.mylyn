/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author David Shepherd
 */
public class SingleSelectionAttributeEditorWithIcon extends AbstractAttributeEditor {

	private static ImageDescriptor getImageDescriptor(TaskAttribute taskAttribute, ITaskMapping mapping) {
		TaskAttribute root = taskAttribute.getTaskData().getRoot();
		if (root.getMappedAttribute(TaskAttribute.PRIORITY).equals(taskAttribute)) {
			PriorityLevel priorityLevel = mapping.getPriorityLevel();
			if (priorityLevel != null) {
				return getLargerImageDescriptorForPriority(priorityLevel);
			}
		}
		return null;
	}

	private static ImageDescriptor getLargerImageDescriptorForPriority(PriorityLevel priorityLevel) {
		switch (priorityLevel) {
		case P1:
			return CommonImages.PRIORITY_1_24;
		case P2:
			return CommonImages.PRIORITY_2_24;
		case P3:
			return CommonImages.PRIORITY_3_24;
		case P4:
			return CommonImages.PRIORITY_4_24;
		case P5:
			return CommonImages.PRIORITY_5_24;
		default:
			return CommonImages.PRIORITY_3_24;
		}
	}

	private static ImageDescriptor getSmallerImageDescriptor(TaskAttribute taskAttribute, String value) {
		TaskAttribute root = taskAttribute.getTaskData().getRoot();
		if (root.getMappedAttribute(TaskAttribute.PRIORITY).equals(taskAttribute)) {
			PriorityLevel priorityLevel = PriorityLevel.fromString(value);
			if (priorityLevel != null) {
				return TasksUiImages.getImageDescriptorForPriority(priorityLevel);
			}
		}
		return null;
	}

	private boolean ignoreNotification;

	private Label label;

	private ITaskMapping mapping;

	private Menu menu;

	private ToolItem selectionButton;

	private ToolBar toolBar;

	public SingleSelectionAttributeEditorWithIcon(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
	}

	@Override
	public void createControl(final Composite parent, FormToolkit toolkit) {
		AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(getModel().getTaskRepository()
				.getConnectorKind());
		mapping = connector.getTaskMapping(getModel().getTaskData());

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
					menu.setLocation(location);
					menu.setVisible(true);
				}
			});
			toolkit.adapt(toolBar);
			setControl(toolBar);
		}
		getModel().addModelListener(new TaskDataModelListener() {
			@Override
			public void attributeChanged(TaskDataModelEvent event) {
				if (getTaskAttribute().equals(event.getTaskAttribute())) {
					refresh();
				}
			}
		});
		refresh();
	}

	private void createMenu(final ToolBar bar) {
		menu = new Menu(bar);
		final List<MenuItem> items = new ArrayList<MenuItem>();
		Map<String, String> labelByValue = getAttributeMapper().getOptions(getTaskAttribute());
		for (String key : labelByValue.keySet()) {
			final MenuItem item = new MenuItem(menu, SWT.CHECK);
			items.add(item);
			item.setText(labelByValue.get(key));
			item.setData(key);
			item.setImage(getSmallerImageForValue(key));
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (!ignoreNotification) {
						setValue((String) item.getData());
						item.setSelection(true);
						for (MenuItem menuItem : items) {
							if (item != menuItem) {
								menuItem.setSelection(false);
							}
						}
					}
				}
			});
		}
	}

	@Override
	public void dispose() {
		if (menu != null && !menu.isDisposed()) {
			menu.dispose();
		}
		super.dispose();
	}

	private Image getImageForCurrentValue() {
		Image image = null;
		if (mapping != null) {
			ImageDescriptor descriptor = getImageDescriptor(getTaskAttribute(), mapping);
			if (descriptor != null) {
				image = CommonImages.getImage(descriptor);
				return image;
			}
		}
		return null;
	}

	private Image getSmallerImageForValue(String value) {
		ImageDescriptor descriptor = getSmallerImageDescriptor(getTaskAttribute(), value);
		if (descriptor != null) {
			return CommonImages.getImage(descriptor);
		}
		return null;

	}

	public String getValueLabel() {
		return getAttributeMapper().getValueLabel(getTaskAttribute());
	}

	@Override
	public void refresh() {
		try {
			ignoreNotification = true;
			if (label != null) {
				label.setImage(getImageForCurrentValue());
				label.setToolTipText(getValueLabel());
			}
			if (selectionButton != null && toolBar != null) {
				// the menu will be re-created with updated options when it is requested again
				if (menu != null) {
					menu.dispose();
				}
				menu = null;

				selectionButton.setImage(getImageForCurrentValue());
				selectionButton.setToolTipText(getValueLabel());
				toolBar.getParent().layout();
			}
		} finally {
			ignoreNotification = false;
		}
	}

	public void setValue(String value) {
		String oldValue = getAttributeMapper().getValue(getTaskAttribute());
		if (!oldValue.equals(value)) {
			getAttributeMapper().setValue(getTaskAttribute(), value);
			attributeChanged();
			refresh();
		}
	}
}
