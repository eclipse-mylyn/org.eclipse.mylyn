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

package org.eclipse.mylyn.builds.ui;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.ui.spi.BuildServerWizard;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.dialogs.ValidatableWizardDialog;

/**
 * @author Steffen Pingel
 */
public class BuildsUiUtil {

	public static int openPropertiesDialog(IBuildServer server) {
		Wizard wizard = new BuildServerWizard(server);
		ValidatableWizardDialog dialog = new ValidatableWizardDialog(WorkbenchUtil.getShell(), wizard);
		dialog.create();
		return dialog.open();
	}

}
