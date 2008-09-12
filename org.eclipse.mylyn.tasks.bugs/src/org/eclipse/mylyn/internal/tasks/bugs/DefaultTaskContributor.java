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

package org.eclipse.mylyn.internal.tasks.bugs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.internal.provisional.tasks.bugs.AbstractTaskContributor;
import org.eclipse.mylyn.internal.tasks.bugs.wizards.ErrorLogStatus;
import org.eclipse.mylyn.internal.tasks.bugs.wizards.FeatureStatus;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.osgi.framework.Bundle;

/**
 * @author Steffen Pingel
 */
public class DefaultTaskContributor extends AbstractTaskContributor {

	public void appendErrorDetails(StringBuilder sb, IStatus status, Date date) {
		sb.append("\n\n-- Error Details --");
		if (date != null) {
			sb.append("\nDate: ");
			sb.append(date);
		}
		sb.append("\nMessage: ");
		sb.append(status.getMessage());
		sb.append("\nSeverity: ");
		sb.append(getSeverityText(status.getSeverity()));
		sb.append("\nPlugin: ");
		sb.append(status.getPlugin());
	}

	@Override
	public Map<String, String> getAttributes(IStatus status) {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(IRepositoryConstants.DESCRIPTION, getDescription(status));
		return attributes;
	}

	public String getDescription(IStatus status) {
		if (status instanceof FeatureStatus) {
			StringBuilder sb = new StringBuilder();
			sb.append("\n\n\n");
			sb.append("-- Installed Features and Plug-ins --\n");
			IBundleGroup[] bundleGroups = ((FeatureStatus) status).getBundleGroup();
			for (IBundleGroup bundleGroup : bundleGroups) {
				sb.append(bundleGroup.getIdentifier());
				sb.append(" ");
				sb.append(bundleGroup.getVersion());
				sb.append("\n");

				Bundle[] bundles = bundleGroup.getBundles();
				if (bundles != null) {
					for (Bundle bundle : bundles) {
						sb.append("  ");
						sb.append(bundle.getSymbolicName());
						String version = (String) bundle.getHeaders().get("Bundle-Version");
						if (version != null) {
							sb.append(" ");
							sb.append(version);
						}
						sb.append("\n");
					}
				}
			}
			return sb.toString();
		} else if (status instanceof ErrorLogStatus) {
			ErrorLogStatus errorLogStatus = (ErrorLogStatus) status;
			StringBuilder sb = new StringBuilder();
			appendErrorDetails(sb, errorLogStatus, errorLogStatus.getDate());
			if (errorLogStatus.getLogSessionData() != null) {
				sb.append("\nSession Data:\n");
				sb.append(errorLogStatus.getLogSessionData());
			}
			if (errorLogStatus.getStack() != null) {
				sb.append("\nException Stack Trace:\n");
				sb.append(errorLogStatus.getStack());
			}
			return sb.toString();
		} else {
			StringBuilder sb = new StringBuilder();
			appendErrorDetails(sb, status, new Date());
			if (status.getException() != null) {
				sb.append("\nException Stack Trace:\n");
				StringWriter writer = new StringWriter();
				status.getException().printStackTrace(new PrintWriter(writer));
				sb.append(writer.getBuffer());
			}
			return sb.toString();
		}
	}

	@Override
	public String getEditorId(IStatus status) {
		return TaskEditor.ID_EDITOR;
	}

	private String getSeverityText(int severity) {
		switch (severity) {
		case IStatus.ERROR:
			return "Error";
		case IStatus.WARNING:
			return "Warning";
		case IStatus.INFO:
			return "Info";
		case IStatus.OK:
			return "OK";
		}
		return "?"; //$NON-NLS-1$
	}

}