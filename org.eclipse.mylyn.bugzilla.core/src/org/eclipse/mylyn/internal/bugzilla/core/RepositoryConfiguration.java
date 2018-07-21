/*******************************************************************************
 * Copyright (c) 2004, 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Red Hat Inc. - fixes for bug 259291
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_REPORT_STATUS;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_REPORT_STATUS_4_0;
import org.eclipse.mylyn.internal.bugzilla.core.service.BugzillaXmlRpcClient;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;

/**
 * Class describing the configuration of products and components for a given Bugzilla installation.
 *
 * @author Rob Elves
 * @author Charley Wang
 * @author Frank Becker
 */
public class RepositoryConfiguration implements Serializable {

	// old value	private static final long serialVersionUID = -7248029094079799975L;

	private static final long serialVersionUID = -7051029281842783666L;

	private String repositoryUrl = "<unknown>"; //$NON-NLS-1$

	private final Map<String, ProductEntry> products = new HashMap<String, ProductEntry>();

	private final List<String> platforms = new ArrayList<String>();

	private final List<String> operatingSystems = new ArrayList<String>();

	private final List<String> priorities = new ArrayList<String>();

	private final List<String> severities = new ArrayList<String>();

	private final List<String> bugStatus = new ArrayList<String>();

	private final List<String> openStatusValues = new ArrayList<String>();

	private final List<String> closedStatusValues = new ArrayList<String>();

	private final List<String> resolutionValues = new ArrayList<String>();

	private final List<String> keywords = new ArrayList<String>();

	// master lists

	private final List<String> versions = new ArrayList<String>();

	private final List<String> components = new ArrayList<String>();

	private final List<String> milestones = new ArrayList<String>();

	private final List<BugzillaCustomField> customFields = new ArrayList<BugzillaCustomField>();

	private final List<BugzillaFlag> flags = new ArrayList<BugzillaFlag>();

	private BugzillaVersion version = BugzillaVersion.MIN_VERSION;

	private CustomTransitionManager validTransitions;

	private String encoding = null;

	private String eTagValue = null;

	private Date lastModifiedHeader = null;

