/*******************************************************************************
 * Copyright (c) 2012 Torkild U. Resheim.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.core;

/**
 * Implement to provide a log consumer for the EPUB tooling.
 * 
 * @author Torkild U. Resheim
 */
public interface ILogger {
	public enum Severity {
		ERROR, WARNING, INFO, VERBOSE, DEBUG
	}

	/**
	 * Logs the specified message using the <b>INFO</b> severity.
	 * 
	 * @param message
	 *            the message to log
	 */
	public void log(String message);

	/**
	 * Logs the specified message using the given severity.
	 * 
	 * @param message
	 *            the message to log
	 * @param severity
	 *            the severity of the event
	 */
	public void log(String message, Severity severity);

}
