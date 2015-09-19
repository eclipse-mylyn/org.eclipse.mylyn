/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kyle Ross     - Initial implementation
 *     Vaughan Hilts - Initial implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.editor;

import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.ui.compatibility.CommonColors;
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
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class BuildStatusAttributeEditor extends AbstractAttributeEditor {

	public BuildStatusAttributeEditor(TaskDataModel dataModel, TaskAttribute taskAttribute) {
		super(dataModel, taskAttribute);
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {

		Composite layoutComposite = toolkit.createComposite(parent);
		GridLayoutFactory.fillDefaults()
				.spacing(LayoutConstants.getSpacing().x, 0)
				.numColumns(2)
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

			TaskAttribute statusAttribute = currentBuildAttribute.getAttribute(TaskBuildStatusMapper.STATUS_ATTRIBUTE_KEY);
			TaskAttribute urlAttribute = currentBuildAttribute.getAttribute(TaskBuildStatusMapper.URL_ATTRIBUTE_KEY);
			if (statusAttribute == null || urlAttribute == null) {
				return;
			}
			String statusValue = statusAttribute.getValue();
			final String urlValue = urlAttribute.getValue();

			ScalingHyperlink buildLink = new ScalingHyperlink(layoutComposite, SWT.READ_ONLY);
			buildLink.setText(urlValue);
			buildLink.setForeground(CommonColors.HYPERLINK_WIDGET);

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

			if (this.getModel().hasIncomingChanges(currentBuildAttribute)) {
				layoutComposite.getChildren()[2 * i].setBackground(color);
				layoutComposite.getChildren()[2 * i + 1].setBackground(color);
			}
		}
	}

	@Override
	public String getLabel() {
		String label = Messages.ReviewSet_BuildHeader;
		return (label != null) ? LegacyActionTools.escapeMnemonics(label) : ""; //$NON-NLS-1$
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