	public RepositoryConfiguration() {
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
		Collections.sort(productList, String.CASE_INSENSITIVE_ORDER);
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
	private void addComponent(String product, String component) {
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

	private void addVersion(String product, String version) {
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

	public List<String> getKeywords() {
		return keywords;
	}

	public void setInstallVersion(String version) {
		this.version = new BugzillaVersion(version);
	}

	public BugzillaVersion getInstallVersion() {
		return version;
	}

	private void addTargetMilestone(String product, String target) {
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

	public void addUnconfirmedAllowed(String product, Boolean unconfirmedAllowed) {
		ProductEntry entry = products.get(product);
		if (entry == null) {
			entry = new ProductEntry(product);
			products.put(product, entry);
		}
		entry.setUnconfirmedAllowed(unconfirmedAllowed);
	}

	/**
	 * Container for product information: name, components.
	 */
	private static class ProductEntry implements Serializable {

		private static final long serialVersionUID = 4120139521246741120L;

		@SuppressWarnings("unused")
		String productName;

		List<String> components = new ArrayList<String>();

		List<String> versions = new ArrayList<String>();

		List<String> milestones = new ArrayList<String>();

		String defaultMilestone = null;

		Boolean unconfirmedAllowed = false;

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

		public String getDefaultMilestone() {
			return defaultMilestone;
		}

		public void setDefaultMilestone(String defaultMilestone) {
			this.defaultMilestone = defaultMilestone;
		}

		public Boolean getUnconfirmedAllowed() {
			return unconfirmedAllowed;
		}

		public void setUnconfirmedAllowed(Boolean unconfirmedAllowed) {
			this.unconfirmedAllowed = unconfirmedAllowed;
		}
	}

	public List<String> getOpenStatusValues() {
		return openStatusValues;
	}

	public void addOpenStatusValue(String value) {
		openStatusValues.add(value);
	}

	public List<String> getClosedStatusValues() {
		if (validTransitions != null) {
			ArrayList<String> closedStatus = validTransitions.getClosedStatuses();
			if (closedStatus != null && !closedStatus.isEmpty()) {
				return closedStatus;
			}
		}
		return closedStatusValues;
	}

	public void addClosedStatusValue(String value) {
		closedStatusValues.add(value);
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

	/**
	 * Create a custom transition manager. If fileName is invalid, the resulting transition manager will also be
	 * invalid.
	 *
	 * @param fileName
	 * @throws CoreException
	 */
	public void setValidTransitions(IProgressMonitor monitor, String fileName, BugzillaXmlRpcClient xmlClient)
			throws CoreException {
		//Custom transitions only possible for newer versions of Bugzilla
		if (getInstallVersion() != null
				&& getInstallVersion().compareMajorMinorOnly(BugzillaVersion.BUGZILLA_3_2) < 0) {
			return;
		}
		try {
			if (validTransitions == null) {
				validTransitions = new CustomTransitionManager();
			}
			if (xmlClient == null) {
				if (!validTransitions.parse(fileName)) {
					validTransitions = null;
				}
			} else {
				if (!version.isSmaller(BugzillaVersion.BUGZILLA_3_6)) {
					validTransitions.parse(monitor, xmlClient);
				} else {
					validTransitions = null;
				}
			}
		} catch (CoreException e) {
			validTransitions = null;
			throw e;
		}
	}

	public List<String> getOptionValues(BugzillaAttribute element) {
		switch (element) {
		case PRODUCT:
			return getProducts();
		case TARGET_MILESTONE:
			return milestones;
		case BUG_STATUS:
			return bugStatus;
		case VERSION:
			return versions;
		case COMPONENT:
			return components;
		case REP_PLATFORM:
			return platforms;
		case OP_SYS:
			return operatingSystems;
		case PRIORITY:
			return priorities;
		case BUG_SEVERITY:
			return severities;
		case KEYWORDS:
			return keywords;
		case RESOLUTION:
			return resolutionValues;
		default:
			return Collections.emptyList();
		}
	}

	public List<String> getProductOptionValues(BugzillaAttribute element, String product) {
		switch (element) {
		case TARGET_MILESTONE:
			return getTargetMilestones(product);
		case VERSION:
			return getVersions(product);
		case COMPONENT:
			return getComponents(product);
		default:
			return Collections.emptyList();
		}
	}

	public void addItem(BugzillaAttribute element, String value) {
		switch (element) {
		case BUG_STATUS:
			bugStatus.add(value);
			break;
		case RESOLUTION:
			resolutionValues.add(value);
			break;
		case KEYWORDS:
			keywords.add(value);
			break;
		case REP_PLATFORM:
			platforms.add(value);
			break;
		case OP_SYS:
			operatingSystems.add(value);
			break;
		case PRIORITY:
			priorities.add(value);
			break;
		case BUG_SEVERITY:
			severities.add(value);
			break;
		case PRODUCT:
			addProduct(value);
			break;
		default:
			break;
		}
	}

	public void addItem2ProductConfiguration(BugzillaAttribute element, String product, String value) {
		switch (element) {
		case COMPONENT:
			addComponent(product, value);
			break;
		case VERSION:
			addVersion(product, value);
			break;
		case TARGET_MILESTONE:
			addTargetMilestone(product, value);
		default:
			break;
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

	public void configureTaskData(TaskData taskData, boolean localuser, BugzillaRepositoryConnector connector) {
		if (taskData != null) {
			addMissingFlags(taskData, connector);
			updateAttributeOptions(taskData);
			addValidOperations(taskData);
			if (localuser) {
				removeDomain(taskData);
			}
			addMissingAttachmentFlags(taskData, connector);
			updateAttachmentOptions(taskData);
		}
	}

	private void removeDomain(TaskData taskData) {
		for (BugzillaAttribute element : BugzillaAttribute.PERSON_ATTRIBUTES) {
			TaskAttribute attribute = taskData.getRoot().getAttribute(element.getKey());
			if (attribute != null) {
				cleanShortLogin(attribute);
			}
		}
	}

	private void cleanShortLogin(TaskAttribute a) {
		if (a.getValue() != null && a.getValue().length() > 0) {
			int atIndex = a.getValue().indexOf("@"); //$NON-NLS-1$
			if (atIndex != -1) {
				String newValue = a.getValue().substring(0, atIndex);
				a.setValue(newValue);
			}
		}
	}

	private void addMissingFlags(TaskData taskData, BugzillaRepositoryConnector connector) {
		List<String> existingFlags = new ArrayList<String>();
		List<BugzillaFlag> flags = getFlags();
		for (TaskAttribute attribute : new HashSet<TaskAttribute>(taskData.getRoot().getAttributes().values())) {
			if (attribute.getId().startsWith(BugzillaAttribute.KIND_FLAG)) {
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
			BugzillaFlagMapper mapper = new BugzillaFlagMapper(connector);
			mapper.setRequestee(""); //$NON-NLS-1$
			mapper.setSetter(""); //$NON-NLS-1$
			mapper.setState(" "); //$NON-NLS-1$
			mapper.setFlagId(bugzillaFlag.getName());
			mapper.setNumber(0);
			mapper.setDescription(bugzillaFlag.getDescription());
			TaskAttribute attribute = taskData.getRoot()
					.createAttribute(BugzillaAttribute.KIND_FLAG_TYPE + bugzillaFlag.getFlagId());
			mapper.applyTo(attribute);
		}
		setFlagsRequestee(taskData);
	}

	private void setFlagsRequestee(TaskData taskData) {
		for (TaskAttribute attribute : new HashSet<TaskAttribute>(taskData.getRoot().getAttributes().values())) {
			if (attribute.getId().startsWith(BugzillaAttribute.KIND_FLAG)) {
				TaskAttribute state = attribute.getAttribute("state"); //$NON-NLS-1$
				if (state != null) {
					String nameValue = state.getMetaData().getLabel();
					for (BugzillaFlag bugzillaFlag : flags) {
						if (nameValue.equals(bugzillaFlag.getName())) {
							TaskAttribute requestee = attribute.getAttribute("requestee"); //$NON-NLS-1$
							if (requestee == null) {
								requestee = attribute.createMappedAttribute("requestee"); //$NON-NLS-1$
								requestee.getMetaData().defaults().setType(TaskAttribute.TYPE_PERSON);
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

			if (attribute.getId().startsWith(BugzillaAttribute.KIND_FLAG)) {
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
					options.addAll(bugzillaCustomField.getOptions());
					break;
				}
			}

		} else if (attribute.getId().startsWith(BugzillaAttribute.KIND_FLAG)) {

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
				if ((element == BugzillaAttribute.TARGET_MILESTONE || element == BugzillaAttribute.VERSION
						|| element == BugzillaAttribute.COMPONENT) && (product != null && !product.equals(""))) { //$NON-NLS-1$
					options.addAll(getProductOptionValues(element, product));
				} else {
					options.addAll(getOptionValues(element));
				}
				if (element != BugzillaAttribute.RESOLUTION && element != BugzillaAttribute.OP_SYS
						&& element != BugzillaAttribute.BUG_SEVERITY && element != BugzillaAttribute.PRIORITY
						&& element != BugzillaAttribute.BUG_STATUS && element != BugzillaAttribute.TARGET_MILESTONE
						&& element != BugzillaAttribute.PRODUCT) {
					Collections.sort(options);
				}
			}
		}
		return options;
	}

	public void addValidOperations(TaskData bugReport) {
		BugzillaVersion bugzillaVersion = getInstallVersion();
		if (bugzillaVersion == null) {
			bugzillaVersion = BugzillaVersion.MIN_VERSION;
		}
		if (bugzillaVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_4_0) < 0) {
			addValidOperationsBefore4(bugReport);
		} else {
			if (getOptionValues(BugzillaAttribute.BUG_STATUS)
					.contains(BUGZILLA_REPORT_STATUS_4_0.IN_PROGRESS.toString())
					|| getOptionValues(BugzillaAttribute.BUG_STATUS)
							.contains(BUGZILLA_REPORT_STATUS_4_0.CONFIRMED.toString())) {
				addValidOperationsAfter4(bugReport);
			} else {
				addValidOperationsBefore4(bugReport);
			}

		}
	}

	public void addValidOperationsAfter4(TaskData bugReport) {
		TaskAttribute attributeStatus = bugReport.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		BUGZILLA_REPORT_STATUS_4_0 status = BUGZILLA_REPORT_STATUS_4_0.UNCONFIRMED;
		if (attributeStatus != null) {
			try {
				status = BUGZILLA_REPORT_STATUS_4_0.valueOf(attributeStatus.getValue());
			} catch (RuntimeException e) {
				status = BUGZILLA_REPORT_STATUS_4_0.UNCONFIRMED;
			}
		}
		if (validTransitions != null && attributeStatus != null && validTransitions.isValid()) {
			//Handle custom operations. Currently only tuned for transitions based on default status names
			if (attributeStatus.getValue().equals("START")) { //$NON-NLS-1$
				addOperation(bugReport, BugzillaOperation.new_default);
				TaskAttribute operationAttribute = bugReport.getRoot().getAttribute(TaskAttribute.OPERATION);
				TaskOperation.applyTo(operationAttribute, BugzillaOperation.new_default.toString(),
						IBugzillaConstants.BUGZILLA_REPORT_STATUS_4_0.START.toString());

			} else {
				addOperation(bugReport, BugzillaOperation.none);
				for (AbstractBugzillaOperation b : validTransitions.getValidTransitions(attributeStatus.getValue())) {
					//Special case: the CLOSED status needs a Resolution input.
					//This happens automatically if current status is RESOLVED, else we need to supply one
					if (b.toString().equals(BugzillaOperation.close.toString())) {
						if (attributeStatus.getValue().equals("RESOLVED") && b.getInputId() != null) { //$NON-NLS-1$
							//Do not add close with resolution operation if status is RESOLVED
							continue;
						} else if (!attributeStatus.getValue().equals("RESOLVED") && b.getInputId() == null) { //$NON-NLS-1$
							//Do not add normal 'close' operation if status is not currently RESOLVED
							continue;
						}
					}
					addOperation(bugReport, b);
				}
			}
		} else {
			TaskAttribute everConfirmed = bugReport.getRoot().getAttribute(BugzillaAttribute.EVERCONFIRMED.getKey());
			TaskAttribute product = bugReport.getRoot().getMappedAttribute(TaskAttribute.PRODUCT);
			boolean unconfirmedAllowed = getUnconfirmedAllowed(product.getValue());
			switch (status) {
			case START:
				addOperation(bugReport, BugzillaOperation.new_default);
				addOperation(bugReport, BugzillaOperation.unconfirmed);
				TaskAttribute unconfirmedAttribute = bugReport.getRoot()
						.getAttribute(TaskAttribute.PREFIX_OPERATION + BugzillaOperation.unconfirmed.toString());
				if (unconfirmedAttribute != null) {
					unconfirmedAttribute.getMetaData().setDisabled(!unconfirmedAllowed);
				}
				addOperation(bugReport, BugzillaOperation.confirmed);
				addOperation(bugReport, BugzillaOperation.in_progress);
				TaskAttribute operationAttribute = bugReport.getRoot().getAttribute(TaskAttribute.OPERATION);
				TaskOperation.applyTo(operationAttribute, BugzillaOperation.new_default.toString(),
						IBugzillaConstants.BUGZILLA_REPORT_STATUS_4_0.START.toString());
				break;
			case UNCONFIRMED:
				addOperation(bugReport, BugzillaOperation.none);
				addOperation(bugReport, BugzillaOperation.confirmed);
				addOperation(bugReport, BugzillaOperation.in_progress);
				addOperation(bugReport, BugzillaOperation.resolve);
				break;
			case CONFIRMED:
				addOperation(bugReport, BugzillaOperation.none);
				addOperation(bugReport, BugzillaOperation.in_progress);
				addOperation(bugReport, BugzillaOperation.resolve);
				break;
			case IN_PROGRESS:
				addOperation(bugReport, BugzillaOperation.none);
				addOperation(bugReport, BugzillaOperation.confirmed);
				addOperation(bugReport, BugzillaOperation.resolve);
				break;
			case RESOLVED:
				addOperation(bugReport, BugzillaOperation.none);
				if (unconfirmedAllowed) {
					addOperation(bugReport, BugzillaOperation.unconfirmed);
				}
				if (everConfirmed != null && "1".equals(everConfirmed.getValue())) { //$NON-NLS-1$
					addOperation(bugReport, BugzillaOperation.confirmed);
				}
				addOperation(bugReport, BugzillaOperation.verify_with_resolution);
				break;
			case VERIFIED:
				addOperation(bugReport, BugzillaOperation.none);
				if (unconfirmedAllowed) {
					addOperation(bugReport, BugzillaOperation.unconfirmed);
				}
				if (everConfirmed != null && "1".equals(everConfirmed.getValue())) { //$NON-NLS-1$
					addOperation(bugReport, BugzillaOperation.confirmed);
				}
				addOperation(bugReport, BugzillaOperation.resolve);
				break;
			}
			if (status != BUGZILLA_REPORT_STATUS_4_0.START) {
				addOperation(bugReport, BugzillaOperation.duplicate);
			}
		}
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

	public void addValidOperationsBefore4(TaskData bugReport) {
		TaskAttribute attributeStatus = bugReport.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		BUGZILLA_REPORT_STATUS status = BUGZILLA_REPORT_STATUS.NEW;

		if (attributeStatus != null) {
			try {
				status = BUGZILLA_REPORT_STATUS.valueOf(attributeStatus.getValue());
			} catch (RuntimeException e) {
				status = BUGZILLA_REPORT_STATUS.NEW;
			}
		}
		BugzillaVersion bugzillaVersion = getInstallVersion();
		if (bugzillaVersion == null) {
			bugzillaVersion = BugzillaVersion.MIN_VERSION;
		}

		if (validTransitions != null && attributeStatus != null && validTransitions.isValid()) {
			//Handle custom operations. Currently only tuned for transitions based on default status names
			addOperation(bugReport, BugzillaOperation.none);
			for (AbstractBugzillaOperation b : validTransitions.getValidTransitions(attributeStatus.getValue())) {
				//Special case: the CLOSED status needs a Resolution input.
				//This happens automatically if current status is RESOLVED, else we need to supply one
				if (b.toString().equals(BugzillaOperation.close.toString())) {
					if (attributeStatus.getValue().equals("RESOLVED") && b.getInputId() != null) { //$NON-NLS-1$
						//Do not add close with resolution operation if status is RESOLVED
						continue;
					} else if (!attributeStatus.getValue().equals("RESOLVED") && b.getInputId() == null) { //$NON-NLS-1$
						//Do not add normal 'close' operation if status is not currently RESOLVED
						continue;
					}
				}
				addOperation(bugReport, b);
			}
		} else {
//			Eclipse Bugzilla State transitions
//			UNCONFIRMED
//			NEW				ASSIGNED RESOLVED# CLOSED#
//			ASSIGNED		NEW RESOLVED# CLOSED#
//			REOPENED		NEW ASSIGNED RESOLVED# CLOSED#
//			RESOLVED		REOPENED VERIFIED# CLOSED#
//			VERIFIED		REOPENED RESOLVED# CLOSED#
//			CLOSED			REOPENED RESOLVED#

			switch (status) {
			case NEW:
				addOperation(bugReport, BugzillaOperation.none);
				addOperation(bugReport, BugzillaOperation.accept);
				addOperation(bugReport, BugzillaOperation.resolve);
				// This is not the standard workflow add with bug 326216 to support eclipse.org
				if (bugzillaVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_3_2) >= 0) {
					addOperation(bugReport, BugzillaOperation.close_with_resolution);
				}
				addOperation(bugReport, BugzillaOperation.duplicate);
				break;
			case UNCONFIRMED:
			case REOPENED:
				addOperation(bugReport, BugzillaOperation.none);
				addOperation(bugReport, BugzillaOperation.accept);
				addOperation(bugReport, BugzillaOperation.resolve);
				// This is not the standard workflow add with bug 326216 to support eclipse.org
				if (bugzillaVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_3_2) >= 0) {
					addOperation(bugReport, BugzillaOperation.close_with_resolution);
				}
				addOperation(bugReport, BugzillaOperation.duplicate);
				if (bugzillaVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_3_2) >= 0) {
					addOperation(bugReport, BugzillaOperation.markNew);
				}
				break;
			case ASSIGNED:
				addOperation(bugReport, BugzillaOperation.none);
				addOperation(bugReport, BugzillaOperation.resolve);
				// This is not the standard workflow add with bug 326216 to support eclipse.org
				if (bugzillaVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_3_2) >= 0) {
					addOperation(bugReport, BugzillaOperation.close_with_resolution);
				}
				addOperation(bugReport, BugzillaOperation.duplicate);
				if (bugzillaVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_3_2) >= 0) {
					addOperation(bugReport, BugzillaOperation.markNew);
				}
				break;
			case RESOLVED:
				addOperation(bugReport, BugzillaOperation.none);
				addOperation(bugReport, BugzillaOperation.reopen);
				addOperation(bugReport, BugzillaOperation.verify);
				addOperation(bugReport, BugzillaOperation.close);
				if (bugzillaVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_3_0) >= 0) {
					addOperation(bugReport, BugzillaOperation.duplicate);
					addOperation(bugReport, BugzillaOperation.resolve);
				}
				break;
			case CLOSED:
				addOperation(bugReport, BugzillaOperation.none);
				addOperation(bugReport, BugzillaOperation.reopen);
				if (bugzillaVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_3_0) >= 0) {
					addOperation(bugReport, BugzillaOperation.duplicate);
					addOperation(bugReport, BugzillaOperation.resolve);
				}
				break;
			case VERIFIED:
				addOperation(bugReport, BugzillaOperation.none);
				addOperation(bugReport, BugzillaOperation.reopen);
				addOperation(bugReport, BugzillaOperation.close);
				if (bugzillaVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_3_0) >= 0) {
					addOperation(bugReport, BugzillaOperation.duplicate);
					addOperation(bugReport, BugzillaOperation.resolve);
				}
			}
		}

		if (bugzillaVersion.compareTo(BugzillaVersion.BUGZILLA_3_0) < 0) {
			// Product change is only supported for Versions >= 3.0 without verify html page
			TaskAttribute productAttribute = bugReport.getRoot().getMappedAttribute(BugzillaAttribute.PRODUCT.getKey());
			productAttribute.getMetaData().setReadOnly(true);
		}

		if (bugzillaVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_3_0) <= 0) {
			if (status == BUGZILLA_REPORT_STATUS.NEW || status == BUGZILLA_REPORT_STATUS.ASSIGNED
					|| status == BUGZILLA_REPORT_STATUS.REOPENED || status == BUGZILLA_REPORT_STATUS.UNCONFIRMED) {
				// old bugzilla workflow is used
				addOperation(bugReport, BugzillaOperation.reassign);
				addOperation(bugReport, BugzillaOperation.reassignbycomponent);
			}
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

	public void addOperation(TaskData bugReport, AbstractBugzillaOperation op) {
		TaskAttribute attribute;
		TaskAttribute operationAttribute = bugReport.getRoot().getAttribute(TaskAttribute.OPERATION);
		if (operationAttribute == null) {
			operationAttribute = bugReport.getRoot().createAttribute(TaskAttribute.OPERATION);
		}

		if (op.toString() == BugzillaOperation.none.toString()) {
			attribute = bugReport.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + op.toString());
			String label = "Leave"; //$NON-NLS-1$
			TaskAttribute attributeStatus = bugReport.getRoot().getMappedAttribute(TaskAttribute.STATUS);
			TaskAttribute attributeResolution = bugReport.getRoot().getMappedAttribute(TaskAttribute.RESOLUTION);
			if (attributeStatus != null && attributeResolution != null) {
				label = String.format(op.getLabel(), attributeStatus.getValue(), attributeResolution.getValue());
			}

			TaskOperation.applyTo(attribute, op.toString(), label);
			// set as default
			TaskOperation.applyTo(operationAttribute, op.toString(), label);
		} else if (op.toString() == BugzillaOperation.resolve.toString()) {
			attribute = bugReport.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + op.toString());
			TaskOperation.applyTo(attribute, op.toString(), op.getLabel());
			TaskAttribute attrResolvedInput = attribute.getTaskData().getRoot().createAttribute(op.getInputId());
			attrResolvedInput.getMetaData().setType(op.getInputType());
			attribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, op.getInputId());
			for (String resolution : getOptionValues(BugzillaAttribute.RESOLUTION)) {
				// DUPLICATE and MOVED have special meanings so do not show as resolution
				if (resolution.compareTo("DUPLICATE") != 0 && resolution.compareTo("MOVED") != 0) { //$NON-NLS-1$ //$NON-NLS-2$
					attrResolvedInput.putOption(resolution, resolution);
				}
			}
			if (getOptionValues(BugzillaAttribute.RESOLUTION).size() > 0) {
				attrResolvedInput.setValue(getOptionValues(BugzillaAttribute.RESOLUTION).get(0));
			}
		} else if (op.toString().equals(BugzillaOperation.verify_with_resolution.toString())
				&& op.getInputId() != null) {
			TaskAttribute attributeResolution = bugReport.getRoot().getMappedAttribute(TaskAttribute.RESOLUTION);
			String oldResolutionValue = attributeResolution.getValue();
			attribute = bugReport.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + op.toString());
			TaskOperation.applyTo(attribute, op.toString(), op.getLabel());
			TaskAttribute attrResolvedInput = attribute.getTaskData().getRoot().createAttribute(op.getInputId());
			attrResolvedInput.getMetaData().setType(op.getInputType());
			attribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, op.getInputId());
			for (String resolution : getOptionValues(BugzillaAttribute.RESOLUTION)) {
				// DUPLICATE and MOVED have special meanings so do not show as resolution
				if (resolution.compareTo("DUPLICATE") != 0 && resolution.compareTo("MOVED") != 0) { //$NON-NLS-1$ //$NON-NLS-2$
					attrResolvedInput.putOption(resolution, resolution);
				}
			}
			if (getOptionValues(BugzillaAttribute.RESOLUTION).size() > 0) {
				attrResolvedInput.setValue(oldResolutionValue);
			}
		} else if (op.toString().equals(BugzillaOperation.close_with_resolution.toString())
				&& op.getInputId() != null) {
			attribute = bugReport.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + op.toString());
			TaskOperation.applyTo(attribute, op.toString(), op.getLabel());
			TaskAttribute attrResolvedInput = attribute.getTaskData().getRoot().createAttribute(op.getInputId());
			attrResolvedInput.getMetaData().setType(op.getInputType());
			attribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, op.getInputId());
			for (String resolution : getOptionValues(BugzillaAttribute.RESOLUTION)) {
				// DUPLICATE and MOVED have special meanings so do not show as resolution
				if (resolution.compareTo("DUPLICATE") != 0 && resolution.compareTo("MOVED") != 0) { //$NON-NLS-1$ //$NON-NLS-2$
					attrResolvedInput.putOption(resolution, resolution);
				}
			}
			if (getOptionValues(BugzillaAttribute.RESOLUTION).size() > 0) {
				attrResolvedInput.setValue(getOptionValues(BugzillaAttribute.RESOLUTION).get(0));
			}
		} else if (op.toString() == BugzillaOperation.duplicate.toString()) {

			attribute = bugReport.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + op.toString());
			TaskOperation.applyTo(attribute, op.toString(), op.getLabel());
			if (op.getInputId() != null) {
				TaskAttribute attrInput = bugReport.getRoot().getAttribute(op.getInputId());
				if (attrInput == null) {
					attrInput = bugReport.getRoot().createAttribute(op.getInputId());
				}
				attrInput.getMetaData().defaults().setReadOnly(false).setType(op.getInputType());
				attribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, op.getInputId());
			}
		} else if (op.toString() == BugzillaOperation.new_default.toString()) {
			attribute = bugReport.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + op.toString());
			TaskOperation.applyTo(attribute, op.toString(), op.getLabel());
		} else {
			attribute = bugReport.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + op.toString());
			if (bugReport.isNew()) {
				TaskOperation.applyTo(attribute, op.toString(), op.getLabel());
			} else {
				TaskOperation.applyTo(attribute, op.toString(), op.getLabel());
			}
			if (op.getInputId() != null) {
				TaskAttribute attrInput = bugReport.getRoot().createAttribute(op.getInputId());
				attrInput.getMetaData().defaults().setReadOnly(false).setType(op.getInputType());
				attribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, op.getInputId());
			}
		}
		//set the Tooltip
		if (op.toString().equals(BugzillaOperation.none.toString())) {
			attribute.getMetaData().putValue(TaskAttribute.META_DESCRIPTION,
					Messages.RepositoryConfiguration_Operation_Tooltip_none);
		} else if (op.toString().equals(BugzillaOperation.new_default.toString())) {
			attribute.getMetaData().putValue(TaskAttribute.META_DESCRIPTION,
					Messages.RepositoryConfiguration_Operation_Tooltip_new_default);
		} else if (op.toString().equals(BugzillaOperation.unconfirmed.toString())) {
			attribute.getMetaData().putValue(TaskAttribute.META_DESCRIPTION,
					Messages.RepositoryConfiguration_Operation_Tooltip_unconfirmed);
		} else if (op.toString().equals(BugzillaOperation.confirmed.toString())) {
			attribute.getMetaData().putValue(TaskAttribute.META_DESCRIPTION,
					Messages.RepositoryConfiguration_Operation_Tooltip_confirmed);
		} else if (op.toString().equals(BugzillaOperation.in_progress.toString())) {
			attribute.getMetaData().putValue(TaskAttribute.META_DESCRIPTION,
					Messages.RepositoryConfiguration_Operation_Tooltip_in_progress);
		} else if (op.toString().equals(BugzillaOperation.resolve.toString())) {
			attribute.getMetaData().putValue(TaskAttribute.META_DESCRIPTION,
					Messages.RepositoryConfiguration_Operation_Tooltip_resolve);
		} else if (op.toString().equals(BugzillaOperation.verify.toString())
				|| op.toString().equals(BugzillaOperation.verify_with_resolution.toString())) {
			attribute.getMetaData().putValue(TaskAttribute.META_DESCRIPTION,
					Messages.RepositoryConfiguration_Operation_Tooltip_veryfy);
		} else if (op.toString().equals(BugzillaOperation.duplicate.toString())) {
			attribute.getMetaData().putValue(TaskAttribute.META_DESCRIPTION,
					Messages.RepositoryConfiguration_Operation_Tooltip_duplicate);
		} else if (op.toString().equals(BugzillaOperation.reopen.toString())) {
			attribute.getMetaData().putValue(TaskAttribute.META_DESCRIPTION,
					Messages.RepositoryConfiguration_Operation_Tooltip_reopen);
		} else if (op.toString().equals(BugzillaOperation.close.toString())
				|| op.toString().equals(BugzillaOperation.close_with_resolution.toString())) {
			attribute.getMetaData().putValue(TaskAttribute.META_DESCRIPTION,
					Messages.RepositoryConfiguration_Operation_Tooltip_close);
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

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getEncoding() {
		return encoding;
	}

	public void updateAttachmentOptions(TaskData existingReport) {
		for (TaskAttribute attribute : new HashSet<TaskAttribute>(existingReport.getRoot().getAttributes().values())) {

			if (!attribute.getId().startsWith("task.common.attachment")) { //$NON-NLS-1$
				continue;
			}

			for (TaskAttribute attachmentAttribute : attribute.getAttributes().values()) {
				if (!attachmentAttribute.getId().startsWith(BugzillaAttribute.KIND_FLAG)) {
					continue;
				}

				TaskAttribute state = attachmentAttribute.getAttribute("state"); //$NON-NLS-1$
				attachmentAttribute.clearOptions();

				String nameValue = state.getMetaData().getLabel();
				state.putOption("", ""); //$NON-NLS-1$ //$NON-NLS-2$
				for (BugzillaFlag bugzillaFlag : flags) {
					if (nameValue.equals(bugzillaFlag.getName()) && bugzillaFlag.getType().equals("attachment")) { //$NON-NLS-1$
						if ("attachment".equals(bugzillaFlag.getType())) { //$NON-NLS-1$
							if (bugzillaFlag.isRequestable()) {
								state.putOption("?", "?"); //$NON-NLS-1$ //$NON-NLS-2$
							}
							break;
						}
					}
				}
				state.putOption("-", "-"); //$NON-NLS-1$ //$NON-NLS-2$
				state.putOption("+", "+"); //$NON-NLS-1$ //$NON-NLS-2$
				String flagNameValue = state.getMetaData().getLabel();
				for (BugzillaFlag bugzillaFlag : flags) {
					if (flagNameValue.equals(bugzillaFlag.getName()) && bugzillaFlag.getType().equals("attachment")) { //$NON-NLS-1$
						TaskAttribute requestee = attachmentAttribute.getAttribute("requestee"); //$NON-NLS-1$
						if (requestee == null) {
							requestee = attachmentAttribute.createMappedAttribute("requestee"); //$NON-NLS-1$
							requestee.getMetaData().defaults().setType(TaskAttribute.TYPE_SHORT_TEXT);
							requestee.setValue(""); //$NON-NLS-1$
						}
						requestee.getMetaData().setReadOnly(!bugzillaFlag.isSpecifically_requestable());
					}
				}

			}
		}
	}

	private void addMissingAttachmentFlags(TaskData taskData, BugzillaRepositoryConnector connector) {
		List<String> existingFlags = new ArrayList<String>();
		List<BugzillaFlag> flags = getFlags();
		for (TaskAttribute attribute : new HashSet<TaskAttribute>(taskData.getRoot().getAttributes().values())) {

			if (!attribute.getId().startsWith("task.common.attachment")) { //$NON-NLS-1$
				continue;
			}
			existingFlags.clear();
			for (TaskAttribute attachmentAttribute : attribute.getAttributes().values()) {
				if (!attachmentAttribute.getId().startsWith(BugzillaAttribute.KIND_FLAG)) {
					continue;
				}
				TaskAttribute state = attachmentAttribute.getAttribute("state"); //$NON-NLS-1$
				if (state != null) {
					String nameValue = state.getMetaData().getLabel();
					if (!existingFlags.contains(nameValue)) {
						existingFlags.add(nameValue);
					}
				}
			}
			TaskAttribute productAttribute = taskData.getRoot().getMappedAttribute(BugzillaAttribute.PRODUCT.getKey());
			TaskAttribute componentAttribute = taskData.getRoot()
					.getMappedAttribute(BugzillaAttribute.COMPONENT.getKey());
			for (BugzillaFlag bugzillaFlag : flags) {
				if (!bugzillaFlag.getType().equals("attachment")) { //$NON-NLS-1$
					continue;
				}
				if (!bugzillaFlag.isUsedIn(productAttribute.getValue(), componentAttribute.getValue())) {
					continue;
				}
				if (existingFlags.contains(bugzillaFlag.getName()) && !bugzillaFlag.isMultiplicable()) {
					continue;
				}
				BugzillaFlagMapper mapper = new BugzillaFlagMapper(connector);
				mapper.setRequestee(""); //$NON-NLS-1$
				mapper.setSetter(""); //$NON-NLS-1$
				mapper.setState(" "); //$NON-NLS-1$
				mapper.setFlagId(bugzillaFlag.getName());
				mapper.setNumber(0);
				mapper.setDescription(bugzillaFlag.getDescription());
				TaskAttribute newattribute = attribute
						.createAttribute(BugzillaAttribute.KIND_FLAG_TYPE + bugzillaFlag.getFlagId());
				mapper.applyTo(newattribute);
			}
		}
	}

	public void setETagValue(String eTagValue) {
		this.eTagValue = eTagValue;
	}

	public String getETagValue() {
		return eTagValue;
	}

	public String getDuplicateStatus() {
		return validTransitions == null
				? IBugzillaConstants.BUGZILLA_REPORT_STATUS.RESOLVED.toString()
				: validTransitions.getDuplicateStatus();
	}

	public String getStartStatus() {
		if (validTransitions == null) {
			return version.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_4_0) < 0
					|| !(getOptionValues(BugzillaAttribute.BUG_STATUS)
							.contains(BUGZILLA_REPORT_STATUS_4_0.IN_PROGRESS.toString())
					|| getOptionValues(BugzillaAttribute.BUG_STATUS)
							.contains(BUGZILLA_REPORT_STATUS_4_0.CONFIRMED.toString()))
									? IBugzillaConstants.BUGZILLA_REPORT_STATUS.NEW.toString()
									: IBugzillaConstants.BUGZILLA_REPORT_STATUS_4_0.CONFIRMED.toString();
		} else {
			return validTransitions.getStartStatus();
		}
	}

	public String getDefaultMilestones(String product) {
		ProductEntry entry = products.get(product);
		if (entry != null) {
			return entry.getDefaultMilestone();
		} else {
			return null;
		}
	}

	public void setDefaultMilestone(String product, String defaultMilestone) {
		ProductEntry entry = products.get(product);
		if (entry == null) {
			entry = new ProductEntry(product);
			products.put(product, entry);
		}
		entry.setDefaultMilestone(defaultMilestone);
	}

	public Date getLastModifiedHeader() {
		return lastModifiedHeader;
	}

	public void setLastModifiedHeader(Date lastModifiedHeader) {
		this.lastModifiedHeader = lastModifiedHeader;
	}

	public boolean getUnconfirmedAllowed(String product) {
		ProductEntry entry = products.get(product);
		if (entry != null) {
			return BooleanUtils.toBoolean(entry.getUnconfirmedAllowed());
		} else {
			return false;
		}
	}
}
