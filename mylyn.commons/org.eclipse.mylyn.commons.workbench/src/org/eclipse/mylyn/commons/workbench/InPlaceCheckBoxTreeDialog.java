/*******************************************************************************
 * Copyright (c) 2004, 2024 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/
package org.eclipse.mylyn.commons.workbench;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.ui.dialogs.AbstractInPlaceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * @author Shawn Minto
 * @since 3.7
 */
public class InPlaceCheckBoxTreeDialog extends AbstractInPlaceDialog {

	private final Map<String, String> validValues;

	private CheckboxFilteredTree valueTree;

	private final Set<String> selectedValues;

	private final String dialogLabel;

	private final Map<String, String> validDescriptions;

	private Text description;

	private class CheckboxFilteredTree extends FilteredTree {

		public CheckboxFilteredTree(Composite parent, int treeStyle, PatternFilter filter) {
			super(parent, treeStyle, filter, true, true);
		}

		@Override
		protected WorkbenchJob doCreateRefreshJob() {
			WorkbenchJob job = super.doCreateRefreshJob();
			job.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					if (event.getResult() != null && event.getResult().isOK() && !getViewer().getTree().isDisposed()) {
						getViewer().setCheckedElements(selectedValues.toArray());
					}
				}
			});
			return job;
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

	/**
	 * @since 3.22
	 */
	public InPlaceCheckBoxTreeDialog(Shell shell, Control openControl, List<String> values,
			Map<String, String> validValues, String dialogLabel, Map<String, String> validDescriptions) {
		super(shell, SWT.RIGHT, openControl);
		Assert.isNotNull(values);
		Assert.isNotNull(validValues);
		Assert.isNotNull(dialogLabel);
		selectedValues = new HashSet<>(values);
		this.validValues = validValues;
		this.dialogLabel = dialogLabel;
		this.validDescriptions = validDescriptions;
		setShellStyle(getShellStyle());
	}

	public InPlaceCheckBoxTreeDialog(Shell shell, Control openControl, List<String> values,
			Map<String, String> validValues, String dialogLabel) {
		this(shell, openControl, values, validValues, dialogLabel, null);
	}

	@Override
	protected Control createControl(Composite parent) {
		getShell().setText(dialogLabel);

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = MARGIN_SIZE;
		layout.marginWidth = MARGIN_SIZE;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		composite.setLayoutData(gd);

		valueTree = new CheckboxFilteredTree(composite,
				SWT.CHECK | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER, new SubstringPatternFilter());
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		gd.heightHint = 175;
		gd.widthHint = 160;
		CheckboxTreeViewer viewer = valueTree.getViewer();
		viewer.getControl().setLayoutData(gd);
		if (validDescriptions != null) {
			Label label = new Label(composite, SWT.NONE);
			label.setText("Description:"); //$NON-NLS-1$
			gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
			gd.widthHint = 160;
			label.setLayoutData(gd);
			description = new Text(composite, SWT.WRAP);
			gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
			gd.heightHint = 25;
			gd.widthHint = 160;
			description.setLayoutData(gd);
		}

		if (validValues != null) {

			viewer.setContentProvider(new ITreeContentProvider() {

				@Override
				public Object[] getChildren(Object parentElement) {
					if (parentElement instanceof Map<?, ?>) {
						return ((Map<?, ?>) parentElement).keySet().toArray();
					}
					return null;
				}

				@Override
				public Object getParent(Object element) {
					return null;
				}

				@Override
				public boolean hasChildren(Object element) {
					return false;
				}

				@Override
				public Object[] getElements(Object inputElement) {
					return getChildren(inputElement);
				}

				@Override
				public void dispose() {
				}

				@Override
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}

			});

			//viewer.setSorter(new ViewerSorter());
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

			Set<String> invalidValues = new HashSet<>();

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

		viewer.addCheckStateListener(event -> {
			if (event.getChecked()) {
				selectedValues.add((String) event.getElement());
			} else {
				selectedValues.remove(event.getElement());
			}
		});
		if (validDescriptions != null) {
			viewer.addSelectionChangedListener(event -> {
				TreeSelection treeSelection = (TreeSelection) event.getSelection();
				Object firstSelectedElement = treeSelection.getFirstElement();
				if (validDescriptions.containsKey(firstSelectedElement)) {
					description.setText(validDescriptions.get(firstSelectedElement));
				} else {
					description.setText(""); //$NON-NLS-1$
				}
			});
		}

		return valueTree;
	}

	public Set<String> getSelectedValues() {
		return new HashSet<>(selectedValues);
	}

}
