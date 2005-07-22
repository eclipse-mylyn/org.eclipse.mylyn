/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.tasklist.internal;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class MylarExternalizerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5804522104992031907L;

	public MylarExternalizerException() {
		super();
	}
	
	public MylarExternalizerException(String detailMessage) {
		super(detailMessage);
	}
}
