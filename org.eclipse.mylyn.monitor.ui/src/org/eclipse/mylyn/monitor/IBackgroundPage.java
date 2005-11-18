/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.monitor;

import java.io.File;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * @author Leah Findlater
 */
public interface IBackgroundPage extends IWizardPage {
	
	public abstract File createFeedbackFile();
}
