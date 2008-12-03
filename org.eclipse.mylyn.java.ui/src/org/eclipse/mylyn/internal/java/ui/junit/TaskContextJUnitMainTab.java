/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui.junit;

import java.util.Set;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.junit.launcher.JUnitLaunchConfigurationTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.mylyn.internal.java.ui.JavaUiBridgePlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Mik Kersten
 */
public class TaskContextJUnitMainTab extends JUnitLaunchConfigurationTab {

	private boolean isPdeMode = false;

	private final Image image;

	public TaskContextJUnitMainTab(boolean isPdeMode) {
		this.isPdeMode = isPdeMode;
		image = JavaUiBridgePlugin.getImageDescriptor("icons/etool16/junit-tab.gif").createImage(); //$NON-NLS-1$
	}

	@Override
	public boolean isValid(ILaunchConfiguration config) {
		return true;
	}

	@Override
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);

		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 3;
		comp.setLayout(topLayout);

		new Label(comp, SWT.NONE);
		Label label = new Label(comp, SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);

		label.setText(Messages.TaskContextJUnitMainTab_SUBCLASSES_OF_TESTCASE_AUTOMATICALLY_ADD_TO_SUITE);
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy config) {
		if (!isPdeMode) {
			Set<IType> types = InteractionContextTestUtil.getTestCasesInContext();
			if (!types.isEmpty()) {
				IType firstType = types.iterator().next();
				String projectName = firstType.getJavaProject().getElementName();
				config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
			}
		}
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		// ignore
	}

	@Override
	public void initializeFrom(ILaunchConfiguration config) {
		// ignore
	}

	@Override
	public String getName() {
		return "JUnit"; //$NON-NLS-1$
	}

	@Override
	public Image getImage() {
		// IMAGE
		return image;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (image != null && !image.isDisposed()) {
			image.dispose();
		}
	}
}
