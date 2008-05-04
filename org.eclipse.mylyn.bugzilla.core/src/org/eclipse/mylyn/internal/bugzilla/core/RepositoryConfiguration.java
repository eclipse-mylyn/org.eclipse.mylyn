/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_OPERATION;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_REPORT_STATUS;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryOperation;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;

/**
 * Class describing the configuration of products and components for a given Bugzilla installation.
 * 
 * @author Rob Elves
 */
public class RepositoryConfiguration implements Serializable {
	
	private static final String OPERATION_INPUT_ASSIGNED_TO = "assigned_to";

	private static final String OPERATION_INPUT_DUP_ID = "dup_id";

	private static final String OPERATION_OPTION_RESOLUTION = "resolution";

	private static final String OPERATION_LABEL_CLOSE = "Mark as CLOSED";

	private static final String OPERATION_LABEL_VERIFY = "Mark as VERIFIED";

	private static final String OPERATION_LABEL_REOPEN = "Reopen bug";

	private static final String OPERATION_LABEL_REASSIGN_DEFAULT = "Reassign to default assignee";

	private static final String OPERATION_LABEL_REASSIGN = "Reassign to";

	private static final String OPERATION_LABEL_DUPLICATE = "Mark as duplicate of #";

	private static final String OPERATION_LABEL_RESOLVE = "Resolve as";

	private static final String OPERATION_LABEL_ACCEPT = "Accept (change status to ASSIGNED)";

	private static final long serialVersionUID = -482656956042521023L;

	private static final String VERSION_UNKNOWN = "unknown";

	private String repositoryUrl = "<unknown>";

	private Map<String, ProductEntry> products = new HashMap<String, ProductEntry>();

	private List<String> platforms = new ArrayList<String>();

	private List<String> operatingSystems = new ArrayList<String>();

	private List<String> priorities = new ArrayList<String>();

	private List<String> severities = new ArrayList<String>();

	private List<String> bugStatus = new ArrayList<String>();

	private List<String> openStatusValues = new ArrayList<String>();

	private List<String> resolutionValues = new ArrayList<String>();

	private List<String> keywords = new ArrayList<String>();

	// master lists

	private List<String> versions = new ArrayList<String>();

	private List<String> components = new ArrayList<String>();

	private List<String> milestones = new ArrayList<String>();

	private List<BugzillaCustomField> customFields = new ArrayList<BugzillaCustomField>();
	
	private String version = VERSION_UNKNOWN;

	public RepositoryConfiguration() {
		super();
		// ignore
	}

	public void addStatus(String status) {
		bugStatus.add(status);
	}

	public List<String> getStatusValues() {
		return bugStatus;
	}

	public void addResolution(String res) {
		resolutionValues.add(res);
	}

	public List<String> getResolutions() {
		return resolutionValues;
	}

	/**
	 * Adds a product to the configuration.
	 */
	public void addProduct(String name) {
		if (!products.containsKey(name)) {
			ProductEntry product = new ProductEntry(name);
			products.put(name, product);
		}
	}

	/**
	 * Returns an array of names of current products.
	 */
	public List<String> getProducts() {
		ArrayList<String> productList = new ArrayList<String>(products.keySet());
		Collections.sort(productList);
		return productList;
	}

	/**
	 * Returns an array of names of component that exist for a given product or <code>null</code> if the product does
	 * not exist.
	 */
	public List<String> getComponents(String product) {
		ProductEntry entry = products.get(product);
		if (entry != null) {
			return entry.getComponents();
		} else
			return Collections.emptyList();
	}

	/**
	 * Returns an array of names of versions that exist for a given product or <code>null</code> if the product does
	 * not exist.
	 */
	public List<String> getVersions(String product) {
		ProductEntry entry = products.get(product);
		if (entry != null) {
			return entry.getVersions();
		} else
			return Collections.emptyList();
	}

