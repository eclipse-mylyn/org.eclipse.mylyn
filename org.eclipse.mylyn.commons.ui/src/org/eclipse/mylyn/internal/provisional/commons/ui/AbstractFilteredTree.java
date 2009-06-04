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

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.lang.reflect.Field;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.commons.ui.CommonsUiPlugin;
import org.eclipse.mylyn.internal.commons.ui.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Mik Kersten
 */
public abstract class AbstractFilteredTree extends EnhancedFilteredTree {

	private static final int FILTER_WIDTH_MIN = 60;

	private static final int FILTER_WIDTH_MAX = 300;

	private static final float FILTER_WIDTH_RATIO = 0.3f;

	public static final String LABEL_FIND = Messages.AbstractFilteredTree_Find;

	private Job refreshJob;

	private AdaptiveRefreshPolicy refreshPolicy;

	private Composite progressComposite;

	private Composite searchComposite;

	private boolean showProgress = false;

	/**
	 * XXX: using reflection to gain access
	 * 
	 * @param parent
	 * @param treeStyle
	 * @param filter
	 */
	public AbstractFilteredTree(Composite parent, int treeStyle, PatternFilter filter) {
		super(parent, treeStyle, filter);
		try {
			// TODO e3.4 override doCreateRefreshJob() instead
			Field refreshField = FilteredTree.class.getDeclaredField("refreshJob"); //$NON-NLS-1$
			refreshField.setAccessible(true);
			refreshJob = (Job) refreshField.get(this);
			refreshPolicy = new AdaptiveRefreshPolicy(refreshJob);
		} catch (Exception e) {
			CommonsUiPlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR, CommonsUiPlugin.ID_PLUGIN, "Could not get refresh job", e)); //$NON-NLS-1$
		}
		setInitialText(""); //$NON-NLS-1$
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
//		progressComposite.setVisible(false);
//		((GridData) progressComposite.getLayoutData()).exclude = true;

		searchComposite = createSearchComposite(parent);
		if (searchComposite != null) {
			searchComposite.setVisible(false);
			((GridData) searchComposite.getLayoutData()).exclude = true;
		}

		return super.createTreeControl(parent, style);
	}

	@Override
	protected Composite createFilterControls(final Composite parent) {
		// replace filterComposite by a new composite
		filterComposite = new Composite(parent.getParent(), SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginTop = 3;
		gridLayout.marginBottom = 3;
		gridLayout.verticalSpacing = 0;
		filterComposite.setLayout(gridLayout);

		Label label = new Label(filterComposite, SWT.NONE);
		label.setText(LABEL_FIND);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(label);

		// let FilteredTree create the find and clear control
		super.createFilterControls(parent);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).hint(FILTER_WIDTH_MIN,
				SWT.DEFAULT).minSize(FILTER_WIDTH_MIN, SWT.DEFAULT).applyTo(parent);
		filterComposite.addControlListener(new ControlAdapter() {
			boolean handlingEvents;

			@Override
			public void controlResized(ControlEvent e) {
				if (handlingEvents) {
					return;
				}
				try {
					handlingEvents = true;
					Point size = parent.getParent().getSize();
					int width = Math.max(FILTER_WIDTH_MIN, (int) (size.x * FILTER_WIDTH_RATIO));
					((GridData) parent.getLayoutData()).widthHint = Math.min(width, FILTER_WIDTH_MAX);
					parent.getParent().layout();
				} finally {
					handlingEvents = false;
				}
			}
		});
		filterText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.ESC) {
					setFilterText(""); //$NON-NLS-1$
				}
			}
		});
		((GridData) filterText.getLayoutData()).verticalAlignment = SWT.CENTER;

		// move original filterComposite on new filterComposite
		parent.setParent(filterComposite);

		Composite workingSetComposite = createActiveWorkingSetComposite(filterComposite);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).applyTo(workingSetComposite);

		Composite activeTaskComposite = createActiveTaskComposite(filterComposite);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(true, false).applyTo(activeTaskComposite);

		gridLayout.numColumns = filterComposite.getChildren().length;

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
				@Override
				public void run() {
					clearText();
				}
			};

			clearTextAction.setToolTipText(Messages.AbstractFilteredTree_Clear);
			clearTextAction.setImageDescriptor(CommonImages.FIND_CLEAR);
			clearTextAction.setDisabledImageDescriptor(CommonImages.FIND_CLEAR_DISABLED);
			filterToolBar.add(clearTextAction);
		}
	}

	protected abstract Composite createProgressComposite(Composite container);

	protected abstract Composite createActiveWorkingSetComposite(Composite container);

	protected abstract Composite createActiveTaskComposite(Composite container);

	protected Composite createSearchComposite(Composite container) {
		return null;
	}

	@Override
	protected void textChanged() {
		// this call allows the filtered tree to preserve the selection when the clear button is used.
		// It is necessary to correctly set the private narrowingDown flag in the super class. 
		// Note that the scheduling of the refresh job that is done in the super class will be overridden 
		// by the call to refreshPolicy.textChanged().
		super.textChanged();

		if (refreshPolicy != null) {
			refreshPolicy.textChanged(getFilterString());
		}
		// bug 165353 work-around for premature return at FilteredTree.java:374
		updateToolbar(true);
	}

	@Deprecated
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

	public void setShowSearch(boolean showSearch) {
		if (searchComposite != null) {
			searchComposite.setVisible(showSearch);
			((GridData) searchComposite.getLayoutData()).exclude = !showSearch;
			getParent().getParent().layout(true, true);
		}
	}
}
