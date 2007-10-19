/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.web.core;

import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.commons.logging.impl.SimpleLog;

/**
 * @author Rob Elves
 */
public class WebClientLog extends SimpleLog {

	private static final long serialVersionUID = -8631869110301753325L;

	public WebClientLog(String name) {
		super(name);
	}

	@Override
	protected void write(StringBuffer buffer) {
		OutputStream out = WebClientUtil.getLogStream();
		new PrintStream(out).println(buffer);
	}

}