	/**
	 * Returns an array of names of valid severity values.
	 */
	public List<String> getSeverities() {
		return severities;
	}

	/**
	 * Returns an array of names of valid OS values.
	 */
	public List<String> getOSs() {
		return operatingSystems;
	}

	public void addOS(String os) {
		operatingSystems.add(os);
	}

	/**
	 * Returns an array of names of valid platform values.
	 */
	public List<String> getPlatforms() {
		return platforms;
	}

	/**
	 * Returns an array of names of valid platform values.
	 */
	public List<String> getPriorities() {
		return priorities;
	}

	/**
	 * Adds a component to the given product.
	 */
	public void addComponent(String product, String component) {
		if (!components.contains(component))
			components.add(component);
		ProductEntry entry = products.get(product);
		if (entry == null) {
			entry = new ProductEntry(product);
			products.put(product, entry);
		}
		entry.addComponent(component);
	}

	// /**
	// * Adds a list of components to the given product.
	// */
	// public void addComponents(String product, List<String> components) {
	// ProductEntry entry = products.get(product);
	// if (entry == null) {
	// entry = new ProductEntry(product);
	// products.put(product, entry);
	// }
	// for (String component : components) {
	// entry.addComponent(component);
	// }
	// }
	// /**
	// * Adds a list of components to the given product.
	// */
	// public void addComponents(String product, List<String> components) {
	// ProductEntry entry = products.get(product);
	// if (entry == null) {
	// entry = new ProductEntry(product);
	// products.put(product, entry);
	// }
	// for (String component : components) {
	// entry.addComponent(component);
	// }
	// }

	public void addVersion(String product, String version) {
		if (!versions.contains(version))
			versions.add(version);
		ProductEntry entry = products.get(product);
		if (entry == null) {
			entry = new ProductEntry(product);
			products.put(product, entry);
		}
		entry.addVersion(version);
	}

	// /**
	// * Adds a list of components to the given product.
	// */
	// public void addVersions(String product, List<String> versions) {
	// ProductEntry entry = products.get(product);
	// if (entry == null) {
	// entry = new ProductEntry(product);
	// products.put(product, entry);
	// }
	// for (String version : versions) {
	// entry.addVersion(version);
	// }
	// }

