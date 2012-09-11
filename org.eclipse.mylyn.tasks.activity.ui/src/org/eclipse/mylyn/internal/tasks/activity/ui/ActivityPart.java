/*******************************************************************************
 * Copyright (c) 2012 Timur Achmetow and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Timur Achmetow - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.activity.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.activity.core.ActivityManager;
import org.eclipse.mylyn.internal.tasks.activity.ui.provider.ActivityRecordContentProvider;
import org.eclipse.mylyn.internal.tasks.activity.ui.provider.ActivityRecordLabelProvider;
import org.eclipse.mylyn.tasks.activity.core.IActivityStream;
import org.eclipse.mylyn.tasks.activity.core.TaskActivityScope;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Timur Achmetow
 */
@SuppressWarnings("restriction")
public class ActivityPart extends AbstractTaskEditorPart {
	public ActivityPart() {
		setPartName("Activity"); //$NON-NLS-1$
		setExpandVertically(true);
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		final Section section = createSection(parent, toolkit, false);
		section.setText("Activity"); //$NON-NLS-1$

		Composite activityComposite = toolkit.createComposite(section);
		activityComposite.setLayout(new GridLayout(1, false));
		activityComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createTreeViewer(toolkit, activityComposite);

		toolkit.paintBordersFor(activityComposite);
		section.setClient(activityComposite);
		setSection(toolkit, section);
	}

	private void createTreeViewer(FormToolkit toolkit, Composite activityComposite) {
		TreeViewer viewer = new TreeViewer(toolkit.createTree(activityComposite, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION));
		GridDataFactory.fillDefaults().hint(500, 100).grab(true, true).applyTo(viewer.getControl());
		viewer.setContentProvider(new ActivityRecordContentProvider());
		viewer.setLabelProvider(new DecoratingStyledCellLabelProvider(new ActivityRecordLabelProvider(), null, null));
		IActivityStream stream = new ActivityManager().getStream(new TaskActivityScope(getModel().getTask()));
		viewer.setInput(stream);
	}
}