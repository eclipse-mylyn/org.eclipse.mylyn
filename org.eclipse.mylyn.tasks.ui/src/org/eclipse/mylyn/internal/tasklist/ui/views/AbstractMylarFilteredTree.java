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

package org.eclipse.mylar.internal.tasklist.ui.views;

import java.lang.reflect.Field;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

public abstract class AbstractMylarFilteredTree extends FilteredTree {

	private static final int DELAY_REFRESH = 700;
	
	private static final int filterWidth = 70;
	
	private static final String LABEL_FIND = " Find:";
	
	private Job refreshJob;

	/**
	 * HACK: using reflectoin to gain access
	 */
	public AbstractMylarFilteredTree(Composite parent, int treeStyle, PatternFilter filter) {
		super(parent, treeStyle, filter);
		Field refreshField;
		try {
			refreshField = FilteredTree.class.getDeclaredField("refreshJob");
			refreshField.setAccessible(true);
			refreshJob = (Job)refreshField.get(this);
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Could not get refresh job", false);
		}
		setInitialText("");
	}
	
	@Override
    protected Composite createFilterControls(Composite parent){
		Composite container = new Composite(parent, SWT.NULL);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		container.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		container.setLayout(gridLayout); 
		
		Label label = new Label(container, SWT.LEFT);
		label.setText(LABEL_FIND);
				
		super.createFilterControls(container);
		
		filterText.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.ESC) {
					setFilterText("");
				}
			}

			public void keyReleased(KeyEvent e) {
				// ignore
			}
		});
		
		Composite status = createStatusComposite(container);
		if (status != null) {
			filterText.setLayoutData(new GridData(filterWidth, label.getSize().y));
		}
		
		return container;
	}
	
    protected abstract Composite createStatusComposite(Composite container);

	protected void textChanged() {
    	if (refreshJob == null) return;
    	refreshJob.cancel();
    	int refreshDelay = 0;
    	int textLength = filterText.getText().length();
        if (textLength > 0) {
                refreshDelay = DELAY_REFRESH / textLength;
        } 
        refreshJob.schedule(refreshDelay); 
    }

}