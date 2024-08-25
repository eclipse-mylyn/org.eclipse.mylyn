/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Kyle Ross     - Initial implementation
 *     Vaughan Hilts - Initial implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.editor;

import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
import org.eclipse.mylyn.commons.workbench.forms.ScalingHyperlink;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsImages;
import org.eclipse.mylyn.reviews.internal.core.BuildResult;
import org.eclipse.mylyn.reviews.internal.core.TaskBuildStatusMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.services.IServiceLocator;

public class BuildStatusAttributeEditor extends AbstractAttributeEditor {

	private final IServiceLocator locator;

	public BuildStatusAttributeEditor(TaskDataModel dataModel, IServiceLocator locator, TaskAttribute taskAttribute) {
		super(dataModel, taskAttribute);
		this.locator = locator;
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {

		Composite layoutComposite = toolkit.createComposite(parent);
		GridLayoutFactory.fillDefaults()
				.spacing(LayoutConstants.getSpacing().x, 0)
				.numColumns(3)
				.applyTo(layoutComposite);
		GridDataFactory.fillDefaults().indent(28, 0).applyTo(layoutComposite);

		TaskAttribute buildAttribute = getTaskAttribute();
		int numberOfBuilds = buildAttribute.getAttributes().size();

		for (int i = 0; i < numberOfBuilds; i++) {
			String buildKey = TaskBuildStatusMapper.ATTR_ID_BUILD_RESULT + i;

			TaskAttribute currentBuildAttribute = buildAttribute.getAttribute(buildKey);
			if (currentBuildAttribute == null) {
				return;
			}

			TaskAttribute statusAttribute = currentBuildAttribute
					.getAttribute(TaskBuildStatusMapper.STATUS_ATTRIBUTE_KEY);
			TaskAttribute urlAttribute = currentBuildAttribute.getAttribute(TaskBuildStatusMapper.URL_ATTRIBUTE_KEY);
			if (statusAttribute == null || urlAttribute == null) {
				return;
			}
			String statusValue = statusAttribute.getValue();
			final String urlValue = urlAttribute.getValue();

			ScalingHyperlink buildLink = new ScalingHyperlink(layoutComposite, SWT.READ_ONLY);
			buildLink.setText(urlValue);

			buildLink.registerMouseTrackListener();
			buildLink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent event) {
					BrowserUtil.openUrl(urlValue);
				}
			});

			CLabel buildText = new CLabel(layoutComposite, SWT.NONE);
			setControl(layoutComposite);

			buildText.setText(statusValue);
			buildText.setImage(getImageForBuildStatus(statusValue));

			final ToolBarManager toolBarManager = new ToolBarManager();
			IMenuService menuService = locator.getService(IMenuService.class);
			if (menuService != null) {
				menuService.populateContributionManager(toolBarManager, "toolbar:org.eclipse.mylyn.build.toolbar"); //$NON-NLS-1$
				toolBarManager.createControl(layoutComposite);
				for (ToolItem item : toolBarManager.getControl().getItems()) {
					item.setData(urlValue);
				}
			} else {
				new Label(layoutComposite, SWT.NONE);
			}
		}
	}

	@Override
	public void decorateIncoming(Color color) {
		TaskAttribute buildAttribute = getTaskAttribute();
		int numberOfBuilds = getTaskAttribute().getAttributes().size();

		for (int i = 0; i < numberOfBuilds; i++) {
			String buildKey = TaskBuildStatusMapper.ATTR_ID_BUILD_RESULT + i;
			TaskAttribute currentBuildAttribute = buildAttribute.getAttribute(buildKey);
			if (currentBuildAttribute == null) {
				return;
			}
			Composite layoutComposite = (Composite) getControl();

			if (getModel().hasIncomingChanges(currentBuildAttribute)) {
				layoutComposite.getChildren()[3 * i].setBackground(color);
				layoutComposite.getChildren()[3 * i + 1].setBackground(color);
			}
		}
	}

	@Override
	public String getLabel() {
		String label = Messages.ReviewSet_BuildHeader;
		return label != null ? LegacyActionTools.escapeMnemonics(label) : ""; //$NON-NLS-1$
	}

	private Image getImageForBuildStatus(String status) {
		if (status.equals(BuildResult.BuildStatus.SUCCESS.toString())) {
			return CommonImages.getImage(ReviewsImages.APPROVED);
		} else if (status.equals(BuildResult.BuildStatus.STARTED.toString())) {
			return CommonImages.getImage(ReviewsImages.UNKNOWN);
		} else if (status.equals(BuildResult.BuildStatus.UNSTABLE.toString())) {
			return CommonImages.getImage(ReviewsImages.UNSTABLE);
		}
		return CommonImages.getImage(ReviewsImages.REJECTED);
	}
}
