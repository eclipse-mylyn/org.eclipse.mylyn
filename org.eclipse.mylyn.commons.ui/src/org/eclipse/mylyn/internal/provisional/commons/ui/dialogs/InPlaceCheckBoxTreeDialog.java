/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.provisional.commons.ui.dialogs;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.internal.provisional.commons.ui.EnhancedFilteredTree;
import org.eclipse.mylyn.internal.provisional.commons.ui.SubstringPatternFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Shawn Minto
 * @since 3.3
 */
public class InPlaceCheckBoxTreeDialog extends AbstractInPlaceDialog {

	private final Map<String, String> validValues;

	private CheckboxFilteredTree valueTree;

	private final Set<String> selectedValues;

	private final String dialogLabel;

	private class CheckboxFilteredTree extends EnhancedFilteredTree {

		public CheckboxFilteredTree(Composite parent, int treeStyle, PatternFilter filter) {
			super(parent, treeStyle, filter);
		}

		@Override
		protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
			return new CheckboxTreeViewer(parent, style);
		}

		@Override
		public CheckboxTreeViewer getViewer() {
			return (CheckboxTreeViewer) super.getViewer();
		}

	}

	public InPlaceCheckBoxTreeDialog(Shell shell, Control openControl, List<String> values,
			Map<String, String> validValues, String dialogLabel) {
		super(shell, SWT.RIGHT, openControl);
		Assert.isNotNull(values);
		Assert.isNotNull(validValues);
		Assert.isNotNull(dialogLabel);
		this.selectedValues = new HashSet<String>(values);
		this.validValues = validValues;
		this.dialogLabel = dialogLabel;
		setShellStyle(getShellStyle());
	}

	@Override
	protected Control createControl(Composite parent) {
		getShell().setText(dialogLabel);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		composite.setLayoutData(gd);

		valueTree = new CheckboxFilteredTree(composite, SWT.CHECK | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.BORDER, new SubstringPatternFilter());
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		gd.heightHint = 175;
		gd.widthHint = 160;
		CheckboxTreeViewer viewer = valueTree.getViewer();
		viewer.getControl().setLayoutData(gd);

		if (validValues != null) {

			viewer.setContentProvider(new ITreeContentProvider() {

				public Object[] getChildren(Object parentElement) {
					if (parentElement instanceof Map<?, ?>) {
						return ((Map<?, ?>) parentElement).keySet().toArray();
					}
					return null;
				}

				public Object getParent(Object element) {
					return null;
				}

				public boolean hasChildren(Object element) {
					return false;
				}

				public Object[] getElements(Object inputElement) {
					return getChildren(inputElement);
				}

				public void dispose() {
				}

				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}

			});

			viewer.setSorter(new ViewerSorter());
			viewer.setLabelProvider(new LabelProvider() {
				@Override
				public String getText(Object element) {
					if (element instanceof String) {
						return validValues.get(element);
					}
					return super.getText(element);
				}
			});
			viewer.setInput(validValues);

			Set<String> invalidValues = new HashSet<String>();

			// Remove any currently entered invalid values
			for (String value : selectedValues) {
				if (!validValues.containsKey(value)) {
					invalidValues.add(value);
				}
			}

			// Remove any unselected values
			for (String value : validValues.keySet()) {
				if (!viewer.setChecked(value, true)) {
					invalidValues.add(value);
				}
			}

			selectedValues.removeAll(invalidValues);

			viewer.setCheckedElements(selectedValues.toArray());

		}

		viewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					selectedValues.add((String) event.getElement());
				} else {
					selectedValues.remove(event.getElement());
				}
			}

		});

		return valueTree;
	}

	public Set<String> getSelectedValues() {
		return new HashSet<String>(selectedValues);
	}

}
