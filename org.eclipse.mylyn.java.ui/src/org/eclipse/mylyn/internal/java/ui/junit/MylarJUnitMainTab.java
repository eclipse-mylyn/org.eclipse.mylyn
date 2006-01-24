/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.java.ui.junit;

import java.util.Set;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.junit.launcher.JUnitLaunchConfigurationTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.mylar.internal.java.MylarJavaPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Mik Kersten
 */
class MylarJUnitMainTab extends JUnitLaunchConfigurationTab {

	private static final String DESCRIPTION = "All interesting subclasses of TestCase will be added to the test suite.";

	private boolean isPdeMode = false;

	public MylarJUnitMainTab(boolean isPdeMode) {
		this.isPdeMode = isPdeMode;
	}

	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);

		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 3;
		comp.setLayout(topLayout);

		Label label = new Label(comp, SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);

		label.setText(DESCRIPTION);
	}

	public void performApply(ILaunchConfigurationWorkingCopy config) {
		if (!isPdeMode) {
			Set<IType> types = MylarContextTestUtil.getTestCasesInContext();
			if (!types.isEmpty()) {
				IType firstType = types.iterator().next();
				String projectName = firstType.getJavaProject().getElementName();
				config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
			}
		}
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		// ignore
	}

	public void initializeFrom(ILaunchConfiguration config) {
		// ignore
	}

	public String getName() {
		return "JUnit";
	}

	public Image getImage() {
		return MylarJavaPlugin.getImageDescriptor("icons/etool16/junit-tab.gif").createImage();
	}
}