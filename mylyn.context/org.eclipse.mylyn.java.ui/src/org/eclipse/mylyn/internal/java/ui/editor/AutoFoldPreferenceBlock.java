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
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui.editor;

import org.eclipse.jdt.ui.text.folding.IJavaFoldingPreferenceBlock;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Mik Kersten
 */
public class AutoFoldPreferenceBlock implements IJavaFoldingPreferenceBlock {

	// XXX never read
	// private IPreferenceStore preferences;
	// private Map checkBoxes = new HashMap();
	// private Text minimumLines;

	public AutoFoldPreferenceBlock() {
		// preferences = ContextCore.getPreferenceStore(); XXX
		// never used
	}

	@Override
	public Control createControl(Composite parent) {
		Composite inner = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout.verticalSpacing = 3;
		layout.marginWidth = 0;
		inner.setLayout(layout);

		Label label = new Label(inner, SWT.LEFT);
		label.setText(Messages.AutoFoldPreferenceBlock_Elements_of_low_interest_will_be_automatically_folded);

		// Label label= new Label(inner, SWT.LEFT);
		// label.setText( "collapse.title"); //$NON-NLS-1$

		// addCheckBox(inner, "collapse.header", "collapse.header", 0);
		// //$NON-NLS-1$
		// addCheckBox(inner, "collapse.imports",
		// FoldingKeys.COLLAPSE_IMPORTS, 0); //$NON-NLS-1$
		// addCheckBox(inner, "collapse.inner_type",
		// FoldingKeys.COLLAPSE_INNER_TYPES, 0); //$NON-NLS-1$
		// addCheckBox(inner, "collapse.static_initializers",
		// FoldingKeys.COLLAPSE_STATICS, 0); //$NON-NLS-1$
		//
		// addCheckBox(inner, "collapse.comment_blocks",
		// FoldingKeys.COLLAPSE_COMMENT_BLOCKS, 0); //$NON-NLS-1$
		// addCheckBox(inner, "collapse.javadocs",
		// FoldingKeys.COLLAPSE_JAVADOCS, 0); //$NON-NLS-1$

		// Label label2 = new Label(inner, SWT.LEFT);
		// label2.setText( "minSize.title");
		//
		// minimumLines = new Text(inner, SWT.BORDER | SWT.SINGLE);
		// GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		// gd.horizontalSpan= 1;
		// gd.grabExcessVerticalSpace= false;
		// minimumLines.setLayoutData(gd);

		return inner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.ui.text.folding.IJavaFoldingPreferenceBlock#initialize()
	 */
	@Override
	public void initialize() {

		// Iterator it= checkBoxes.keySet().iterator();
		// while (it.hasNext()) {
		// Button b= (Button) it.next();
		// String key= (String) checkBoxes.get(b);
		// b.setSelection(preferences.getBoolean(key));
		// }

		// minimumLines.setText(String.valueOf(preferences.getInt(FoldingKeys.MINIMUM_SIZE)));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.ui.text.folding.IJavaFoldingPreferenceBlock#performOk()
	 */
	@Override
	public void performOk() {
		// Iterator it = checkBoxes.keySet().iterator();
		// while(it.hasNext()) {
		// Button b = (Button) it.next();
		// String key = (String) checkBoxes.get(b);
		// preferences.setValue(key, b.getSelection());
		// }
		//
		// int minLines = Integer.parseInt(minimumLines.getText());
		// preferences.setValue(FoldingKeys.MINIMUM_SIZE, minLines);

	}

	// private Button addCheckBox(Composite parent, String label, String key,
	// int indentation) {
	// Button checkBox= new Button(parent, SWT.CHECK);
	// checkBox.setText(label);
	//
	// GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
	// gd.horizontalIndent= indentation;
	// gd.horizontalSpan= 1;
	// gd.grabExcessVerticalSpace= false;
	// checkBox.setLayoutData(gd);
	//
	// checkBoxes.put(checkBox, key);
	//
	// return checkBox;
	// }

	@Override
	public void performDefaults() {
		initialize();
	}

	@Override
	public void dispose() {
		// don't care if we are disposed

	}

}
