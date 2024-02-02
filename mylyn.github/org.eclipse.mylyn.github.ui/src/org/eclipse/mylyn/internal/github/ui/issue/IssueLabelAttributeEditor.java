/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.issue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.github.ui.GitHubImages;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskDiffUtil;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Editor part for viewing an issue's labels.
 */
@SuppressWarnings("restriction")
public class IssueLabelAttributeEditor extends AbstractAttributeEditor {

	/**
	 * LABEL_WIDTH
	 */
	public static final int LABEL_WIDTH = 80;

	/**
	 * LABEL_COLUMNS
	 */
	public static final int LABEL_COLUMNS = 12;

	private class NewLabelAction extends Action {

		public NewLabelAction() {
			super(Messages.IssueLabelAttributeEditor_ActionNewLabel, IAction.AS_PUSH_BUTTON);
		}

		@Override
		public void run() {
			InputDialog dialog = new InputDialog(getControl().getShell(),
					Messages.IssueLabelAttributeEditor_TitleNewLabel,
					Messages.IssueLabelAttributeEditor_DescriptionNewLabel, "", newText -> {
						if (newText == null || newText.trim().length() == 0) {
							return Messages.IssueLabelAttributeEditor_MessageEnterName;
						}
						return null;
					});
			if (Window.OK == dialog.open() && !getTaskAttribute().getValues()
					.contains(
							dialog.getValue())) {
				getTaskAttribute().addValue(dialog.getValue());
				markLabelsChanged();
				refreshLabels();
			}
		}

	}

	private class RemoveLabelAction extends Action {

		private String label;

		public RemoveLabelAction(String label) {
			super(Messages.IssueLabelAttributeEditor_ActionRemoveLabel, IAction.AS_PUSH_BUTTON);
			this.label = label;
		}

		@Override
		public void run() {
			if (getTaskAttribute().getValues().contains(label)) {
				getTaskAttribute().removeValue(label);
				markLabelsChanged();
				refreshLabels();
			}
		}

	}

	private class LabelAction extends Action {

		public LabelAction(String label) {
			super(label, IAction.AS_PUSH_BUTTON);
			setImageDescriptor(GitHubImages.DESC_GITHUB_ISSUE_LABEL);
		}

		@Override
		public void run() {
			if (!getTaskAttribute().getValues().contains(getText())) {
				getTaskAttribute().addValue(getText());
				markLabelsChanged();
				refreshLabels();
			}
		}
	}

	private Composite displayArea;

	private boolean layout = false;

	private Composite labelsArea;

	private List<CLabel> labelControls = new LinkedList<>();

	private FormToolkit toolkit;

	/**
	 * @param manager
	 * @param taskAttribute
	 */
	public IssueLabelAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.MULTIPLE));
	}

	private void refreshLabels() {
		for (CLabel labelControl : labelControls) {
			labelControl.dispose();
		}
		labelControls.clear();

		Image labelImage = GitHubImages.get(GitHubImages.GITHUB_ISSUE_LABEL_OBJ);
		List<String> labels = new LinkedList<>(getTaskAttribute().getValues());
		Collections.sort(labels, String.CASE_INSENSITIVE_ORDER);
		if (!labels.isEmpty()) {
			for (final String label : labels) {
				CLabel cLabel = new CLabel(labelsArea, SWT.NONE);
				MenuManager manager = new MenuManager();
				manager.setRemoveAllWhenShown(true);
				manager.addMenuListener(manager1 -> manager1.add(new RemoveLabelAction(label)));
				Menu menu = manager.createContextMenu(cLabel);
				cLabel.setMenu(menu);
				String shortened = TaskDiffUtil.shortenText(displayArea, label, LABEL_WIDTH);
				cLabel.setImage(labelImage);
				cLabel.setText(shortened);
				cLabel.setForeground(toolkit.getColors().getForeground());
				if (!shortened.equals(label)) {
					cLabel.setToolTipText(label);
				}
				labelControls.add(cLabel);
			}
		} else {
			labelControls.add(new CLabel(labelsArea, SWT.NONE));
		}

		if (layout) {
			displayArea.getParent().getParent().layout(true, true);
		}
	}

	private void markLabelsChanged() {
		getModel().attributeChanged(getTaskAttribute());
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor#createControl(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.forms.widgets.FormToolkit)
	 */
	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		this.toolkit = toolkit;
		displayArea = toolkit.createComposite(parent);
		displayArea.setBackgroundMode(SWT.INHERIT_FORCE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(displayArea);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(displayArea);

		final ToolBar toolbar = new ToolBar(displayArea, SWT.FLAT);
		toolkit.adapt(toolbar, false, false);
		final ToolItem addItem = new ToolItem(toolbar, SWT.DROP_DOWN);
		addItem.setImage(GitHubImages.get(GitHubImages.GITHUB_ADD_OBJ));
		addItem.setToolTipText(Messages.IssueLabelAttributeEditor_TooltipAddLabel);

		MenuManager manager = new MenuManager();
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(manager1 -> {
			manager1.add(new NewLabelAction());
			manager1.add(new Separator());
			List<String> labels = new LinkedList<>(getTaskAttribute().getOptions().values());
			labels.removeAll(getTaskAttribute().getValues());
			for (String label : labels) {
				manager1.add(new LabelAction(label));
			}
			manager1.update();
		});
		final Menu menu = manager.createContextMenu(displayArea);
		addItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Rectangle toolItemBounds = addItem.getBounds();
				Point location = toolbar.toDisplay(toolItemBounds.x, toolItemBounds.y + toolItemBounds.height);
				menu.setLocation(location);
				menu.setVisible(true);
			}

		});

		labelsArea = toolkit.createComposite(displayArea);
		labelsArea.setBackgroundMode(SWT.INHERIT_FORCE);
		labelsArea.setBackground(null);
		GridLayoutFactory.fillDefaults().numColumns(LABEL_COLUMNS).applyTo(labelsArea);
		GridDataFactory.swtDefaults().grab(true, true).applyTo(labelsArea);
		refreshLabels();
		setControl(displayArea);
		layout = true;
	}
}
