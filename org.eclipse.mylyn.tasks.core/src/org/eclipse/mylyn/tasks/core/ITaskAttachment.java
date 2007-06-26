/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.io.IOException;
import java.io.InputStream;

/**
 * Encapsualtes resources that can be attached to a task.
 * 
 * @author Steffen Pingel
 * @since 2.0
 */
public interface ITaskAttachment {

	public InputStream createInputStream() throws IOException;

	public String getContentType();

	public String getDescription();

	public String getFilename();

	public long getLength();

	public boolean isPatch();

}
