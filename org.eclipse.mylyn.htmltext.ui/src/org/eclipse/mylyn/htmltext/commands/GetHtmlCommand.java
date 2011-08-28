/*******************************************************************************
 * Copyright (c) 2010 Tom Seidel, Remus Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 * Contributors:
 *     Tom Seidel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.htmltext.commands;

/**
 * @author Tom Seidel <tom.seidel@remus-software.org>
 */
public class GetHtmlCommand extends Command {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.mylyn.htmltext.Command#getCommandIdentifier()
	 */
	@Override
	public String getCommandIdentifier() {
		return "gethtml";
	}

	@Override
	public String getCommand() {
		return "return integration.editor.getData();";
	}

	@Override
	protected boolean trackCommand() {
		return false;
	}

}
