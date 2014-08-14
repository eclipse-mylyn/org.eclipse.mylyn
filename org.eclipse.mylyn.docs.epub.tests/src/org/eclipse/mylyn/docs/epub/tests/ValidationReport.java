/*******************************************************************************
 * Copyright (c) 2012 Torkild U. Resheim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.docs.epub.tests;

import java.util.ArrayList;

import com.adobe.epubcheck.api.Report;
import com.adobe.epubcheck.util.FeatureEnum;

public class ValidationReport implements Report {

	private final ArrayList<String> errors;

	private final ArrayList<String> warnings;

	private final String ePubName;

	public ValidationReport(String ePubName) {
		this.ePubName = ePubName;
		errors = new ArrayList<String>();
		warnings = new ArrayList<String>();
	}

	@SuppressWarnings("nls")
	public void error(String resource, int line, int column, String message) {
		String log = "ERROR: " + ePubName + (resource == null ? "" : "/" + resource)
				+ (line <= 0 ? "" : "(" + line + ")") + ": " + message;
		errors.add(log);
	}

	public String getErrors() {
		StringBuilder sb = new StringBuilder();
		for (String error : errors) {
			sb.append("\n"); //$NON-NLS-1$
			sb.append(error);
		}
		return sb.toString();
	}

	public void exception(String arg0, Exception arg1) {
		// ignore
	}

	@SuppressWarnings("nls")
	public void warning(String resource, int line, String message) {
		System.err.println("WARNING: " + ePubName + (resource == null ? "" : "/" + resource)
				+ (line <= 0 ? "" : "(" + line + ")") + ": " + message);
	}

	public int getErrorCount() {
		return errors.size();
	}

	public int getExceptionCount() {
		return 0;
	}

	public int getWarningCount() {
		return warnings.size();
	}

	public void info(String arg0, FeatureEnum arg1, String arg2) {
		// ignore

	}

	public void warning(String arg0, int arg1, int arg2, String arg3) {
		// ignore

	}

	@Override
	public void hint(String arg0, int arg1, int arg2, String arg3) {
	}
}
