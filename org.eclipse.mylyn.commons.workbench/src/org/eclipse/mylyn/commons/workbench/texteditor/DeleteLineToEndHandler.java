/*******************************************************************************
 * Copyright (c) 2007, 2010 Tasktop Technologies Inc. and others.
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
 * Command handler for delete line command (to end)
 * 
 * @author David Green
 * @since 3.7
 */
public class DeleteLineToEndHandler extends AbstractDeleteLineHandler {

	public DeleteLineToEndHandler() {
		super(DeleteLineAction.TO_END, false);
	}

}
