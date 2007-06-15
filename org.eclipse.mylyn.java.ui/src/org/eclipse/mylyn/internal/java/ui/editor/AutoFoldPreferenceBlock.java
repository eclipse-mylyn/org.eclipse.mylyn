/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui.editor;

import org.eclipse.jdt.ui.text.folding.IJavaFoldingPreferenceBlock;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class AutoFoldPreferenceBlock implements IJavaFoldingPreferenceBlock {

	// XXX never read
	// private IPreferenceStore preferences;
	// private Map checkBoxes = new HashMap();
	// private Text minimumLines;

	public AutoFoldPreferenceBlock() {
		// preferences = ContextCorePlugin.getDefault().getPreferenceStore(); XXX
		// never used
	}

	public Control createControl(Composite parent) {
		try {
			Composite inner = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout(1, true);
			layout.verticalSpacing = 3;
			layout.marginWidth = 0;
			inner.setLayout(layout);

			Label label = new Label(inner, SWT.LEFT);
			label.setText("Elements of low interest will be automatically folded.");

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
		} catch (Throwable t) {
			StatusManager.fail(t, "Could not create folding preferences page", true);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.ui.text.folding.IJavaFoldingPreferenceBlock#initialize()
	 */
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

	public void performDefaults() {
		initialize();
	}

	public void dispose() {
		// don't care if we are disposed

	}

}
