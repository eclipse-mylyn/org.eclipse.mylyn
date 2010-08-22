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

package org.eclipse.mylyn.internal.builds.ui;

import java.io.IOException;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.builds.ui.spi.BuildConnectorUi;
import org.eclipse.mylyn.commons.core.HtmlUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonFonts;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.RichToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Steffen Pingel
 */
public class BuildToolTip extends RichToolTip {

	private final static int MAX_TEXT_WIDTH = 300;

	private final static int MAX_WIDTH = 600;

	public BuildToolTip(Control control) {
		super(control);
	}

	@Override
	protected Object computeData(Widget hoverWidget) {
		Object data = super.computeData(hoverWidget);
		if (data instanceof IBuildPlan || data instanceof IBuildServer) {
			return data;
		}
		return null;
	}

	@Override
	public IBuildElement getData() {
		return (IBuildElement) super.getData();
	}

	@Override
	protected Composite createToolTipArea(Event event, Composite parent) {
		IBuildElement data = getData();

		parent.setLayout(new GridLayout(2, false));

		BuildConnectorUi connectorUi = BuildsUi.getConnectorUi(data.getServer());
		addIconAndLabel(parent, CommonImages.getImage(connectorUi.getImageDescriptor()), data.getLabel(), true);

		if (data instanceof IBuildPlan) {
			IBuildPlan plan = (IBuildPlan) data;
			if (plan.getDescription() != null && plan.getDescription().length() > 0) {
				try {
					addIconAndLabel(parent, null, HtmlUtil.toText(plan.getDescription()), false);
				} catch (IOException e) {
					// ignore
				}
			}
		}

		return parent;
	}

	protected void addIconAndLabel(Composite parent, Image image, String text, boolean bold) {
		Label imageLabel = new Label(parent, SWT.NONE);
		imageLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		imageLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		imageLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING));
		imageLabel.setImage(image);

		Label textLabel = new Label(parent, SWT.WRAP);
		if (bold) {
			textLabel.setFont(CommonFonts.BOLD);
		}
		textLabel.setForeground(getTitleColor());
		textLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		textLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));

		textLabel.setText(CommonUiUtil.toLabel(text));
		int width = Math.min(textLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, MAX_WIDTH);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).hint(width, SWT.DEFAULT).applyTo(textLabel);
	}

}
