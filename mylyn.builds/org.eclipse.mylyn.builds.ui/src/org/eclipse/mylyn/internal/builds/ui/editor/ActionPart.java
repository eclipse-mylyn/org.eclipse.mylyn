/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.mylyn.internal.builds.ui.actions.ShowBuildOutputAction;
import org.eclipse.mylyn.internal.builds.ui.actions.ShowTestResultsAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author Steffen Pingel
 */
public class ActionPart extends AbstractBuildEditorPart {

	public ActionPart() {
	}

	@Override
	protected Control createContent(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		final ShowTestResultsAction testResultsAction = new ShowTestResultsAction();
		testResultsAction.selectionChanged(new StructuredSelection(getInput(IBuild.class)));
		if (testResultsAction.isEnabled()) {
			Label label = toolkit.createLabel(composite, ""); //$NON-NLS-1$
			label.setImage(CommonImages.getImage(BuildImages.JUNIT));

			Link link = new Link(composite, SWT.FLAT);
			link.setText("Show tests results in <a>JUnit View</a>.");
			link.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					testResultsAction.run();
				}
			});
			toolkit.adapt(link, false, false);
		}

		final ShowBuildOutputAction buildOutputAction = new ShowBuildOutputAction();
		buildOutputAction.selectionChanged(new StructuredSelection(getInput(IBuild.class)));
		Link link;
		if (buildOutputAction.isEnabled()) {
			Label label = toolkit.createLabel(composite, ""); //$NON-NLS-1$
			label.setImage(CommonImages.getImage(BuildImages.CONSOLE));

			link = new Link(composite, SWT.FLAT);
			link.setText("Show output in <a>Console</a>.");
			link.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					buildOutputAction.run();
				}
			});
			toolkit.adapt(link, false, false);
		}

//		label = toolkit.createLabel(composite, "");
//		label.setImage(CommonImages.getImage(BuildImages.VIEW_HISTORY));
//
//		link = new Link(composite, SWT.FLAT);
//		link.setText("Show builds in <a>History</a>.");
//		toolkit.adapt(link, false, false);

		return composite;
	}

	@Override
	public Control createControl(Composite parent, FormToolkit toolkit) {
		return createContent(parent, toolkit);
	}

}
