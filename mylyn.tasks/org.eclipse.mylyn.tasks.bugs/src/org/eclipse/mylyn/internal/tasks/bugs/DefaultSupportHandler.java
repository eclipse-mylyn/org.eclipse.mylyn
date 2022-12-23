/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.internal.tasks.bugs.wizards.ErrorLogStatus;
import org.eclipse.mylyn.internal.tasks.bugs.wizards.ProductStatus;
import org.eclipse.mylyn.tasks.bugs.AbstractSupportHandler;
import org.eclipse.mylyn.tasks.bugs.ISupportRequest;
import org.eclipse.mylyn.tasks.bugs.ISupportResponse;
import org.eclipse.mylyn.tasks.bugs.ITaskContribution;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;

/**
 * @author Steffen Pingel
 * @author Frank Becker
 */
public class DefaultSupportHandler extends AbstractSupportHandler {

	@Override
	public void preProcess(ISupportRequest request) {
		ITaskContribution contribution = request.getDefaultContribution();
		String description = getDescription(contribution.getStatus());
		if (description != null) {
			contribution.appendToDescription(description);
		}
	}

	@Override
	public void process(ITaskContribution contribution, IProgressMonitor monitor) {
		if (contribution.getAttribute(IRepositoryConstants.DESCRIPTION) == null) {
			String description = getDescription(contribution.getStatus());
			if (description != null) {
				contribution.appendToDescription(description);
			}
		}
	}

	@Override
	public void postProcess(ISupportResponse response, IProgressMonitor monitor) {
		IStatus contribution = response.getStatus();
		TaskData taskData = response.getTaskData();
		if (contribution instanceof ProductStatus) {
			if (response instanceof AttributeTaskMapper) {
				if (((AttributeTaskMapper) response).getAttribute("severity") == null) { //$NON-NLS-1$
					AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(taskData.getConnectorKind());
					ITaskMapping mapping = connector.getTaskMapping(taskData);
					mapping.merge(new TaskMapping() {
						@Override
						public String getSeverity() {
							return "enhancement"; //$NON-NLS-1$
						}
					});
				}
			}
		}
		if (response.getProduct() != null) {
			SupportProduct supportProduct = (SupportProduct) response.getProduct();
			IBundleGroup bundleGroup = getBundleGroup(supportProduct);
			if (bundleGroup != null) {
				TaskAttribute attribute = taskData.getRoot().getMappedAttribute(TaskAttribute.VERSION);
				if (attribute != null) {
					final String version = getBestMatch(bundleGroup.getVersion(), attribute.getOptions());
					if (version.length() > 0) {
						AbstractRepositoryConnector connector = TasksUi
								.getRepositoryConnector(taskData.getConnectorKind());
						ITaskMapping mapping = connector.getTaskMapping(taskData);
						mapping.merge(new TaskMapping() {
							@Override
							public String getVersion() {
								return version;
							}
						});
					}
				}
			}
		}
	}

	private IBundleGroup getBundleGroup(SupportProduct supportProduct) {
		IBundleGroup bundleGroup = supportProduct.getVersioningBundleGroup();
		if (bundleGroup == null) {
			bundleGroup = supportProduct.getBundleGroup();
		}
		return bundleGroup;
	}

	private String getBestMatch(String version, Map<String, String> options) {
		String match = ""; //$NON-NLS-1$
		for (String option : options.values()) {
			if (version.startsWith(option) && option.length() > match.length()) {
				match = option;
			}
		}
		return match;
	}

	public void appendErrorDetails(StringBuilder sb, IStatus status, Date date) {
		sb.append("\n\n"); //$NON-NLS-1$
		sb.append(Messages.DefaultTaskContributor_Error_Details);
		if (date != null) {
			sb.append("\n"); //$NON-NLS-1$
			sb.append(NLS.bind(Messages.DefaultSupportHandler_Date_X, date));
		}
		sb.append("\n"); //$NON-NLS-1$
		sb.append(NLS.bind(Messages.DefaultSupportHandler_Message_X, status.getMessage()));
		sb.append("\n"); //$NON-NLS-1$
		sb.append(NLS.bind(Messages.DefaultSupportHandler_Severity_X, getSeverityText(status.getSeverity())));
		appendProductInformation(sb);
		sb.append("\n"); //$NON-NLS-1$
		sb.append(NLS.bind(Messages.DefaultSupportHandler_Plugin_X, status.getPlugin()));
	}

