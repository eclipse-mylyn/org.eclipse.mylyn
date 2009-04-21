/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.discovery.core.model;


/**
 * @author David Green
 */
public class Overview {
	
	protected String summary;
	protected String url;
	protected String screenshot;
	protected ConnectorDescriptor connectorDescriptor;
	
	public Overview() {
	}
	
	public String getSummary() {
		return summary;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	/**
	 * HTML
	 */
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * 320x240 PNG, JPEG or GIF
	 */
	public String getScreenshot() {
		return screenshot;
	}
	
	public void setScreenshot(String screenshot) {
		this.screenshot = screenshot;
	}
	
	public ConnectorDescriptor getConnectorDescriptor() {
		return connectorDescriptor;
	}
	
	public void setConnectorDescriptor(ConnectorDescriptor connectorDescriptor) {
		this.connectorDescriptor = connectorDescriptor;
	}
	
	public void validate() throws ValidationException {
	}
}
