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

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.lang.reflect.Field;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.internal.WorkbenchMessages;

/**
 * @author Mik Kersten
 */
public abstract class AbstractFilteredTree extends FilteredTree {

	private static final int filterWidth = 70;

	public static final String LABEL_FIND = " Find:";

	private Job refreshJob;

	private AdaptiveRefreshPolicy refreshPolicy;

	private Composite progressComposite;

	private boolean showProgress = false;

	/**
	 * HACK: using reflection to gain access
	 */
	public AbstractFilteredTree(Composite parent, int treeStyle, PatternFilter filter) {
		super(parent, treeStyle, filter);
		Field refreshField;
		try {
			refreshField = FilteredTree.class.getDeclaredField("refreshJob");
			refreshField.setAccessible(true);
			refreshJob = (Job) refreshField.get(this);
			refreshPolicy = new AdaptiveRefreshPolicy(refreshJob, super.getFilterControl());
		} catch (Exception e) {
			StatusManager.fail(e, "Could not get refresh job", false);
		}
		setInitialText("");
	}

	@Override
	protected void createControl(Composite parent, int treeStyle) {
		super.createControl(parent, treeStyle);

		// Override superclass layout settings...
		GridLayout layout = (GridLayout) getLayout();
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
	}

	@Override
	protected Control createTreeControl(Composite parent, int style) {
		progressComposite = createProgressComposite(parent);
		progressComposite.setVisible(false);
		((GridData) progressComposite.getLayoutData()).exclude = true;
		return super.createTreeControl(parent, style);
	}

	@Override
	protected Composite createFilterControls(Composite parent) {
		GridLayout gridLayout = new GridLayout(7, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 2;
		gridLayout.verticalSpacing = 0;
		parent.setLayout(gridLayout);

		Label label = new Label(parent, SWT.NONE);
		label.setText(LABEL_FIND);

		// from super
		createFilterText(parent);
		createClearText(parent);
		if (filterToolBar != null) {
			filterToolBar.update(false);
			// initially there is no text to clear
			filterToolBar.getControl().setVisible(false);
		}

		GridData gd = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
		gd.minimumWidth = filterWidth;
		filterText.setLayoutData(gd);
		filterText.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.ESC) {
					setFilterText("");
				}
			}
		});
		
		createWorkingSetComposite(parent);
		createStatusComposite(parent);
		return parent;
	}

	private void createClearText(Composite parent) {
		// only create the button if the text widget doesn't support one
		// natively
		if ((filterText.getStyle() & SWT.CANCEL) == 0) {
			filterToolBar = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL);
			filterToolBar.createControl(parent);

			IAction clearTextAction = new Action("", IAction.AS_PUSH_BUTTON) {//$NON-NLS-1$
				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.jface.action.Action#run()
				 */
				public void run() {
					clearText();
				}
			};

			clearTextAction.setToolTipText(WorkbenchMessages.FilteredTree_ClearToolTip);
			clearTextAction.setImageDescriptor(TasksUiImages.FIND_CLEAR);
			clearTextAction.setDisabledImageDescriptor(TasksUiImages.FIND_CLEAR_DISABLED);
			filterToolBar.add(clearTextAction);
		}
	}
	
	protected abstract Composite createProgressComposite(Composite container);

	protected abstract Composite createWorkingSetComposite(Composite container);
	
	protected abstract Composite createStatusComposite(Composite container);

	@Override
	protected void textChanged() {
		if (refreshPolicy != null) {
			refreshPolicy.textChanged(filterText.getText());
		}
		
		if (TasksUiPlugin.getDefault().isEclipse_3_3_workbench()) {
			// bug 165353 work-around for premature return at
			// FilteredTree.java:374
			updateToolbar(true);
		} 
	}

	protected Job getRefreshJob() {
		return refreshJob;
	}

	public AdaptiveRefreshPolicy getRefreshPolicy() {
		return refreshPolicy;
	}

	public boolean isShowProgress() {
		return showProgress;
	}

	public void setShowProgress(boolean showProgress) {
		this.showProgress = showProgress;
		progressComposite.setVisible(showProgress);
		((GridData) progressComposite.getLayoutData()).exclude = !showProgress;
		getParent().getParent().layout(true, true);
	}
}