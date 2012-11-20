/*******************************************************************************
 * Copyright (c) 2010 Markus Knittig and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
import org.eclipse.mylyn.builds.ui.spi.BuildConnectorUi;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.mylyn.internal.builds.ui.actions.NewTaskFromBuildAction;
import org.eclipse.mylyn.internal.builds.ui.actions.RunBuildAction;
import org.eclipse.mylyn.internal.builds.ui.actions.ShowHistoryAction;
import org.eclipse.mylyn.internal.builds.ui.view.BuildLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.Messages;
import org.eclipse.osgi.util.NLS;
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

	@Override
	protected void addPages() {
		BuildDetailsPage buildDetailsPage = new BuildDetailsPage(this, "Details");
		try {
			addPage(buildDetailsPage);
		} catch (PartInitException e) {
			StatusHandler.log(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, "Could not create Build editor.", e));
		}
		setActivePage(0);
	}

	@Override
	protected void createHeaderContents(IManagedForm headerForm) {
		getToolkit().decorateFormHeading(headerForm.getForm().getForm());
		EditorUtil.initializeScrollbars(getHeaderForm().getForm());
		updateHeader();
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

		this.plan = ((BuildEditorInput) input).getPlan();
		this.build = ((BuildEditorInput) input).getBuild();
		setPartName(input.getName());
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	private void updateHeader() {
		BuildEditorInput input = getEditorInput();
		if (input.getBuild() != null) {
			getHeaderForm().getForm().setImage(
					CommonImages.getImage(BuildLabelProvider.getImageDescriptor(input.getBuild().getStatus())));
			getHeaderForm().getForm().setText(NLS.bind("Build {0}", input.getBuild().getLabel()));
		} else {
			BuildConnectorUi connectorUi = BuildsUi.getConnectorUi(plan.getServer());
			getHeaderForm().getForm().setImage(CommonImages.getImage(connectorUi.getImageDescriptor()));
			getHeaderForm().getForm().setText("Build");
		}
		setTitleToolTip(input.getToolTipText());
		setPartName(input.getName());

		updateToolBar();
	}

	private void updateToolBar() {
		final Form form = getHeaderForm().getForm().getForm();
		IToolBarManager toolBarManager = form.getToolBarManager();

		toolBarManager.add(new GroupMarker(BuildsUiConstants.GROUP_FILE));

		RunBuildAction runBuildAction = new RunBuildAction();
		runBuildAction.selectionChanged(new StructuredSelection(getEditorInput().getPlan()));
		toolBarManager.add(runBuildAction);

		ShowHistoryAction historyAction = new ShowHistoryAction();
		historyAction.selectionChanged(new StructuredSelection(getEditorInput().getPlan()));
		toolBarManager.add(historyAction);

		toolBarManager.add(new Separator(BuildsUiConstants.GROUP_EDIT));

		if (getEditorInput().getBuild() != null) {
			NewTaskFromBuildAction newTaskFromBuildAction = new NewTaskFromBuildAction();
			newTaskFromBuildAction.selectionChanged(new StructuredSelection(getEditorInput().getBuild()));
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
		openWithBrowserAction.setToolTipText(Messages.AbstractTaskEditorPage_Open_with_Web_Browser);
		toolBarManager.add(openWithBrowserAction);

		toolBarManager.update(true);
	}

}
