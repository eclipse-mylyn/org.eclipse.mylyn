/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.emf.databinding.FeaturePath;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.internal.core.BuildPackage.Literals;
import org.eclipse.mylyn.internal.builds.ui.actions.ShowTestResultsAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Steffen Pingel
 */
public class TestResultPart extends AbstractBuildEditorPart {

	private ShowTestResultsAction showTestResultsAction;

	public TestResultPart() {
		super(ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		setPartName("Test Results");
	}

	@Override
	public void initialize(BuildEditorPage page) {
		super.initialize(page);

		showTestResultsAction = new ShowTestResultsAction();
		showTestResultsAction.selectionChanged(new StructuredSelection(getInput(IBuild.class)));
	}

	@Override
	protected Control createContent(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		if (getInput(IBuild.class).getTestResult() == null) {
			createLabel(composite, toolkit, "No test results generated.");
		} else {
			Label label = createLabel(composite, toolkit, "Failed:");
			GridDataFactory.defaultsFor(label).indent(0, 0).applyTo(label);
			Text text = createTextReadOnly(composite, toolkit, "");
			bind(text, IBuild.class, FeaturePath
					.fromList(Literals.BUILD__TEST_RESULT, Literals.TEST_RESULT__FAIL_COUNT));

			label = createLabel(composite, toolkit, "Passed:");
			GridDataFactory.defaultsFor(label).indent(0, 0).applyTo(label);
			text = createTextReadOnly(composite, toolkit, "");
			bind(text, IBuild.class, FeaturePath
					.fromList(Literals.BUILD__TEST_RESULT, Literals.TEST_RESULT__PASS_COUNT));

			Hyperlink hyperlink = toolkit.createHyperlink(composite, "Show Tests in JUnit View", SWT.NONE);
			GridDataFactory.fillDefaults().span(2, 1).indent(0, 10).applyTo(hyperlink);
			hyperlink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent event) {
					showTestResultsAction.run();
				}
			});
			hyperlink.setEnabled(showTestResultsAction.isEnabled());
		}

		return composite;
	}

	@Override
	protected void fillToolBar(ToolBarManager toolBarManager) {
		super.fillToolBar(toolBarManager);

		toolBarManager.add(showTestResultsAction);
	}

}
