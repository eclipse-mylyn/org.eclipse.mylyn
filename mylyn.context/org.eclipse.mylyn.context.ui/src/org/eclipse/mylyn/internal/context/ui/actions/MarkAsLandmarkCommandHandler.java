/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Shawn Minto
 */
public class MarkAsLandmarkCommandHandler extends AbstractHandler {

	private final InterestIncrementAction action = new InterestIncrementAction();

	public Object execute(ExecutionEvent event) throws ExecutionException {
		action.selectionChanged(null, HandlerUtil.getCurrentSelection(event));
		action.run(null);
		return null;
	}

}