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

package org.eclipse.mylyn.internal.context.core;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.mylyn.context.core.IInteractionContext;

/**
 * @author Mik Kersten
 */
public interface IInteractionContextWriter {

	public abstract void setOutputStream(OutputStream outputStream);

	public abstract void writeContextToStream(IInteractionContext context) throws IOException;

}
