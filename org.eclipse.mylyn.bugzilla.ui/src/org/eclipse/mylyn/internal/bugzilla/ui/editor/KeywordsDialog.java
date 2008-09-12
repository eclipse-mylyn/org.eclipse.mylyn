/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Shawn Minto
 */
public class KeywordsDialog extends Dialog {

	private final List<String> selectedKeywords;

	private final List<String> validKeywords;

	private CheckboxTableViewer keyWordsList;

	public KeywordsDialog(Shell shell, String selectedKeywords, java.util.List<String> validKeywords) {
		super(shell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		StringTokenizer st = new StringTokenizer(selectedKeywords, ",", false);
		this.selectedKeywords = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String s = st.nextToken().trim();
			this.selectedKeywords.add(s);
		}

		this.validKeywords = validKeywords;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Select Keywords");

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		composite.setLayoutData(gd);

		keyWordsList = CheckboxTableViewer.newCheckList(composite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		GridData keyWordsTextData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		keyWordsTextData.heightHint = 175;
		keyWordsTextData.widthHint = 160;
		keyWordsList.getTable().setLayoutData(keyWordsTextData);

		if (validKeywords != null) {

			keyWordsList.setContentProvider(new ITreeContentProvider() {

				public Object[] getChildren(Object parentElement) {
					if (parentElement instanceof Collection) {
						return ((Collection<?>) parentElement).toArray();
					}
					return null;
				}

				public Object getParent(Object element) {
					// TODO Auto-generated method stub
					return null;
				}

				public boolean hasChildren(Object element) {
					// TODO Auto-generated method stub
					return false;
				}

				public Object[] getElements(Object inputElement) {
					return getChildren(inputElement);
				}

				public void dispose() {
					// TODO Auto-generated method stub

				}

				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
					// TODO Auto-generated method stub

				}

			});

			Set<String> invalidKeywords = new HashSet<String>();

			keyWordsList.setInput(validKeywords);

			for (String keyword : selectedKeywords) {
				if (!keyWordsList.setChecked(keyword, true)) {
					invalidKeywords.add(keyword);
				}
			}

			selectedKeywords.removeAll(invalidKeywords);

		}

		keyWordsList.addCheckStateListener(new KeywordListener());

		parent.pack();

		return composite;
	}

	protected class KeywordListener implements ICheckStateListener {

		public void checkStateChanged(CheckStateChangedEvent event) {
			if (event.getChecked()) {
				selectedKeywords.add((String) event.getElement());
			} else {
				selectedKeywords.remove(event.getElement());
			}
		}

	}

	public List<String> getSelectedKeywords() {
		return selectedKeywords;
	}

	public String getSelectedKeywordsString() {
		StringBuffer keywords = new StringBuffer();

		for (String sel : selectedKeywords) {
			keywords.append(sel);
			keywords.append(",");
		}

		String keywordsString = keywords.toString();

		if (keywordsString.endsWith(",")) {
			keywordsString = keywordsString.substring(0, keywordsString.length() - 1);
		}

		return keywordsString;
	}

}