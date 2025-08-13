/*******************************************************************************
 * Copyright (c) 2010, 2013 Markus Knittig and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Markus Knittig - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Benjamin Muskalla - enhancements for bug 324222
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.builds.ui.BuildsUiConstants;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.mylyn.internal.builds.ui.actions.AbortBuildAction;
import org.eclipse.mylyn.internal.builds.ui.actions.AbortBuildFromEditorAction;
import org.eclipse.mylyn.internal.builds.ui.actions.NewTaskFromBuildAction;
import org.eclipse.mylyn.internal.builds.ui.actions.RefreshBuildEditorAction;
import org.eclipse.mylyn.internal.builds.ui.actions.RunBuildAction;
import org.eclipse.mylyn.internal.builds.ui.actions.ShowHistoryAction;
import org.eclipse.mylyn.internal.builds.ui.view.BuildLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.widgets.Form;

/**
 * @author Markus Knittig
 * @author Steffen Pingel
 */
public class BuildEditor extends SharedHeaderFormEditor {

	private IBuildPlan plan;

	private IBuild build;

	private BuildDetailsPage buildDetailsPage;

	private boolean isDisposed;

	private RunBuildAction runBuildAction;

	private AbortBuildAction abortBuildAction;

	private ShowHistoryAction historyAction;

	private NewTaskFromBuildAction newTaskFromBuildAction;

	private RefreshBuildEditorAction refreshAction;

	@Override
	protected void addPages() {
		buildDetailsPage = new BuildDetailsPage(this, Messages.BuildEditor_details);
		try {
			addPage(buildDetailsPage);
		} catch (PartInitException e) {
			StatusHandler.log(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, "Could not create Build editor.", e)); //$NON-NLS-1$
		}
		setActivePage(0);
	}

	@Override
	protected void createHeaderContents(IManagedForm headerForm) {
		getToolkit().decorateFormHeading(headerForm.getForm().getForm());
		EditorUtil.initializeScrollbars(getHeaderForm().getForm());
		updateHeader();
		fillToolBar();
		updateToolBarActions();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// ignore
	}

	@Override
	public void doSaveAs() {
		// ignore
	}

	@Override
	public BuildEditorInput getEditorInput() {
		return (BuildEditorInput) super.getEditorInput();
	}

	public IBuildPlan getPlan() {
		return plan;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (!(input instanceof BuildEditorInput)) {
			throw new PartInitException("Unsupported class for editor input ''" + input.getClass() + "''"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		super.init(site, input);

		plan = ((BuildEditorInput) input).getPlan();
		build = ((BuildEditorInput) input).getBuild();
		setPartName(input.getName());
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	private void updateHeader() {
		BuildEditorInput input = getEditorInput();
		String title;
		Image image;
		if (input.getBuild() != null) {
			image = CommonImages.getImage(BuildLabelProvider.getImageDescriptor(input.getBuild().getStatus()));
			title = NLS.bind(Messages.BuildEditor_buildLabel, input.getBuild().getLabel());
		} else {
			image = CommonImages.getImage(BuildsUi.getConnectorUi(plan.getServer()).getImageDescriptor());
			title = Messages.BuildEditor_build;
		}
		switch (getEditorInput().getBuildInfo()) {
			case PARTIAL:
				title = NLS.bind(Messages.BuildEditor_retrievingBuild, title);
				break;
			case ERROR:
				title = NLS.bind(Messages.BuildEditor_failedToRetrieveBuildInformation, title);
				image = CommonImages.getImage(CommonImages.ERROR);
				break;
		}
		getHeaderForm().getForm().setText(title);
		getHeaderForm().getForm().setImage(image);
		setTitleToolTip(input.getToolTipText());
		setPartName(input.getName());
	}

	private void fillToolBar() {
		final Form form = getHeaderForm().getForm().getForm();
		IToolBarManager toolBarManager = form.getToolBarManager();

		toolBarManager.add(new GroupMarker(BuildsUiConstants.GROUP_FILE));

		refreshAction = new RefreshBuildEditorAction(this);
		toolBarManager.add(refreshAction);

		runBuildAction = new RunBuildAction();
		toolBarManager.add(runBuildAction);

		abortBuildAction = new AbortBuildFromEditorAction(this);
		toolBarManager.add(abortBuildAction);

		historyAction = new ShowHistoryAction();
		toolBarManager.add(historyAction);

		toolBarManager.add(new Separator(BuildsUiConstants.GROUP_EDIT));

		if (getEditorInput().getBuild() != null) {
			newTaskFromBuildAction = new NewTaskFromBuildAction();
			toolBarManager.add(newTaskFromBuildAction);
		}

		toolBarManager.add(new Separator(BuildsUiConstants.GROUP_OPEN));

		Action openWithBrowserAction = new Action() {
			@Override
			public void run() {
				if (build != null) {
					BrowserUtil.openUrl(build.getUrl(), BrowserUtil.NO_RICH_EDITOR);
				} else {
					BrowserUtil.openUrl(plan.getUrl(), BrowserUtil.NO_RICH_EDITOR);
				}
			}
		};
		openWithBrowserAction.setImageDescriptor(CommonImages.WEB);
		openWithBrowserAction.setToolTipText(Messages.BuildEditor_openWithWebBrowser);
		toolBarManager.add(openWithBrowserAction);

		toolBarManager.update(true);
	}

	protected void updateToolBarActions() {
		runBuildAction.selectionChanged(new StructuredSelection(getEditorInput().getPlan()));
		abortBuildAction.selectionChanged(new StructuredSelection(getEditorInput().getBuild()));
		historyAction.selectionChanged(new StructuredSelection(getEditorInput().getPlan()));
		if (newTaskFromBuildAction != null) {
			newTaskFromBuildAction.selectionChanged(new StructuredSelection(getEditorInput().getBuild()));
		}
		refreshAction.updateEnablement();
	}

	public void refresh() {
		build = getEditorInput().getBuild();
		plan = getEditorInput().getPlan();
		buildDetailsPage.refresh();
		updateHeader();
		updateToolBarActions();
	}

	@Override
	public void dispose() {
		isDisposed = true;
		super.dispose();
	}

	public boolean isDisposed() {
		return isDisposed;
	}

}
