/*******************************************************************************
 * Copyright (c) 2011 Tom Seidel, Remus Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 * Contributors:
 *     Tom Seidel - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.htmltext.commands.dialog;

import org.eclipse.mylyn.htmltext.commands.Command;

/**
 * @author Tom Seidel <tom.seidel@remus-software.org>
 * @since 0.8
 */
public class InsertEditTableCommand extends Command {

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.htmltext.commands.Command#getCommandIdentifier()
	 */
	@Override
	public String getCommandIdentifier() {
		return "table";
	}

}
