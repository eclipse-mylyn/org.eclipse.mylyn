/*******************************************************************************
 * Copyright (c) 2016, 2023 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Frank Becker - initial API and implementation
 *     Alexander Fedorov - remove guava
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * @since 3.22
 */
public class TableColumnDescriptorDialog extends TitleAreaDialog {

	private final TableColumnDescriptor[] columnDescriptors;

	private TableColumnDescriptor selectedTableColumnDescriptor;

	private Text nameTxt;

	private Text widthTxt;

	private Combo alignmentCombo;

	private Combo sortDirectionCombo;

	private Button defaultSortColumnCheckbox;

	private Button autosizeCheckbox;

	public TableColumnDescriptorDialog(Shell parentShell, @NonNull TableColumnDescriptor[] columnDescriptor) {
		super(parentShell);
		columnDescriptors = columnDescriptor;
		Assert.isTrue(columnDescriptors.length > 0);
	}

	@Override
	protected Control createContents(Composite parent) {
		getShell().setText(Messages.TableColumnDescriptorDialog_Change_Column_Settings);
		setTitle(Messages.TableColumnDescriptorDialog_Change_Column_Settings);

		Control control = super.createContents(parent);

		applyDialogFont(control);
		return control;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite parent2 = (Composite) super.createDialogArea(parent);

		Composite composite = new Composite(parent2, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(composite);

		createCenterArea(composite);

		composite.pack();
		return parent2;
	}

	private void createCenterArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		TableViewer attributeListViewer = new TableViewer(composite, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		Table table = (Table) attributeListViewer.getControl();
		GridDataFactory.fillDefaults()
				.grab(false, false)
				.align(SWT.LEFT, SWT.FILL)
				.hint(convertWidthInCharsToPixels(10), convertHeightInCharsToPixels(5))
				.applyTo(table);
		table.setFont(parent.getFont());
		attributeListViewer.setContentProvider(new ArrayContentProvider());
		attributeListViewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof TableColumnDescriptor tableColumnDescriptors) {
					return tableColumnDescriptors.getName();
				}
				return super.getText(element);
			}

		});
		attributeListViewer.setInput(columnDescriptors);
		attributeListViewer.addSelectionChangedListener(event -> {
			ISelection selection = event.getSelection();
			if (!selection.isEmpty()) {
				selectedTableColumnDescriptor = (TableColumnDescriptor) ((IStructuredSelection) selection)
						.getFirstElement();
				updateDetailAttributes();
			}
		});
		Composite detailComposite = new Composite(composite, SWT.BORDER);
		detailComposite.setLayout(new GridLayout(2, false));
		detailComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(detailComposite, SWT.None).setText(Messages.TableColumnDescriptorDialog_Name);
		nameTxt = new Text(detailComposite, SWT.BORDER);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(nameTxt);
		nameTxt.setEditable(false);
		nameTxt.setEnabled(false);

		new Label(detailComposite, SWT.None).setText(Messages.TableColumnDescriptorDialog_Width);
		widthTxt = new Text(detailComposite, SWT.BORDER);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(widthTxt);
		widthTxt.addModifyListener(e -> {
			try {
				String widthString = widthTxt.getText();
				if (widthString.isEmpty()) {
					setErrorMessage(Messages.TableColumnDescriptorDialog_please_enter_value_for_Width);
				} else {
					int newWidth = Integer.parseInt(widthTxt.getText());
					if (newWidth >= 0) {
						selectedTableColumnDescriptor.setWidth(newWidth);
						setErrorMessage(null);
					} else {
						setErrorMessage(Messages.TableColumnDescriptorDialog_Width_must_be_greater_or_equal_0);
					}
				}

			} catch (NumberFormatException exception) {
				setErrorMessage(Messages.TableColumnDescriptorDialog_Width_is_not_a_valid_number);
			}
		});
		new Label(detailComposite, SWT.None).setText(Messages.TableColumnDescriptorDialog_Alignment);
		alignmentCombo = new Combo(detailComposite, SWT.READ_ONLY);
		alignmentCombo.add(Messages.TableColumnDescriptorDialog_Left);
		alignmentCombo.add(Messages.TableColumnDescriptorDialog_Center);
		alignmentCombo.add(Messages.TableColumnDescriptorDialog_Right);
		alignmentCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				switch (alignmentCombo.getSelectionIndex()) {
					case 0:
						selectedTableColumnDescriptor.setAlignment(SWT.LEFT);
						break;
					case 1:
						selectedTableColumnDescriptor.setAlignment(SWT.CENTER);
						break;
					case 2:
						selectedTableColumnDescriptor.setAlignment(SWT.RIGHT);
						break;
				}
			}

		});

		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(alignmentCombo);
		new Label(detailComposite, SWT.None);
		autosizeCheckbox = new Button(detailComposite, SWT.CHECK);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(autosizeCheckbox);
		autosizeCheckbox.setText(Messages.TableColumnDescriptorDialog_autosize);
		autosizeCheckbox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedTableColumnDescriptor.setAutoSize(autosizeCheckbox.getSelection());
			}
		});
		new Label(detailComposite, SWT.None);
		defaultSortColumnCheckbox = new Button(detailComposite, SWT.CHECK);
		GridDataFactory.swtDefaults()
				.align(SWT.FILL, SWT.BEGINNING)
				.grab(true, false)
				.applyTo(defaultSortColumnCheckbox);
		defaultSortColumnCheckbox.setText(Messages.TableColumnDescriptorDialog_DefaultSortColumn);
		defaultSortColumnCheckbox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selected = defaultSortColumnCheckbox.getSelection();
				selectedTableColumnDescriptor.setDefaultSortColumn(selected);
				if (selected) {
					for (TableColumnDescriptor tableColumnDescriptor : columnDescriptors) {
						if (tableColumnDescriptor == selectedTableColumnDescriptor) {
							continue;
						}
						tableColumnDescriptor.setDefaultSortColumn(false);
					}
				}
			}
		});

		new Label(detailComposite, SWT.None).setText(Messages.TableColumnDescriptorDialog_Sort_Direction);
		sortDirectionCombo = new Combo(detailComposite, SWT.READ_ONLY);
		sortDirectionCombo.add(""); //$NON-NLS-1$
		sortDirectionCombo.add(Messages.TableColumnDescriptorDialog_Up);
		sortDirectionCombo.add(Messages.TableColumnDescriptorDialog_Down);
		sortDirectionCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				switch (sortDirectionCombo.getSelectionIndex()) {
					case 0:
						selectedTableColumnDescriptor.setSortDirection(SWT.NONE);
						break;
					case 1:
						selectedTableColumnDescriptor.setSortDirection(SWT.UP);
						break;
					case 2:
						selectedTableColumnDescriptor.setSortDirection(SWT.DOWN);
						break;
				}
			}
		});

		selectedTableColumnDescriptor = columnDescriptors[0];
		attributeListViewer.setSelection(new StructuredSelection(selectedTableColumnDescriptor));
	}

	public TableColumnDescriptor[] getColumnDescriptors() {
		return columnDescriptors;
	}

	protected void updateDetailAttributes() {
		nameTxt.setText(selectedTableColumnDescriptor.getName());
		widthTxt.setText(Integer.toString(selectedTableColumnDescriptor.getWidth()));
		defaultSortColumnCheckbox.setSelection(selectedTableColumnDescriptor.isDefaultSortColumn());
		autosizeCheckbox.setSelection(selectedTableColumnDescriptor.isAutoSize());
		if (selectedTableColumnDescriptor.getAlignment() == SWT.LEFT) {
			alignmentCombo.select(0);
		} else if (selectedTableColumnDescriptor.getAlignment() == SWT.CENTER) {
			alignmentCombo.select(1);
		} else if (selectedTableColumnDescriptor.getAlignment() == SWT.RIGHT) {
			alignmentCombo.select(2);
		}
		if (selectedTableColumnDescriptor.getSortDirection() == SWT.UP) {
			sortDirectionCombo.select(1);
		} else if (selectedTableColumnDescriptor.getSortDirection() == SWT.DOWN) {
			sortDirectionCombo.select(2);
		} else {
			sortDirectionCombo.select(0);
		}
	}

}
