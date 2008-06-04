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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_REPORT_STATUS;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;

/**
 * Class describing the configuration of products and components for a given Bugzilla installation.
 * 
 * @author Rob Elves
 */
public class RepositoryConfiguration implements Serializable {

	private static final long serialVersionUID = 575019225495659016L;

	private static final String VERSION_UNKNOWN = "unknown";

	private String repositoryUrl = "<unknown>";

	private final Map<String, ProductEntry> products = new HashMap<String, ProductEntry>();

	private final List<String> platforms = new ArrayList<String>();

	private final List<String> operatingSystems = new ArrayList<String>();

	private final List<String> priorities = new ArrayList<String>();

	private final List<String> severities = new ArrayList<String>();

	private final List<String> bugStatus = new ArrayList<String>();

	private final List<String> openStatusValues = new ArrayList<String>();

	private final List<String> resolutionValues = new ArrayList<String>();

	private final List<String> keywords = new ArrayList<String>();

	// master lists

	private final List<String> versions = new ArrayList<String>();

	private final List<String> components = new ArrayList<String>();

	private final List<String> milestones = new ArrayList<String>();

	private final List<BugzillaCustomField> customFields = new ArrayList<BugzillaCustomField>();

	private final List<BugzillaFlag> flags = new ArrayList<BugzillaFlag>();

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
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * Returns an array of names of versions that exist for a given product or <code>null</code> if the product does not
	 * exist.
	 */
	public List<String> getVersions(String product) {
		ProductEntry entry = products.get(product);
		if (entry != null) {
			return entry.getVersions();
		} else {
			return Collections.emptyList();
		}
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
		if (!components.contains(component)) {
			components.add(component);
		}
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
		if (!versions.contains(version)) {
			versions.add(version);
		}
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
		if (!milestones.contains(target)) {
			milestones.add(target);
		}
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
		} else {
			return Collections.emptyList();
		}
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
	public List<String> getOptionValues(BugzillaAttribute element, String product) {
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

	public void configureTaskData(TaskData taskData) {
		updateAttributeOptions(taskData);
		addValidOperations(taskData);
	}

	public void updateAttributeOptions(TaskData existingReport) {
		TaskAttribute attributeProduct = existingReport.getRoot()
				.getMappedAttribute(BugzillaAttribute.PRODUCT.getKey());
		if (attributeProduct == null) {
			return;
		}
		String product = attributeProduct.getValue();
		for (TaskAttribute attribute : new HashSet<TaskAttribute>(existingReport.getRoot().getAttributes().values())) {
			if (attribute.getId().startsWith(BugzillaCustomField.CUSTOM_FIELD_PREFIX)) {
				attribute.clearOptions();
				List<BugzillaCustomField> customFields = getCustomFields();

				for (BugzillaCustomField bugzillaCustomField : customFields) {
					if (bugzillaCustomField.getName().equals(attribute.getId())) {
						List<String> optionList = bugzillaCustomField.getOptions();
						for (String option : optionList) {
							attribute.putOption(option, option);
						}
					}
				}
			} else {

				BugzillaAttribute element;
				try {
					element = BugzillaAttribute.valueOf(attribute.getId().trim().toUpperCase(Locale.ENGLISH));
				} catch (RuntimeException e) {
					if (e instanceof IllegalArgumentException) {
						// ignore unrecognized tags
						continue;
					}
					throw e;
				}
				attribute.clearOptions();
				List<String> optionValues = getOptionValues(element, product);
				if (element != BugzillaAttribute.OP_SYS && element != BugzillaAttribute.BUG_SEVERITY
						&& element != BugzillaAttribute.PRIORITY && element != BugzillaAttribute.BUG_STATUS) {
					Collections.sort(optionValues);
				}
				if (element == BugzillaAttribute.TARGET_MILESTONE && optionValues.isEmpty()) {

					existingReport.getRoot().removeAttribute(BugzillaAttribute.TARGET_MILESTONE.getKey());
					continue;
				}
				attribute.clearOptions();
				for (String option : optionValues) {
					attribute.putOption(option, option);
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

	public void addValidOperations(TaskData bugReport) {
		TaskAttribute attributeStatus = bugReport.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		BUGZILLA_REPORT_STATUS status = BUGZILLA_REPORT_STATUS.NEW;
		if (attributeStatus != null) {
			try {
				status = BUGZILLA_REPORT_STATUS.valueOf(attributeStatus.getValue());
			} catch (RuntimeException e) {
//				StatusHandler.log(new Status(IStatus.INFO, BugzillaCorePlugin.PLUGIN_ID, "Unrecognized status: "
//						+ attributeStatus.getValue(), e));
				status = BUGZILLA_REPORT_STATUS.NEW;
			}
		}
		switch (status) {
		case UNCONFIRMED:
		case REOPENED:
		case NEW:
			addOperation(bugReport, BugzillaOperation.none);
			addOperation(bugReport, BugzillaOperation.accept);
			addOperation(bugReport, BugzillaOperation.resolve);
			addOperation(bugReport, BugzillaOperation.duplicate);
			break;
		case ASSIGNED:
			addOperation(bugReport, BugzillaOperation.none);
			addOperation(bugReport, BugzillaOperation.resolve);
			addOperation(bugReport, BugzillaOperation.duplicate);
			break;
		case RESOLVED:
			addOperation(bugReport, BugzillaOperation.none);
			addOperation(bugReport, BugzillaOperation.reopen);
			addOperation(bugReport, BugzillaOperation.verify);
			addOperation(bugReport, BugzillaOperation.close);
			break;
		case CLOSED:
			addOperation(bugReport, BugzillaOperation.none);
			addOperation(bugReport, BugzillaOperation.reopen);
			break;
		case VERIFIED:
			addOperation(bugReport, BugzillaOperation.none);
			addOperation(bugReport, BugzillaOperation.reopen);
			addOperation(bugReport, BugzillaOperation.close);
		}
		String bugzillaVersion = getInstallVersion();
		if (bugzillaVersion == null) {
			bugzillaVersion = "2.18";
		}
		if (bugzillaVersion.compareTo("3.1") < 0
				&& (status == BUGZILLA_REPORT_STATUS.NEW || status == BUGZILLA_REPORT_STATUS.ASSIGNED
						|| status == BUGZILLA_REPORT_STATUS.REOPENED || status == BUGZILLA_REPORT_STATUS.UNCONFIRMED)) {
			// old bugzilla workflow is used
			addOperation(bugReport, BugzillaOperation.reassign);
			addOperation(bugReport, BugzillaOperation.reassignbycomponent);
		}
	}

	public void addOperation(TaskData bugReport, BugzillaOperation opcode) {
		TaskAttribute attribute;
		TaskAttribute operationAttribute = bugReport.getRoot().getAttribute(TaskAttribute.OPERATION);
		if (operationAttribute == null) {
			operationAttribute = bugReport.getRoot().createAttribute(TaskAttribute.OPERATION);
		}

		switch (opcode) {
		case none:
			attribute = bugReport.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + opcode.toString());
			String label = "Leave";
			TaskAttribute attributeStatus = bugReport.getRoot().getMappedAttribute(TaskAttribute.STATUS);
			TaskAttribute attributeResolution = bugReport.getRoot().getMappedAttribute(TaskAttribute.RESOLUTION);
			if (attributeStatus != null && attributeResolution != null) {
				label = String.format(opcode.getLabel(), attributeStatus.getValue(), attributeResolution.getValue());
			}

			TaskOperation.applyTo(attribute, opcode.toString(), label);
			// set as default
			TaskOperation.applyTo(operationAttribute, opcode.toString(), label);
			break;
		case resolve:
			attribute = bugReport.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + opcode.toString());
			TaskOperation.applyTo(attribute, opcode.toString(), opcode.getLabel());
			TaskAttribute attrResolvedInput = attribute.getTaskData().getRoot().createAttribute(opcode.getInputId());
			attrResolvedInput.getMetaData().setType(opcode.getInputType());
			attribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, opcode.getInputId());
			for (String resolution : getResolutions()) {
				// DUPLICATE and MOVED have special meanings so do not show as resolution
				if (resolution.compareTo("DUPLICATE") != 0 && resolution.compareTo("MOVED") != 0) {
					attrResolvedInput.putOption(resolution, resolution);
				}
			}
			break;
		default:
			attribute = bugReport.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + opcode.toString());
			TaskOperation.applyTo(attribute, opcode.toString(), opcode.getLabel());
			if (opcode.getInputId() != null) {
				TaskAttribute attrInput = bugReport.getRoot().createAttribute(opcode.getInputId());
				attrInput.getMetaData().defaults().setReadOnly(false).setType(opcode.getInputType());
				attribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, opcode.getInputId());
			}
			break;
		}
	}

	/**
	 * Adds a flag to the configuration.
	 */
	public void addFlag(BugzillaFlag newFlag) {
		flags.add(newFlag);
	}

	public List<BugzillaFlag> getFlags() {
		return flags;
	}
}
