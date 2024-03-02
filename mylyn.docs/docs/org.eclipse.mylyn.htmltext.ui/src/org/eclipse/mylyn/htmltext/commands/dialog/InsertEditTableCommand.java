/*******************************************************************************
 * Copyright (c) 2011, 2024 Tom Seidel, Remus Software and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *
 * Contributors:
 *     Tom Seidel - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/
package org.eclipse.mylyn.htmltext.commands.dialog;

import org.eclipse.mylyn.htmltext.commands.Command;

/**
 * @author Tom Seidel <tom.seidel@remus-software.org>
 * @since 0.8
 */
public class InsertEditTableCommand extends Command {

	@Override
	public String getCommandIdentifier() {
		return "table"; //$NON-NLS-1$
	}

}
