/*******************************************************************************
 * Copyright (c) 2012 Timur Achmetow and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Timur Achmetow - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.activity.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.activity.core.ActivityManager;
import org.eclipse.mylyn.internal.tasks.activity.ui.provider.ActivityRecordContentProvider;
import org.eclipse.mylyn.internal.tasks.activity.ui.provider.ActivityRecordLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.editors.AbstractTaskEditorSection;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.tasks.activity.core.IActivityStream;
import org.eclipse.mylyn.tasks.activity.core.TaskActivityScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Timur Achmetow
 * @author Steffen Pingel
 */
@SuppressWarnings("restriction")
public class ActivityPart extends AbstractTaskEditorSection {

	public ActivityPart() {
		setPartName("Activity"); //$NON-NLS-1$
		setExpandVertically(true);
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		// do not show the part for unsubmitted tasks
		if (getTaskData().isNew()) {
			return;
		}
		super.createControl(parent, toolkit);
	}

	@Override
	protected Control createContent(FormToolkit toolkit, Composite parent) {
		Composite activityComposite = toolkit.createComposite(parent);
		activityComposite.setLayout(EditorUtil.createSectionClientLayout());

		TreeViewer viewer = new TreeViewer(toolkit.createTree(activityComposite, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION));
		GridDataFactory.fillDefaults().hint(500, 100).grab(true, true).applyTo(viewer.getControl());
		viewer.setContentProvider(new ActivityRecordContentProvider());
		viewer.setLabelProvider(new DecoratingStyledCellLabelProvider(new ActivityRecordLabelProvider(), null, null));
		IActivityStream stream = new ActivityManager().getStream(new TaskActivityScope(getModel().getTask()));
		viewer.setInput(stream);

		EditorUtil.addScrollListener(viewer.getTree());
		toolkit.paintBordersFor(activityComposite);

		return activityComposite;
	}

	@Override
	protected boolean shouldExpandOnCreate() {
		return false;
	}

}