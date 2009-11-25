/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *      Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.oslc.core;

import java.io.Serializable;

/**
 * @see http://open-services.net/bin/view/Main/CmServiceDescriptionV1
 * 
 * @author Robert Elves
 */
public class ServiceHome implements Serializable {

	private static final long serialVersionUID = -723213938552650293L;

	private final String title;

	private final String url;

	public ServiceHome(String title, String url) {
		this.title = title;
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}
}
