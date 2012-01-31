/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench.browser;

import org.eclipse.mylyn.commons.workbench.EditorHandle;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author SteffenPingel
 */
public abstract class AbstractUrlHandler {

	public abstract EditorHandle openUrl(IWorkbenchPage page, String location, int customFlags);

}
