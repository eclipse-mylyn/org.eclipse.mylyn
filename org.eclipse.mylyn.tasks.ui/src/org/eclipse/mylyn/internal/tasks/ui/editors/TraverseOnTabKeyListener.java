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

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;

class TraverseOnTabKeyListener implements VerifyKeyListener {

	public void verifyKey(VerifyEvent event) {
		// if there is a tab key, do not "execute" it and instead traverse to the next control
		if (event.keyCode == SWT.TAB) {
			event.doit = false;
		}
	}

}