/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import javax.xml.transform.sax.TransformerHandler;

import org.eclipse.mylyn.tasks.core.data.ITaskDataState;
import org.xml.sax.SAXException;

public class TaskDataStateWriter {

	private final TransformerHandler handler;

	public TaskDataStateWriter(TransformerHandler handler) {
		this.handler = handler;
	}

	public void write(ITaskDataState state) throws SAXException {
		handler.startDocument();
	}

}
