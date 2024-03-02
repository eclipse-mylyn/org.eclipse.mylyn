/*******************************************************************************
 * Copyright (c) 2010, 2024 Tom Seidel, Remus Software and others.
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

package org.eclipse.mylyn.htmltext.commands.basicstyle;

import org.eclipse.mylyn.htmltext.commands.Command;

/**
 * @author Tom Seidel <tom.seidel@remus-software.org>
 */
public class BoldCommand extends Command {

	@Override
	public String getCommandIdentifier() {
		return "bold"; //$NON-NLS-1$
	}

}
