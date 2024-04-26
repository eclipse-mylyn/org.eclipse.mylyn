/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Component;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Field;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.FieldValues;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.FlagType;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.FlagTypes;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Parameters;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Product;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.SortableActiveEntry;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.StatusTransition;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;

public class BugzillaRestConfiguration implements Serializable {

	private static final BugzillaRestCreateTaskSchema SCHEMA = BugzillaRestCreateTaskSchema.getDefault();

	private static final long serialVersionUID = 4173223872076958202L;

	private final String repositoryId;

	private Map<String, Field> fields;

	private Map<String, Product> products;

	private Parameters parameters;

	public BugzillaRestConfiguration(String repositoryId) {
		this.repositoryId = repositoryId;
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	void setFields(Map<String, Field> fields) {
		this.fields = fields.entrySet()
				.stream()
				.sorted((e1, e2) -> e1.getValue().getName().compareTo(e2.getValue().getName()))
				.collect(Collectors.collectingAndThen(
						Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new),
						Collections::unmodifiableMap));

	}

	public Map<String, Field> getFields() {
		return fields;
	}

	public Field getFieldWithName(String fieldName) {
		return fields.get(fieldName);
	}

	void setProducts(Map<String, Product> products) {
		this.products = products.entrySet()
				.stream()
				.sorted((e1, e2) -> e1.getValue().getName().compareTo(e2.getValue().getName()))
				.collect(Collectors.collectingAndThen(
						Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new),
						Collections::unmodifiableMap));
	}

	public Map<String, Product> getProducts() {
		return products;
	}

	public Product getProductWithName(String productName) {
		return products.get(productName);
	}

	void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	public Parameters getParameters() {
		return parameters;
	}

	private Component getProductComponentWithName(@NonNull Product product, String name) {
		return product.getComponentWithName(name);
	}

	public void updateInitialTaskData(TaskData data) throws CoreException {
		setProductOptions(data);
		updateProductOptions(data);
		for (String key : data.getRoot().getAttributes().keySet()) {
			if (key.equals(BugzillaRestTaskSchema.getDefault().ADD_SELF_CC.getKey())
					|| key.equals(BugzillaRestTaskSchema.getDefault().NEW_COMMENT.getKey())
					|| key.equals(BugzillaRestTaskSchema.getDefault().DUPE_OF.getKey())
					|| key.equals(TaskAttribute.OPERATION)
					|| key.equals(BugzillaRestTaskSchema.getDefault().DATE_MODIFICATION.getKey())
					|| key.equals(BugzillaRestTaskSchema.getDefault().RESET_ASSIGNED_TO.getKey())) {
				continue;
			}
			TaskAttribute attribute = data.getRoot().getAttribute(key);
			if (!key.equals(SCHEMA.PRODUCT.getKey())) {
				String configName = mapTaskAttributeKey2ConfigurationFields(key);
				if ("addCC".equals(configName) || "removeCC".equals(configName) //$NON-NLS-1$ //$NON-NLS-2$
						|| "reset_qa_contact".equals(configName)) { //$NON-NLS-1$
					continue;
				}
				Field configField = getFieldWithName(configName);
				if (configField == null) {
					continue;
				}
				if (configName.equals("component") || configName.equals("version") //$NON-NLS-1$ //$NON-NLS-2$
						|| configName.equals("target_milestone")) { //$NON-NLS-1$
					if (attribute.getOptions().size() == 1 && attribute.getValue().isEmpty()) {
						attribute.setValue((String) attribute.getOptions().values().toArray()[0]);
					}
				} else {
					FieldValues[] val = configField.getValues();
					if (val != null && val.length > 0) {
						for (FieldValues fieldValues : val) {
							if (configName.equals("bug_status")) { //$NON-NLS-1$
								if (fieldValues.getName() == null) {
									for (StatusTransition bugzillaRestBugStatusTransition : fieldValues
											.getCanChangeTo()) {
										attribute.putOption(bugzillaRestBugStatusTransition.getName(),
												bugzillaRestBugStatusTransition.getName());
									}
								}
							} else {
								attribute.putOption(fieldValues.getName(), fieldValues.getName());
							}

						}
					}
				}
			}
			if (attribute.getValue().isEmpty()) {
				String newValue = getValueFromParameter(key);
				if (newValue != null) {
					attribute.setValue(getValueFromParameter(key));
				}
			}

		}
		for (Field Field : fields.values()) {
			if (Field.isCustom() && Field.isOnBugEntry()) {
				TaskAttribute attribute = data.getRoot().createAttribute(Field.getName());
				if (attribute != null) {
					attribute.getMetaData().defaults().setLabel(Field.getDisplayName());
					attribute.getMetaData().setKind(TaskAttribute.KIND_DEFAULT);
					String type = getAttributeTypFromFieldTyp(Field.getType());
					attribute.getMetaData().setType(type);
					if (type.equals(TaskAttribute.TYPE_SINGLE_SELECT)) {
						attribute.getMetaData().setRequired(true);
					}

					FieldValues[] values1 = Field.getValues();
					if (values1 != null) {
						for (FieldValues FieldValues : values1) {
							attribute.putOption(FieldValues.getName(), FieldValues.getName());
						}
					}
					attribute.getMetaData().setReadOnly(false);
				}
			}
		}
		updateKeyword(data);
	}

	private String getValueFromParameter(String attributeId) {
		if (attributeId.equals(TaskAttribute.PRIORITY)) {
			return getParameters().getDefaultpriority();
		} else if (attributeId.equals(TaskAttribute.SEVERITY)) {
			return getParameters().getDefaultseverity();
		} else if (attributeId.equals("platform")) { //$NON-NLS-1$
			if (getParameters().getDefaultplatform() == null || getParameters().getDefaultplatform().isEmpty()) {
				return Messages.BugzillaRestConfiguration_PlatformAll;
			} else {
				return getParameters().getDefaultplatform();
			}
		} else if (attributeId.equals("os")) { //$NON-NLS-1$
			if (getParameters().getDefaultopsys() == null || getParameters().getDefaultopsys().isEmpty()) {
				return Messages.BugzillaRestConfiguration_OsAll;
			} else {
				return getParameters().getDefaultopsys();
			}
		}
		return ""; //$NON-NLS-1$
	}

	private String getAttributeTypFromFieldTyp(int fieldTyp) throws CoreException {
		return switch (fieldTyp) {
			case 1://Free Text
				yield TaskAttribute.TYPE_SHORT_TEXT;
			case 2: //DropDown
				yield TaskAttribute.TYPE_SINGLE_SELECT;
			case 3: //Multiple-Selection Box
				yield TaskAttribute.TYPE_MULTI_SELECT;
			case 4: //Large Text Box
				yield TaskAttribute.TYPE_LONG_TEXT;
			case 5: //Date/Time
				yield TaskAttribute.TYPE_DATETIME;
			case 6: //Bug Id
				yield TaskAttribute.TYPE_INTEGER;
			case 7: //Bug URLs
				yield TaskAttribute.TYPE_URL;
			default: {
				Status status = new Status(IStatus.INFO, BugzillaRestCore.ID_PLUGIN,
						Messages.BugzillaRestConfiguration_UnknownCustomFieldType + fieldTyp);
				StatusHandler.log(status);
				throw new CoreException(status);
			}
		};
	}

	private String mapTaskAttributeKey2ConfigurationFields(String taskAttributeKey) {
		String resultString;
		if (taskAttributeKey.equals("task.common.summary")) { //$NON-NLS-1$
			resultString = "short_desc"; //$NON-NLS-1$
		} else if (taskAttributeKey.equals(TaskAttribute.PRODUCT) //
				|| taskAttributeKey.equals(TaskAttribute.RESOLUTION) //
				|| taskAttributeKey.equals(TaskAttribute.PRIORITY) //
				|| taskAttributeKey.equals(TaskAttribute.COMPONENT) //
				|| taskAttributeKey.equals(TaskAttribute.VERSION)) {
			resultString = taskAttributeKey.substring(12);
		} else if (taskAttributeKey.equals(TaskAttribute.STATUS)) {
			resultString = "bug_status"; //$NON-NLS-1$
		} else if (taskAttributeKey.equals(TaskAttribute.USER_ASSIGNED)) {
			resultString = "assigned_to"; //$NON-NLS-1$
		} else if (taskAttributeKey.equals(TaskAttribute.USER_CC)) {
			resultString = "cc"; //$NON-NLS-1$
		} else if (taskAttributeKey.equals(TaskAttribute.DESCRIPTION)) {
			resultString = "longdesc"; //$NON-NLS-1$
		} else if (taskAttributeKey.equals("description_is_private")) { //$NON-NLS-1$
			resultString = "longdescs.isprivate"; //$NON-NLS-1$
		} else if (taskAttributeKey.equals("os")) { //$NON-NLS-1$
			resultString = "op_sys"; //$NON-NLS-1$
		} else if (taskAttributeKey.equals("platform")) { //$NON-NLS-1$
			resultString = "rep_platform"; //$NON-NLS-1$
		} else if (taskAttributeKey.equals(TaskAttribute.SEVERITY)) {
			resultString = "bug_severity"; //$NON-NLS-1$
		} else if (taskAttributeKey.equals("comment")) { //$NON-NLS-1$
			resultString = "longdesc"; //$NON-NLS-1$
		} else if (taskAttributeKey.equals("blocks")) { //$NON-NLS-1$
			resultString = "blocked"; //$NON-NLS-1$
		} else if (taskAttributeKey.equals("depends_on")) { //$NON-NLS-1$
			resultString = "dependson"; //$NON-NLS-1$
		} else {

			resultString = taskAttributeKey;
		}
		return resultString;
	}

	private void setAttributeOptionsForProduct(TaskAttribute taskAttribute, Product actualProduct) {
		taskAttribute.clearOptions();
		if (taskAttribute.getId().equals(SCHEMA.TARGET_MILESTONE.getKey())) {
			internalSetAttributeOptions(taskAttribute, actualProduct.getMilestones());
		} else if (taskAttribute.getId().equals(SCHEMA.VERSION.getKey())) {
			internalSetAttributeOptions(taskAttribute, actualProduct.getVersions());
		} else if (taskAttribute.getId().equals(SCHEMA.COMPONENT.getKey())) {
			internalSetAttributeOptions(taskAttribute, actualProduct.getComponents());
		}
	}

	private void internalSetAttributeOptions(TaskAttribute taskAttribute, SortableActiveEntry[] actualProductEntry) {
		boolean found = false;
		String actualValue = taskAttribute.getValue();
		for (SortableActiveEntry SortableActiveEntry : actualProductEntry) {
			if (SortableActiveEntry.isActive()) {
				taskAttribute.putOption(SortableActiveEntry.getName(), SortableActiveEntry.getName());
				if (!found) {
					found = actualValue.equals(SortableActiveEntry.getName());
				}
			}
		}
		if (!found) {
			taskAttribute.setValue(""); //$NON-NLS-1$
		}
	}

	public boolean setProductOptions(@NonNull TaskData taskData) {
		TaskAttribute attributeProduct = taskData.getRoot().getMappedAttribute(SCHEMA.PRODUCT.getKey());
		if (attributeProduct != null) {
			SortedSet<String> products = new TreeSet<>();
			Field configFieldComponent = getFieldWithName("component"); //$NON-NLS-1$
			FieldValues[] val = configFieldComponent.getValues();
			if (val != null && val.length > 0) {
				for (FieldValues fieldValues : val) {
					for (String visibilityValue : fieldValues.getVisibilityValues()) {
						if (!products.contains(visibilityValue)) {
							products.add(visibilityValue);
						}
					}
				}
				attributeProduct.clearOptions();
				for (String productName : products) {
					attributeProduct.putOption(productName, productName);
				}
			}

			return true;
		}
		return false;
	}

	public boolean updateProductOptions(@NonNull TaskData taskData) {
		if (taskData == null) {
			return false;
		}
		TaskAttribute attributeProduct = taskData.getRoot().getMappedAttribute(SCHEMA.PRODUCT.getKey());
		if (attributeProduct == null) {
			return false;
		}
		if (!attributeProduct.getValue().isEmpty()) {
			Product actualProduct = getProductWithName(attributeProduct.getValue());

			TaskAttribute attributeComponent = taskData.getRoot().getMappedAttribute(SCHEMA.COMPONENT.getKey());
			if (attributeComponent != null) {
				setAttributeOptionsForProduct(attributeComponent, actualProduct);
			}
			TaskAttribute attributeVersion = taskData.getRoot().getMappedAttribute(SCHEMA.VERSION.getKey());
			if (attributeVersion != null) {
				setAttributeOptionsForProduct(attributeVersion, actualProduct);
			}
			TaskAttribute attributeTargetMilestone = taskData.getRoot()
					.getMappedAttribute(SCHEMA.TARGET_MILESTONE.getKey());
			if (attributeTargetMilestone != null) {
				setAttributeOptionsForProduct(attributeTargetMilestone, actualProduct);
			}
		} else {
			for (Product product : getProducts().values()) {
				attributeProduct.putOption(product.getName(), product.getName());
			}
			TaskAttribute attributeComponent = taskData.getRoot().getMappedAttribute(SCHEMA.COMPONENT.getKey());
			if (attributeComponent != null) {
				setAllAttributeOptions(attributeComponent, getFieldWithName("component")); //$NON-NLS-1$
			}
			TaskAttribute attributeVersion = taskData.getRoot().getMappedAttribute(SCHEMA.VERSION.getKey());
			if (attributeVersion != null) {
				setAllAttributeOptions(attributeVersion, getFieldWithName("version")); //$NON-NLS-1$
			}
			TaskAttribute attributeTargetMilestone = taskData.getRoot()
					.getMappedAttribute(SCHEMA.TARGET_MILESTONE.getKey());
			if (attributeTargetMilestone != null) {
				setAllAttributeOptions(attributeTargetMilestone, getFieldWithName("target_milestone")); //$NON-NLS-1$
			}
		}
		return true;
	}

	private void setAllAttributeOptions(TaskAttribute updateAttribute, Field configField) {
		FieldValues[] val = configField.getValues();
		if (val != null && val.length > 0) {
			for (FieldValues fieldValues : val) {
				updateAttribute.putOption(fieldValues.getName(), fieldValues.getName());
			}
		}
	}

	public void addValidOperations(TaskData bugReport) {
		TaskAttribute attributeStatus = bugReport.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		String attributeStatusValue = attributeStatus.getValue();
		TaskAttribute operationAttribute = bugReport.getRoot().getAttribute(TaskAttribute.OPERATION);
		if (operationAttribute == null) {
			operationAttribute = bugReport.getRoot().createAttribute(TaskAttribute.OPERATION);
		}
		TaskAttribute attribute = bugReport.getRoot()
				.createAttribute(TaskAttribute.PREFIX_OPERATION + attributeStatusValue);
		if (attributeStatusValue.equals("RESOLVED")) { //$NON-NLS-1$
			attribute.getMetaData()
			.putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID,
					BugzillaRestTaskSchema.getDefault().RESOLUTION.getKey());
		}

		TaskOperation.applyTo(attribute, attributeStatusValue, attributeStatusValue);
		// set as default
		TaskOperation.applyTo(operationAttribute, attributeStatusValue, attributeStatusValue);
		Field status = getFieldWithName("bug_status"); //$NON-NLS-1$
		for (FieldValues fieldValues : status.getValues()) {
			if ((attributeStatusValue == null || attributeStatusValue.isEmpty()) && fieldValues.getName() == null
					|| attributeStatusValue != null && attributeStatusValue.equals(fieldValues.getName())) {
				for (StatusTransition statusTransition : fieldValues.getCanChangeTo()) {
					attribute = bugReport.getRoot()
							.createAttribute(TaskAttribute.PREFIX_OPERATION + statusTransition.name);
					TaskOperation.applyTo(attribute, statusTransition.name, statusTransition.name);
					if (statusTransition.name != null && statusTransition.name.equals("RESOLVED")) { //$NON-NLS-1$
						TaskAttribute attrResolvedInput = attribute.getTaskData()
								.getRoot()
								.createAttribute("resolutionInput"); //$NON-NLS-1$
						attrResolvedInput.getMetaData().setType(TaskAttribute.TYPE_SINGLE_SELECT);
						attribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, "resolutionInput"); //$NON-NLS-1$
						Field resolution = getFieldWithName("resolution"); //$NON-NLS-1$
						for (FieldValues resolutionValues : resolution.getValues()) {
							if (resolutionValues.getName().compareTo("DUPLICATE") != 0) { //$NON-NLS-1$
								attrResolvedInput.putOption(resolutionValues.getName(), resolutionValues.getName());
							}
						}
					}
				}
			}
		}
		attribute = bugReport.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + "duplicate"); //$NON-NLS-1$
		TaskOperation.applyTo(attribute, "duplicate", "Mark as Duplicate"); //$NON-NLS-1$ //$NON-NLS-2$
		TaskAttribute attrResolvedInput = attribute.getTaskData()
				.getRoot()
				.getAttribute(BugzillaRestTaskSchema.getDefault().DUPE_OF.getKey());
		if (attrResolvedInput == null) {
			attrResolvedInput = attribute.getTaskData()
					.getRoot()
					.createAttribute(BugzillaRestTaskSchema.getDefault().DUPE_OF.getKey());
		}
		attrResolvedInput.getMetaData().setType(TaskAttribute.TYPE_TASK_DEPENDENCY);
		attribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, attrResolvedInput.getId());
	}

	public boolean updateFlags(@NonNull TaskData taskData) {
		List<String> existingFlags = new ArrayList<>();
		TaskAttribute attributeProduct = taskData.getRoot().getMappedAttribute(SCHEMA.PRODUCT.getKey());
		TaskAttribute attributeComponent = taskData.getRoot().getMappedAttribute(SCHEMA.COMPONENT.getKey());
		Product actualProduct = getProductWithName(attributeProduct.getValue());
		Component actualComponent = getProductComponentWithName(actualProduct, attributeComponent.getValue());
		FlagTypes flagTypes = actualComponent.getFlagTypes();

		for (TaskAttribute attribute : taskData.getRoot().getAttributes().values()) {
			if (attribute.getId().startsWith(TaskAttribute.PREFIX_ATTACHMENT)
					|| attribute.getId().equals(TaskAttribute.NEW_ATTACHMENT)) {
				List<String> existingAttachmentFlags = new ArrayList<>();
				for (TaskAttribute attachmentAttribute : attribute.getAttributes().values()) {
					updateFlag(flagTypes.getAttachment(), existingAttachmentFlags, attachmentAttribute);
				}
				addMissingFlagsInternal(attribute, flagTypes.getAttachment(), existingAttachmentFlags);
			} else {
				updateFlag(flagTypes.getBug(), existingFlags, attribute);
			}
		}
		addMissingFlagsInternal(taskData.getRoot(), flagTypes.getBug(), existingFlags);

		return false;
	}

	private void updateFlag(FlagType[] flagTypes, List<String> existingAttachmentFlags, TaskAttribute flagAttribute) {
		if (flagAttribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG)) {
			TaskAttribute stateAttribute = flagAttribute.getAttribute("state"); //$NON-NLS-1$))
			stateAttribute.putOption("", ""); //$NON-NLS-1$ //$NON-NLS-2$
			String flagName = stateAttribute.getMetaData().getLabel();
			if (!existingAttachmentFlags.contains(flagName)) {
				existingAttachmentFlags.add(flagName);
			}
			for (FlagType flagType : flagTypes) {
				if (flagType.getName().equals(flagName)) {
					if (flagType.isRequestable()) {
						stateAttribute.putOption("?", "?"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					updateRequestee(flagAttribute, flagType);
					break;
				}
			}
			stateAttribute.putOption("-", "-"); //$NON-NLS-1$ //$NON-NLS-2$
			stateAttribute.putOption("+", "+"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public void addMissingFlags(TaskData taskData) {
		List<String> existingFlags = new ArrayList<>();
		TaskAttribute attributeProduct = taskData.getRoot().getMappedAttribute(SCHEMA.PRODUCT.getKey());
		TaskAttribute attributeComponent = taskData.getRoot().getMappedAttribute(SCHEMA.COMPONENT.getKey());
		if (attributeProduct.getValue().equals("") || attributeComponent.getValue().equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		Product actualProduct = getProductWithName(attributeProduct.getValue());
		Component actualComponent = getProductComponentWithName(actualProduct, attributeComponent.getValue());
		FlagTypes flagTypes = actualComponent.getFlagTypes();
		addMissingFlagsInternal(taskData.getRoot(), flagTypes.getBug(), existingFlags);
	}

	private void addMissingFlagsInternal(TaskAttribute rootTaskAttribute, FlagType[] flagTypes,
			List<String> existingFlags) {
		for (FlagType flagType : flagTypes) {
			if (existingFlags.contains(flagType.getName()) && !flagType.isMultiplicable()) {
				continue;
			}
			BugzillaRestFlagMapper mapper = new BugzillaRestFlagMapper();
			mapper.setRequestee(""); //$NON-NLS-1$
			mapper.setSetter(""); //$NON-NLS-1$
			mapper.setState(" "); //$NON-NLS-1$
			mapper.setName(flagType.getName());
			mapper.setNumber(0);
			mapper.setDescription(flagType.getDescription());
			mapper.setTypeId(flagType.getId());
			TaskAttribute attribute = rootTaskAttribute
					.createAttribute(IBugzillaRestConstants.KIND_FLAG_TYPE + flagType.getId());
			mapper.applyTo(attribute);
			TaskAttribute stateAttribute = attribute.getAttribute("state"); //$NON-NLS-1$))
			stateAttribute.putOption("", ""); //$NON-NLS-1$ //$NON-NLS-2$
			if (flagType.isRequestable()) {
				stateAttribute.putOption("?", "?"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			updateRequestee(attribute, flagType);
			attribute.getMetaData().putValue(TaskAttribute.META_DESCRIPTION, flagType.getDescription());
			stateAttribute.putOption("-", "-"); //$NON-NLS-1$ //$NON-NLS-2$
			stateAttribute.putOption("+", "+"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public void updateRequestee(TaskAttribute attribute, FlagType flagType) {
		TaskAttribute requestee = attribute.getAttribute("requestee"); //$NON-NLS-1$
		if (requestee == null) {
			requestee = attribute.createMappedAttribute("requestee"); //$NON-NLS-1$
			requestee.getMetaData().defaults().setType(TaskAttribute.TYPE_PERSON);
			requestee.setValue(""); //$NON-NLS-1$
		}
		requestee.getMetaData().setReadOnly(!flagType.isRequesteeble());
	}

	public void updateAttachmentFlags(@NonNull TaskAttribute attribute) {
		TaskAttribute attributeProduct = attribute.getParentAttribute().getMappedAttribute(SCHEMA.PRODUCT.getKey());
		TaskAttribute attributeComponent = attribute.getParentAttribute().getMappedAttribute(SCHEMA.COMPONENT.getKey());
		Product actualProduct = getProductWithName(attributeProduct.getValue());
		Component actualComponent = getProductComponentWithName(actualProduct, attributeComponent.getValue());
		FlagTypes flagTypes = actualComponent.getFlagTypes();

		List<String> existingAttachmentFlags = new ArrayList<>();
		for (TaskAttribute attachmentAttribute : attribute.getAttributes().values()) {
			updateFlag(flagTypes.getAttachment(), existingAttachmentFlags, attachmentAttribute);
		}
		addMissingFlagsInternal(attribute, flagTypes.getAttachment(), existingAttachmentFlags);
	}

	public void updateKeyword(TaskData taskData) {
		TaskAttribute attributeKeywords = taskData.getRoot().getMappedAttribute(SCHEMA.KEYWORDS.getKey());
		Field keywords = getFieldWithName("keywords"); //$NON-NLS-1$
		FieldValues[] keywordList = keywords.getValues();
		attributeKeywords.clearOptions();
		for (FieldValues fieldValues : keywordList) {
			attributeKeywords.putOption(fieldValues.getName(), fieldValues.getDescription());
		}
	}

}