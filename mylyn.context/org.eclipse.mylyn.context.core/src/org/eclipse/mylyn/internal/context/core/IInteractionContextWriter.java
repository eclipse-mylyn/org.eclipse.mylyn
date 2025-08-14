/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.mylyn.context.core.IInteractionContext;

/**
 * @author Mik Kersten
 */
public interface IInteractionContextWriter {

	void setOutputStream(OutputStream outputStream);

	void writeContextToStream(IInteractionContext context) throws IOException;

}
