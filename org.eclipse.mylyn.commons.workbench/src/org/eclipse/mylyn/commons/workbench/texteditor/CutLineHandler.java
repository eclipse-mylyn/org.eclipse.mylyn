/*******************************************************************************
 * Copyright (c) 2007, 2011 Tasktop Technologies Inc. and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench.texteditor;

import org.eclipse.ui.texteditor.DeleteLineAction;

/**
 * Command handler for cut line command (whole)
 * 
 * @author David Green
 * @since 3.7
 */
public class CutLineHandler extends AbstractDeleteLineHandler {

	public CutLineHandler() {
		super(DeleteLineAction.WHOLE, true);
	}

}
