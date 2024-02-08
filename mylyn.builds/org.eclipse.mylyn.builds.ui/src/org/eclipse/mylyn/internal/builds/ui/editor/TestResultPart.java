/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     GitHub, Inc. - fixes for bug 350334
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.emf.databinding.FeaturePath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.ITestCase;
import org.eclipse.mylyn.builds.core.ITestResult;
import org.eclipse.mylyn.builds.core.ITestSuite;
import org.eclipse.mylyn.builds.core.TestCaseResult;
import org.eclipse.mylyn.builds.internal.core.BuildPackage.Literals;
import org.eclipse.mylyn.builds.internal.core.TestResult;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.mylyn.internal.builds.ui.actions.ShowTestResultsAction;
import org.eclipse.mylyn.internal.builds.ui.util.TestResultManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 * @author Kevin Sawicki
 */
public class TestResultPart extends AbstractBuildEditorPart {

	public class FilterTestFailuresAction extends Action {

		private final TestFailureFilter filter;

		public FilterTestFailuresAction() {
			super("Show Failures Only", IAction.AS_CHECK_BOX);
			filter = new TestFailureFilter();
			setToolTipText("Show Failures Only");
			setImageDescriptor(BuildImages.FILTER_FAILURES);
			setChecked(BuildsUiPlugin.getDefault()
					.getPreferenceStore()
					.getBoolean(BuildsUiInternal.PREF_SHOW_TEST_FAILURES_ONLY));
		}

		public void initialize() {
			if (isChecked()) {
				addFilter();
			}
		}

		private void addFilter() {
			viewer.addFilter(filter);
			viewer.expandAll();
		}

		private void removeFilter() {
			viewer.removeFilter(filter);
		}

		@Override
		public void run() {
			boolean checked = isChecked();
			if (checked) {
				addFilter();
			} else {
				removeFilter();
			}
			BuildsUiPlugin.getDefault()
			.getPreferenceStore()
			.setValue(BuildsUiInternal.PREF_SHOW_TEST_FAILURES_ONLY, checked);
		}
	}

	private static final String ID_POPUP_MENU = "org.eclipse.mylyn.builds.ui.editor.menu.TestResult"; //$NON-NLS-1$

	static class TestResultContentProvider implements ITreeContentProvider {

		private static final Object[] NO_ELEMENTS = {};

		private TestResult input;

		@Override
		public void dispose() {
			input = null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof ITestSuite) {
				return ((ITestSuite) parentElement).getCases().toArray();
			}
			return NO_ELEMENTS;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement == input) {
				return input.getSuites().toArray();
			}
			if (inputElement instanceof String) {
				return new Object[] { inputElement };
			}
			return NO_ELEMENTS;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof ITestSuite) {
				return !((ITestSuite) element).getCases().isEmpty();
			}
			return false;
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput instanceof TestResult) {
				input = (TestResult) newInput;
			} else {
				input = null;
			}
		}

	}

	/**
	 * Selects failed tests only.
	 */
	private class TestFailureFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof ITestCase) {
				return isFailure(element);
			} else if (element instanceof ITestSuite) {
				for (ITestCase testCase : ((ITestSuite) element).getCases()) {
					if (isFailure(testCase)) {
						return true;
					}
				}
			}
			return false;
		}

		boolean isFailure(Object element) {
			TestCaseResult status = ((ITestCase) element).getStatus();
			return status == TestCaseResult.FAILED || status == TestCaseResult.REGRESSION;
		}

	}

	private MenuManager menuManager;

	private ShowTestResultsAction showTestResultsAction;

	private FilterTestFailuresAction filterTestFailuresAction;

	private TreeViewer viewer;

	public TestResultPart() {
		super(ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		setPartName("Test Results");
	}

	@Override
	protected Control createContent(Composite parent, FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(new GridLayout(6, false));

		ITestResult testResult = getInput(IBuild.class).getTestResult();
		if (testResult != null) {
			Label label;
			Text text;

			label = createLabel(composite, toolkit, "Passed:");
			GridDataFactory.defaultsFor(label).indent(0, 0).applyTo(label);
			text = createTextReadOnly(composite, toolkit, ""); //$NON-NLS-1$
			bind(text, IBuild.class,
					FeaturePath.fromList(Literals.BUILD__TEST_RESULT, Literals.TEST_RESULT__PASS_COUNT));

			label = createLabel(composite, toolkit, "Failed:");
			GridDataFactory.defaultsFor(label).indent(0, 0).applyTo(label);
			text = createTextReadOnly(composite, toolkit, ""); //$NON-NLS-1$
			bind(text, IBuild.class,
					FeaturePath.fromList(Literals.BUILD__TEST_RESULT, Literals.TEST_RESULT__FAIL_COUNT));

			label = createLabel(composite, toolkit, "Ignored:");
			GridDataFactory.defaultsFor(label).indent(0, 0).applyTo(label);
			text = createTextReadOnly(composite, toolkit, ""); //$NON-NLS-1$
			bind(text, IBuild.class,
					FeaturePath.fromList(Literals.BUILD__TEST_RESULT, Literals.TEST_RESULT__IGNORED_COUNT));
		}

		viewer = new TreeViewer(toolkit.createTree(composite, SWT.MULTI));
		GridDataFactory.fillDefaults().hint(300, 100).span(6, 1).grab(true, true).applyTo(viewer.getControl());
		viewer.setContentProvider(new TestResultContentProvider());
		viewer.setLabelProvider(new DecoratingStyledCellLabelProvider(new TestResultLabelProvider(), null, null));
		viewer.addSelectionChangedListener(event -> getPage().getSite().getSelectionProvider().setSelection(event.getSelection()));
		viewer.addOpenListener(event -> {
			Object item = ((IStructuredSelection) event.getSelection()).getFirstElement();
			if (item instanceof ITestSuite) {
				TestResultManager.openInEditor((ITestSuite) item);
			} else if (item instanceof ITestCase) {
				TestResultManager.openInEditor((ITestCase) item);
			}
		});

		menuManager = new MenuManager();
		WorkbenchUtil.addDefaultGroups(menuManager);
		getPage().getEditorSite().registerContextMenu(ID_POPUP_MENU, menuManager, viewer, true);
		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		refresh(testResult);
		filterTestFailuresAction.initialize();

		toolkit.paintBordersFor(composite);
		return composite;
	}

	void refresh(ITestResult testResult) {
		if (testResult != null) {
			viewer.setInput(testResult);
			boolean hasFailures = testResult.getFailCount() > 0;
			filterTestFailuresAction.setEnabled(hasFailures);
			if (!hasFailures) {
				filterTestFailuresAction.setChecked(false);
			}
			showTestResultsAction.setEnabled(true);
		} else {
			viewer.setInput("No test results generated.");
			filterTestFailuresAction.setEnabled(false);
			filterTestFailuresAction.setChecked(false);
			showTestResultsAction.setEnabled(false);
		}
	}

	@Override
	public void initialize(BuildEditorPage page) {
		super.initialize(page);

		showTestResultsAction = new ShowTestResultsAction();
		showTestResultsAction.selectionChanged(new StructuredSelection(getInput(IBuild.class)));

		filterTestFailuresAction = new FilterTestFailuresAction();
		showTestResultsAction.selectionChanged(new StructuredSelection(getInput(IBuild.class)));
	}

	@Override
	protected void fillToolBar(ToolBarManager toolBarManager) {
		super.fillToolBar(toolBarManager);

		toolBarManager.add(filterTestFailuresAction);
		toolBarManager.add(showTestResultsAction);
	}

}
