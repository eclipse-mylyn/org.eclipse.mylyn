/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Pawel Niewiadomski - fix for bug 287832
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.ui.dialogs.AbstractInPlaceDialog;
import org.eclipse.mylyn.commons.ui.dialogs.IInPlaceDialogListener;
import org.eclipse.mylyn.commons.ui.dialogs.InPlaceDialogEvent;
import org.eclipse.mylyn.commons.workbench.InPlaceCheckBoxTreeDialog;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;

/**
 * @author Shawn Minto
 * @author Sam Davis
 */
public class CheckboxMultiSelectAttributeEditor extends AbstractAttributeEditor {

	private Text valueText;

	private Composite parent;

	private Button button;

	private boolean suppressRefresh;

	public CheckboxMultiSelectAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.MULTIPLE));
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		if (isReadOnly()) {
			valueText = new Text(parent, SWT.FLAT | SWT.READ_ONLY | SWT.WRAP);
			valueText.setFont(EditorUtil.TEXT_FONT);
			toolkit.adapt(valueText, false, false);
			valueText.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
			valueText.setToolTipText(getDescription());
			refresh();
			setControl(valueText);
		} else {
			this.parent = parent;

			Composite composite = toolkit.createComposite(parent);
			composite.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
			GridLayout layout = new GridLayout(2, false);
			layout.marginWidth = 0;
			layout.marginBottom = 0;
			layout.marginLeft = 0;
			layout.marginRight = 0;
			layout.marginTop = 0;
			layout.marginHeight = 0;
			composite.setLayout(layout);

			valueText = toolkit.createText(composite, "", SWT.FLAT | SWT.WRAP); //$NON-NLS-1$
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(valueText);
			valueText.setFont(EditorUtil.TEXT_FONT);
			valueText.setEditable(false);
			valueText.setToolTipText(getDescription());

			button = toolkit.createButton(composite, "", SWT.ARROW | SWT.DOWN); //$NON-NLS-1$
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.TOP).applyTo(button);
			button.addSelectionListener(createSelectionListener());
			toolkit.adapt(valueText, false, false);
			refresh();
			setControl(composite);
		}
	}

	protected SelectionListener createSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final List<String> values = getValueList();
				final InPlaceCheckBoxTreeDialog selectionDialog = createInPlaceCheckBoxTreeDialog(values);
				addEventListener(values, selectionDialog);
				selectionDialog.open();
			}

		};
	}

	protected List<String> getValueList() {
		final List<String> values = getValues();
		return values;
	}

	protected InPlaceCheckBoxTreeDialog createInPlaceCheckBoxTreeDialog(List<String> values) {
		Map<String, String> validValues = getAttributeMapper().getOptions(getTaskAttribute());
		final InPlaceCheckBoxTreeDialog selectionDialog = new InPlaceCheckBoxTreeDialog(WorkbenchUtil.getShell(),
				button, values, validValues,
				NLS.bind(Messages.CheckboxMultiSelectAttributeEditor_Select_X, getLabel()));
		return selectionDialog;
	}

	private void addEventListener(final List<String> values, final InPlaceCheckBoxTreeDialog selectionDialog) {
		selectionDialog.addEventListener(new IInPlaceDialogListener() {

			public void buttonPressed(InPlaceDialogEvent event) {
				suppressRefresh = true;
				try {
					if (event.getReturnCode() == Window.OK) {
						Set<String> newValues = selectionDialog.getSelectedValues();
						if (!new HashSet<String>(values).equals(newValues)) {
							setValues(new ArrayList<String>(newValues));
							refresh();
						}
					} else if (event.getReturnCode() == AbstractInPlaceDialog.ID_CLEAR) {
						Set<String> newValues = new HashSet<String>();
						if (!new HashSet<String>(values).equals(newValues)) {
							setValues(new ArrayList<String>(newValues));
							refresh();
						}
					}
				} finally {
					suppressRefresh = false;
				}
			}
		});
	}

	/**
	 * Update scroll bars of the enclosing form.
	 *
	 * @see Section#reflow()
	 */
	private void reflow() {
		Composite c = parent;
		while (c != null) {
			c.setRedraw(false);
			c = c.getParent();
			if (c instanceof SharedScrolledComposite || c instanceof Shell) {
				break;
			}
		}
		c = parent;
		while (c != null) {
			c.layout(true);
			c = c.getParent();
			if (c instanceof SharedScrolledComposite) {
				((SharedScrolledComposite) c).reflow(true);
				break;
			}
		}
		c = parent;
		while (c != null) {
			c.setRedraw(true);
			c = c.getParent();
			if (c instanceof SharedScrolledComposite || c instanceof Shell) {
				break;
			}
		}
	}

	public List<String> getValues() {
		return getAttributeMapper().getValues(getTaskAttribute());
	}

	public List<String> getValuesLabels() {
		return getAttributeMapper().getValueLabels(getTaskAttribute());
	}

	public void setValues(List<String> newValues) {
		getAttributeMapper().setValues(getTaskAttribute(), newValues);
		attributeChanged();
	}

	@Override
	protected void decorateIncoming(Color color) {
		super.decorateIncoming(color);
		if (valueText != null && !valueText.isDisposed()) {
			valueText.setBackground(color);
		}
		if (button != null && !button.isDisposed()) {
			button.setBackground(color);
		}
	}

	@Override
	public void refresh() {
		if (valueText == null || valueText.isDisposed()) {
			return;
		}

		StringBuilder valueString = new StringBuilder();
		List<String> values = getValuesLabels();
		Collections.sort(values);
		for (int i = 0; i < values.size(); i++) {
			valueString.append(values.get(i));
			if (i != values.size() - 1) {
				valueString.append(", "); //$NON-NLS-1$
			}
		}
		valueText.setText(valueString.toString());
		if (valueText != null && parent != null && parent.getParent() != null
				&& parent.getParent().getParent() != null) {
			Point size = valueText.getSize();
			// subtract 1 from size for border
			Point newSize = valueText.computeSize(size.x - 1, SWT.DEFAULT);
			if (newSize.y != size.y) {
				reflow();
			}
		}
	}

	@Override
	public boolean shouldAutoRefresh() {
		return !suppressRefresh;
	}

	protected Button getButton() {
		return button;
	}

}