	private void appendProductInformation(StringBuilder sb) {
		IProduct product = Platform.getProduct();
		if (product != null) {
			sb.append("\n"); //$NON-NLS-1$
			if (product.getName() != null) {
				sb.append(NLS.bind(Messages.DefaultSupportHandler_Product_X, product.getName()));
			} else {
				sb.append(NLS.bind(Messages.DefaultSupportHandler_Product_X, product.getId()));
			}
			Bundle definingBundle = product.getDefiningBundle();
			if (definingBundle != null) {
				Object version = definingBundle.getHeaders().get("Bundle-Version"); //$NON-NLS-1$
				if (version != null) {
					sb.append(" "); //$NON-NLS-1$
					sb.append(version);
				}
			}
			if (product.getName() != null) {
				sb.append(NLS.bind(" ({0})", product.getId())); //$NON-NLS-1$
			}
		}
	}

	public String getDescription(IStatus status) {
		if (status instanceof ProductStatus) {
			SupportProduct product = (SupportProduct) ((ProductStatus) status).getProduct();
			IBundleGroup bundleGroup = product.getBundleGroup();
			if (bundleGroup != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("\n\n\n"); //$NON-NLS-1$
				sb.append(Messages.DefaultSupportHandler_Configuration_Details);
				appendProductInformation(sb);
				sb.append(Messages.DefaultSupportHandler_Installed_Features);
				appendBundleGroup(bundleGroup, sb);
				IBundleGroup versioningBundleGroup = product.getVersioningBundleGroup();
				if (versioningBundleGroup != null) {
					sb.append(Messages.DefaultSupportHandler_VersioningPlugin);
					appendBundleGroup(versioningBundleGroup, sb);
				}
				return sb.toString();
			}
		} else if (status instanceof ErrorLogStatus) {
			ErrorLogStatus errorLogStatus = (ErrorLogStatus) status;
			StringBuilder sb = new StringBuilder();
			sb.append("\n\n"); //$NON-NLS-1$
			sb.append(Messages.DefaultSupportHandler_What_steps_message);
			sb.append("\n"); //$NON-NLS-1$
			sb.append(Messages.DefaultSupportHandler_Step_1);
			sb.append(Messages.DefaultSupportHandler_Step_2);
			sb.append(Messages.DefaultSupportHandler_Step_3);
			appendStatus(errorLogStatus, sb, true);
			return sb.toString();
		} else {
			StringBuilder sb = new StringBuilder();
			appendErrorDetails(sb, status, new Date());
			if (status.getException() != null) {
				sb.append(Messages.DefaultTaskContributor_EXCEPTION_STACK_TRACE);
				StringWriter writer = new StringWriter();
				status.getException().printStackTrace(new PrintWriter(writer));
				sb.append(writer.getBuffer());
			}
			return sb.toString();
		}
		return null;
	}

	private void appendBundleGroup(IBundleGroup bundleGroup, StringBuilder sb) {
		sb.append("\n"); //$NON-NLS-1$
		sb.append(" "); //$NON-NLS-1$
		sb.append(bundleGroup.getIdentifier());
		sb.append(" "); //$NON-NLS-1$
		sb.append(bundleGroup.getVersion());
		sb.append("\n"); //$NON-NLS-1$
	}

	private void appendStatus(IStatus status, StringBuilder sb, boolean includeSessionData) {
		Date date = (status instanceof ErrorLogStatus) ? ((ErrorLogStatus) status).getDate() : null;
		appendErrorDetails(sb, status, date);
		if (status instanceof ErrorLogStatus) {
			ErrorLogStatus errorStatus = (ErrorLogStatus) status;
			if (includeSessionData && errorStatus.getLogSessionData() != null) {
				sb.append(Messages.DefaultTaskContributor_SESSION_DATA);
				sb.append(errorStatus.getLogSessionData());
			}
			if (errorStatus.getStack() != null) {
				sb.append(Messages.DefaultTaskContributor_EXCEPTION_STACK_TRACE);
				sb.append(errorStatus.getStack());
			}
		}

		IStatus[] children = status.getChildren();
		if (children != null) {
			for (IStatus child : children) {
				appendStatus(child, sb, false);
			}
		}
	}

	private String getSeverityText(int severity) {
		switch (severity) {
		case IStatus.ERROR:
			return Messages.DefaultTaskContributor_Error;
		case IStatus.WARNING:
			return Messages.DefaultTaskContributor_Warning;
		case IStatus.INFO:
			return Messages.DefaultTaskContributor_Info;
		case IStatus.OK:
			return Messages.DefaultTaskContributor_OK;
		}
		return "?"; //$NON-NLS-1$
	}

}