	public void addKeyword(String keyword) {
		keywords.add(keyword);
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void addPlatform(String platform) {
		platforms.add(platform);
	}

	public void addPriority(String priority) {
		priorities.add(priority);
	}

	public void addSeverity(String severity) {
		severities.add(severity);

	}

	public void setInstallVersion(String version) {
		this.version = version;
	}

	public String getInstallVersion() {
		return version;
	}

	public void addTargetMilestone(String product, String target) {
		if (!milestones.contains(target))
			milestones.add(target);
		ProductEntry entry = products.get(product);
		if (entry == null) {
			entry = new ProductEntry(product);
			products.put(product, entry);
		}

		entry.addTargetMilestone(target);

	}

	public List<String> getTargetMilestones(String product) {
		ProductEntry entry = products.get(product);
		if (entry != null) {
			return entry.getTargetMilestones();
		} else
			return Collections.emptyList();
	}

	/**
	 * Container for product information: name, components.
	 */
	private static class ProductEntry implements Serializable {

		private static final long serialVersionUID = 4120139521246741120L;

		String productName;

		List<String> components = new ArrayList<String>();

		List<String> versions = new ArrayList<String>();

		List<String> milestones = new ArrayList<String>();

		ProductEntry(String name) {
			this.productName = name;
		}

		List<String> getComponents() {
			return components;
		}

		void addComponent(String componentName) {
			if (!components.contains(componentName)) {
				components.add(componentName);
			}
		}

		List<String> getVersions() {
			return versions;
		}

		void addVersion(String name) {
			if (!versions.contains(name)) {
				versions.add(name);
			}
		}

		List<String> getTargetMilestones() {
			return milestones;
		}

		void addTargetMilestone(String target) {
			milestones.add(target);
		}
	}

	public List<String> getOpenStatusValues() {
		return openStatusValues;
	}

	public void addOpenStatusValue(String value) {
		openStatusValues.add(value);
	}

	public List<String> getComponents() {
		return components;
	}

	public List<String> getTargetMilestones() {
		return milestones;
	}

	public List<String> getVersions() {
		return versions;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	/*
	 * Intermediate step until configuration is made generic.
	 */
	public List<String> getOptionValues(BugzillaReportElement element, String product) {
		switch (element) {
		case PRODUCT:
			return getProducts();
		case TARGET_MILESTONE:
			// return getTargetMilestones();
			return getTargetMilestones(product);
		case BUG_STATUS:
			return getStatusValues();
		case VERSION:
			// return getVersions();
			return getVersions(product);
		case COMPONENT:
			// return getComponents();
			return getComponents(product);
		case REP_PLATFORM:
			return getPlatforms();
		case OP_SYS:
			return getOSs();
		case PRIORITY:
			return getPriorities();
		case BUG_SEVERITY:
			return getSeverities();
		case KEYWORDS:
			return getKeywords();
		case RESOLUTION:
			return getResolutions();
		default:
			return new ArrayList<String>();
		}
	}

	/**
	 * Adds a field to the configuration.
	 */
	public void addCustomField(BugzillaCustomField newField) {
		customFields.add(newField);
	}

	public List<BugzillaCustomField> getCustomFields() {
		return customFields;
	}

	
	public void configureTaskData(RepositoryTaskData taskData) {
		updateAttributeOptions(taskData);
		addValidOperations(taskData);
	}

	public void updateAttributeOptions(RepositoryTaskData existingReport)
			{
		String product = existingReport.getAttributeValue(BugzillaReportElement.PRODUCT.getKeyString());
		for (RepositoryTaskAttribute attribute : existingReport.getAttributes()) {
			if (attribute.getId().startsWith(BugzillaCustomField.CUSTOM_FIELD_PREFIX)) {
				attribute.clearOptions();
				List<BugzillaCustomField> customFields = getCustomFields();

				for (BugzillaCustomField bugzillaCustomField : customFields) {
					if (bugzillaCustomField.getName().equals(attribute.getId())) {
						List<String> optionList = bugzillaCustomField.getOptions();
						for (String option : optionList) {
							attribute.addOption(option, option);
						}
					}
				}
			} else {
				BugzillaReportElement element = BugzillaReportElement.valueOf(attribute.getId().trim().toUpperCase(
						Locale.ENGLISH));
				attribute.clearOptions();
				List<String> optionValues = getOptionValues(element, product);
				if (element != BugzillaReportElement.OP_SYS && element != BugzillaReportElement.BUG_SEVERITY
						&& element != BugzillaReportElement.PRIORITY && element != BugzillaReportElement.BUG_STATUS) {
					Collections.sort(optionValues);
				}
				if (element == BugzillaReportElement.TARGET_MILESTONE && optionValues.isEmpty()) {

					existingReport.removeAttribute(BugzillaReportElement.TARGET_MILESTONE);
					continue;
				}
				attribute.clearOptions();
				for (String option : optionValues) {
					attribute.addOption(option, option);
				}

				// TODO: bug#162428, bug#150680 - something along the lines of...
				// but must think about the case of multiple values selected etc.
				// if(attribute.hasOptions()) {
				// if(!attribute.getOptionValues().containsKey(attribute.getValue()))
				// {
				// // updateAttributes()
				// }
				// }
			}
		}

	}

	private void addValidOperations(RepositoryTaskData bugReport)
			 {
		BUGZILLA_REPORT_STATUS status;
		try {
			status = BUGZILLA_REPORT_STATUS.valueOf(bugReport.getStatus());
		} catch (RuntimeException e) {
			StatusHandler.log(new Status(IStatus.INFO, BugzillaCorePlugin.PLUGIN_ID, "Unrecognized status: "
					+ bugReport.getStatus(), e));
			status = BUGZILLA_REPORT_STATUS.NEW;
		}
		switch (status) {
		case UNCONFIRMED:
		case REOPENED:
		case NEW:
			addOperation(bugReport, BUGZILLA_OPERATION.none);
			addOperation(bugReport, BUGZILLA_OPERATION.accept);
			addOperation(bugReport, BUGZILLA_OPERATION.resolve);
			addOperation(bugReport, BUGZILLA_OPERATION.duplicate);
			break;
		case ASSIGNED:
			addOperation(bugReport, BUGZILLA_OPERATION.none);
			addOperation(bugReport, BUGZILLA_OPERATION.resolve);
			addOperation(bugReport, BUGZILLA_OPERATION.duplicate);
			break;
		case RESOLVED:
			addOperation(bugReport, BUGZILLA_OPERATION.none);
			addOperation(bugReport, BUGZILLA_OPERATION.reopen);
			addOperation(bugReport, BUGZILLA_OPERATION.verify);
			addOperation(bugReport, BUGZILLA_OPERATION.close);
			break;
		case CLOSED:
			addOperation(bugReport, BUGZILLA_OPERATION.none);
			addOperation(bugReport, BUGZILLA_OPERATION.reopen);
			break;
		case VERIFIED:
			addOperation(bugReport, BUGZILLA_OPERATION.none);
			addOperation(bugReport, BUGZILLA_OPERATION.reopen);
			addOperation(bugReport, BUGZILLA_OPERATION.close);
		}
		String bugzillaVersion = getInstallVersion();
		if (bugzillaVersion == null) {
			bugzillaVersion = "2.18";
		}
		if (bugzillaVersion.compareTo("3.1") < 0
				&& (status == BUGZILLA_REPORT_STATUS.NEW || status == BUGZILLA_REPORT_STATUS.ASSIGNED
						|| status == BUGZILLA_REPORT_STATUS.REOPENED || status == BUGZILLA_REPORT_STATUS.UNCONFIRMED)) {
			// old bugzilla workflow is used
			addOperation(bugReport, BUGZILLA_OPERATION.reassign);
			addOperation(bugReport, BUGZILLA_OPERATION.reassignbycomponent);
		}
	}

	private void addOperation(RepositoryTaskData bugReport, BUGZILLA_OPERATION opcode) {
		RepositoryOperation newOperation = null;
		switch (opcode) {
		case none:
			newOperation = new RepositoryOperation(opcode.toString(), "Leave as " + bugReport.getStatus() + " "
					+ bugReport.getResolution());
			newOperation.setChecked(true);
			break;
		case accept:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_ACCEPT);
			break;
		case resolve:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_RESOLVE);
			newOperation.setUpOptions(OPERATION_OPTION_RESOLUTION);
				for (String resolution : getResolutions()) {
					// DUPLICATE and MOVED have special meanings so do not show as resolution
					if (resolution.compareTo("DUPLICATE") != 0 && resolution.compareTo("MOVED") != 0)
						newOperation.addOption(resolution, resolution);
				}
			break;
		case duplicate:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_DUPLICATE);
			newOperation.setInputName(OPERATION_INPUT_DUP_ID);
			newOperation.setInputValue("");
			break;
		case reassign:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_REASSIGN);
			newOperation.setInputName(OPERATION_INPUT_ASSIGNED_TO);
			newOperation.setInputValue("");
			break;
		case reassignbycomponent:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_REASSIGN_DEFAULT);
			break;
		case reopen:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_REOPEN);
			break;
		case verify:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_VERIFY);
			break;
		case close:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_CLOSE);
			break;
		default:
			break;
		}
		if (newOperation != null) {
			bugReport.addOperation(newOperation);
		}
	}

}
