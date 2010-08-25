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
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.builds.ui.spi.BuildConnectorUi;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;

/**
 * @author Markus Knittig
 * @author Steffen Pingel
 */
public class BuildEditor extends SharedHeaderFormEditor {

	private IBuildPlan plan;

	@Override
	protected void addPages() {
		BuildEditorPage buildEditorPage = new BuildEditorPage(this, "Details");
		try {
			addPage(buildEditorPage);
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
		setPartName(input.getName());
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	private void updateHeader() {
		IEditorInput input = getEditorInput();
		BuildConnectorUi connectorUi = BuildsUi.getConnectorUi(plan.getServer());
		getHeaderForm().getForm().setImage(CommonImages.getImage(connectorUi.getImageDescriptor()));
		if (plan.getLastBuild() != null) {
			getHeaderForm().getForm().setText(NLS.bind("Build {0}", plan.getLastBuild().getLabel()));
		} else {
			getHeaderForm().getForm().setText("Build");
		}
		setTitleToolTip(input.getToolTipText());
		setPartName(input.getName());
	}

}
