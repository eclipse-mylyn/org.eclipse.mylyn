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

// old	private static final long serialVersionUID = 575019225495659016L;
	private static final long serialVersionUID = -782630475741754124L;

	private static final String VERSION_UNKNOWN = "unknown"; //$NON-NLS-1$

	private String repositoryUrl = "<unknown>"; //$NON-NLS-1$

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
			return getTargetMilestones(product);
		case BUG_STATUS:
			return getStatusValues();
		case VERSION:
			return getVersions(product);
		case COMPONENT:
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
			return Collections.emptyList();
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
		if (taskData != null) {
			addMissingFlags(taskData);
			updateAttributeOptions(taskData);
			addValidOperations(taskData);
		}
	}

	private void addMissingFlags(TaskData taskData) {
		List<String> existingFlags = new ArrayList<String>();
		List<BugzillaFlag> flags = getFlags();
		for (TaskAttribute attribute : new HashSet<TaskAttribute>(taskData.getRoot().getAttributes().values())) {
			if (attribute.getId().startsWith("task.common.kind.flag")) { //$NON-NLS-1$
				TaskAttribute state = attribute.getAttribute("state"); //$NON-NLS-1$
				if (state != null) {
					String nameValue = state.getMetaData().getLabel();
					if (!existingFlags.contains(nameValue)) {
						existingFlags.add(nameValue);
					}
					String desc = attribute.getMetaData().getLabel();
					if (desc == null || desc.equals("")) { //$NON-NLS-1$
						for (BugzillaFlag bugzillaFlag : flags) {
							if (bugzillaFlag.getType().equals("attachment")) { //$NON-NLS-1$
								continue;
							}
							if (bugzillaFlag.getName().equals(nameValue)) {
								attribute.getMetaData().setLabel(bugzillaFlag.getDescription());
							}
						}
					}
				}
			}
		}
		TaskAttribute productAttribute = taskData.getRoot().getMappedAttribute(BugzillaAttribute.PRODUCT.getKey());
		TaskAttribute componentAttribute = taskData.getRoot().getMappedAttribute(BugzillaAttribute.COMPONENT.getKey());
		for (BugzillaFlag bugzillaFlag : flags) {
			if (bugzillaFlag.getType().equals("attachment")) { //$NON-NLS-1$
				continue;
			}
			if (!bugzillaFlag.isUsedIn(productAttribute.getValue(), componentAttribute.getValue())) {
				continue;
			}
			if (existingFlags.contains(bugzillaFlag.getName()) && !bugzillaFlag.isMultiplicable()) {
				continue;
			}
			BugzillaFlagMapper mapper = new BugzillaFlagMapper();
			mapper.setRequestee(""); //$NON-NLS-1$
			mapper.setSetter(""); //$NON-NLS-1$
			mapper.setState(" "); //$NON-NLS-1$
			mapper.setFlagId(bugzillaFlag.getName());
			mapper.setNumber(0);
			mapper.setDescription(bugzillaFlag.getDescription());
			TaskAttribute attribute = taskData.getRoot().createAttribute(
					"task.common.kind.flag_type" + bugzillaFlag.getFlagId()); //$NON-NLS-1$
			mapper.applyTo(attribute);
		}
		setFlagsRequestee(taskData);
	}

	private void setFlagsRequestee(TaskData taskData) {
		for (TaskAttribute attribute : new HashSet<TaskAttribute>(taskData.getRoot().getAttributes().values())) {
			if (attribute.getId().startsWith("task.common.kind.flag")) { //$NON-NLS-1$
				TaskAttribute state = attribute.getAttribute("state"); //$NON-NLS-1$
				if (state != null) {
					String nameValue = state.getMetaData().getLabel();
					for (BugzillaFlag bugzillaFlag : flags) {
						if (nameValue.equals(bugzillaFlag.getName())) {
							TaskAttribute requestee = attribute.getAttribute("requestee"); //$NON-NLS-1$
							if (requestee == null) {
								requestee = attribute.createMappedAttribute("requestee"); //$NON-NLS-1$
								requestee.getMetaData().defaults().setType(TaskAttribute.TYPE_SHORT_TEXT);
								requestee.setValue(""); //$NON-NLS-1$
							}
							requestee.getMetaData().setReadOnly(!bugzillaFlag.isSpecifically_requestable());
						}
					}
				}
			}
		}
	}

	public void updateAttributeOptions(TaskData existingReport) {
		TaskAttribute attributeProduct = existingReport.getRoot()
				.getMappedAttribute(BugzillaAttribute.PRODUCT.getKey());
		if (attributeProduct == null) {
			return;
		}
		String product = attributeProduct.getValue();
		for (TaskAttribute attribute : new HashSet<TaskAttribute>(existingReport.getRoot().getAttributes().values())) {

			List<String> optionValues = getAttributeOptions(product, attribute);

			if (attribute.getId().equals(BugzillaAttribute.TARGET_MILESTONE.getKey()) && optionValues.isEmpty()) {
				existingReport.getRoot().removeAttribute(BugzillaAttribute.TARGET_MILESTONE.getKey());
				continue;
			}

			if (attribute.getId().startsWith("task.common.kind.flag")) { //$NON-NLS-1$
				attribute = attribute.getAttribute("state"); //$NON-NLS-1$
			}

			attribute.clearOptions();
			for (String option : optionValues) {
				attribute.putOption(option, option);
			}
		}

	}

	public List<String> getAttributeOptions(String product, TaskAttribute attribute) {
		List<String> options = new ArrayList<String>();

		if (attribute.getId().startsWith(BugzillaCustomField.CUSTOM_FIELD_PREFIX)) {
			for (BugzillaCustomField bugzillaCustomField : customFields) {
				if (bugzillaCustomField.getName().equals(attribute.getId())) {
					options = bugzillaCustomField.getOptions();
					break;
				}
			}

		} else if (attribute.getId().startsWith("task.common.kind.flag")) { //$NON-NLS-1$

			TaskAttribute state = attribute.getAttribute("state"); //$NON-NLS-1$
			if (state != null) {
				String nameValue = state.getMetaData().getLabel();
				options.add(""); //$NON-NLS-1$
				for (BugzillaFlag bugzillaFlag : flags) {
					if (nameValue.equals(bugzillaFlag.getName())) {
						if (nameValue.equals(bugzillaFlag.getName())) {
							if (bugzillaFlag.isRequestable()) {
								options.add("?"); //$NON-NLS-1$
							}
							break;
						}
					}
				}
				options.add("+"); //$NON-NLS-1$
				options.add("-"); //$NON-NLS-1$
			}
		}

		else {
			String type = attribute.getMetaData().getType();

			if (type != null && type.equals(IBugzillaConstants.EDITOR_TYPE_FLAG)) {
				options.add(""); //$NON-NLS-1$
				options.add("?"); //$NON-NLS-1$
				options.add("+"); //$NON-NLS-1$
				options.add("-"); //$NON-NLS-1$
			} else {

				BugzillaAttribute element;
				try {
					element = BugzillaAttribute.valueOf(attribute.getId().trim().toUpperCase(Locale.ENGLISH));
				} catch (RuntimeException e) {
					if (e instanceof IllegalArgumentException) {
						// ignore unrecognized tags
						return options;
					}
					throw e;
				}

				options = getOptionValues(element, product);

				if (element != BugzillaAttribute.RESOLUTION && element != BugzillaAttribute.OP_SYS
						&& element != BugzillaAttribute.BUG_SEVERITY && element != BugzillaAttribute.PRIORITY
						&& element != BugzillaAttribute.BUG_STATUS) {
					Collections.sort(options);
				}
			}
		}
		return options;
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
			bugzillaVersion = "2.18"; //$NON-NLS-1$
		}
		if (status == BUGZILLA_REPORT_STATUS.NEW || status == BUGZILLA_REPORT_STATUS.ASSIGNED
				|| status == BUGZILLA_REPORT_STATUS.REOPENED || status == BUGZILLA_REPORT_STATUS.UNCONFIRMED) {
			if (bugzillaVersion.compareTo("3.1") < 0) { //$NON-NLS-1$
				// old bugzilla workflow is used
				addOperation(bugReport, BugzillaOperation.reassign);
				addOperation(bugReport, BugzillaOperation.reassignbycomponent);
			} else {
				BugzillaAttribute key = BugzillaAttribute.SET_DEFAULT_ASSIGNEE;
				TaskAttribute operationAttribute = bugReport.getRoot().getAttribute(key.getKey());
				if (operationAttribute == null) {
					operationAttribute = bugReport.getRoot().createAttribute(key.getKey());
					operationAttribute.getMetaData()
							.defaults()
							.setReadOnly(key.isReadOnly())
							.setKind(key.getKind())
							.setLabel(key.toString())
							.setType(key.getType());
					operationAttribute.setValue("0"); //$NON-NLS-1$
				}
				operationAttribute = bugReport.getRoot().getMappedAttribute(TaskAttribute.USER_ASSIGNED);
				if (operationAttribute != null) {
					operationAttribute.getMetaData().setReadOnly(false);
				}
			}
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
			String label = "Leave"; //$NON-NLS-1$
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
				if (resolution.compareTo("DUPLICATE") != 0 && resolution.compareTo("MOVED") != 0) { //$NON-NLS-1$ //$NON-NLS-2$
					attrResolvedInput.putOption(resolution, resolution);
				}
			}
			if (getResolutions().size() > 0) {
				attrResolvedInput.setValue(getResolutions().get(0));
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

	public BugzillaFlag getFlagWithId(Integer id) {
		for (BugzillaFlag bugzillaFlag : flags) {
			if (bugzillaFlag.getFlagId() == id) {
				return bugzillaFlag;
			}
		}
		return null;
	}
}
