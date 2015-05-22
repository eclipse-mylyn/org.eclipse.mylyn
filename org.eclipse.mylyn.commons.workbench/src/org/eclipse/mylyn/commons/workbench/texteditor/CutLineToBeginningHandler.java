/*******************************************************************************
 * Copyright (c) 2007, 2011 Tasktop Technologies Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench.texteditor;

import org.eclipse.ui.texteditor.DeleteLineAction;

/**
 * Command handler for cut line command (to beginning)
 * 
 * @author David Green
 * @since 3.7
 */
public class CutLineToBeginningHandler extends AbstractDeleteLineHandler {

	public CutLineToBeginningHandler() {
		super(DeleteLineAction.TO_BEGINNING, true);
	}

}
