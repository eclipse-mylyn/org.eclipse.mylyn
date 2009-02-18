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

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCustomField;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTaskDataHandler;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;

/**
 * @author Rob Elves
 * @since 3.0
 */
public class BugzillaTaskEditorPage extends AbstractTaskEditorPage {

	public static final String ID_PART_BUGZILLA_PLANNING = "org.eclipse.mylyn.bugzilla.ui.editors.part.planning"; //$NON-NLS-1$

	public static final String ID_PART_BUGZILLA_FLAGS = "org.eclipse.mylyn.bugzilla.ui.editors.part.flags"; //$NON-NLS-1$

	private final Map<TaskAttribute, AbstractAttributeEditor> attributeEditorMap;

	private TaskDataModelListener productListener;

	public BugzillaTaskEditorPage(TaskEditor editor) {
		this(editor, BugzillaCorePlugin.CONNECTOR_KIND);
	}

	/**
	 * Call this constructor if extending the Bugzilla connector
	 * 
	 * @param editor
	 * @param connectorKind
	 */
	public BugzillaTaskEditorPage(TaskEditor editor, String connectorKind) {
		super(editor, connectorKind);
		this.attributeEditorMap = new HashMap<TaskAttribute, AbstractAttributeEditor>();
	}

	@Override
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> descriptors = super.createPartDescriptors();

		// remove unnecessary default editor parts
		for (TaskEditorPartDescriptor taskEditorPartDescriptor : descriptors) {
			if (taskEditorPartDescriptor.getId().equals(ID_PART_PEOPLE)) {
				descriptors.remove(taskEditorPartDescriptor);
				break;
			}
		}

		// Add Bugzilla Planning part
		try {
			TaskData data = TasksUi.getTaskDataManager().getTaskData(getTask());
			if (data != null) {
				TaskAttribute attrEstimatedTime = data.getRoot().getMappedAttribute(
						BugzillaAttribute.ESTIMATED_TIME.getKey());
				if (attrEstimatedTime != null) {
					descriptors.add(new TaskEditorPartDescriptor(ID_PART_BUGZILLA_PLANNING) {
						@Override
						public AbstractTaskEditorPart createPart() {
							return new BugzillaPlanningEditorPart();
						}
					}.setPath(PATH_ATTRIBUTES));
				}
			}
		} catch (CoreException e) {
			// ignore
		}

		// Add the updated Bugzilla people part
		descriptors.add(new TaskEditorPartDescriptor(ID_PART_PEOPLE) {
			@Override
			public AbstractTaskEditorPart createPart() {
				return new BugzillaPeoplePart();
			}
		}.setPath(PATH_PEOPLE));

		return descriptors;
	}

	@Override
	protected AttributeEditorFactory createAttributeEditorFactory() {
		AttributeEditorFactory factory = new AttributeEditorFactory(getModel(), getTaskRepository(), getEditorSite()) {
			@Override
			public AbstractAttributeEditor createEditor(String type, final TaskAttribute taskAttribute) {
				AbstractAttributeEditor editor;
				if (IBugzillaConstants.EDITOR_TYPE_KEYWORDS.equals(type)) {
					editor = new BugzillaKeywordAttributeEditor(getModel(), taskAttribute);
				} else if (IBugzillaConstants.EDITOR_TYPE_REMOVECC.equals(type)) {
					editor = new BugzillaCcAttributeEditor(getModel(), taskAttribute);
				} else if (IBugzillaConstants.EDITOR_TYPE_VOTES.equals(type)) {
					editor = new BugzillaVotesEditor(getModel(), taskAttribute);
				} else if (IBugzillaConstants.EDITOR_TYPE_FLAG.equals(type)) {
					editor = new FlagAttributeEditor(getModel(), taskAttribute);
				} else {
					editor = super.createEditor(type, taskAttribute);
					if (TaskAttribute.TYPE_BOOLEAN.equals(type)) {
						editor.setDecorationEnabled(false);
					}
				}

				if (editor != null && taskAttribute.getId().startsWith(BugzillaCustomField.CUSTOM_FIELD_PREFIX)) {
					editor.setLayoutHint(new LayoutHint(editor.getLayoutHint()) {

						@Override
						public int getPriority() {
							return super.getPriority() * 10;
						}
					});
				}

				TaskAttributeMetaData properties = taskAttribute.getMetaData();
				if (editor != null && IBugzillaConstants.EDITOR_TYPE_FLAG.equals(properties.getType())) {
					editor.setLayoutHint(new LayoutHint(editor.getLayoutHint()) {

						@Override
						public int getPriority() {
							return super.getPriority() * 5;
						}
					});
				}
				BugzillaTaskEditorPage.this.addToAttributeEditorMap(taskAttribute, editor);
				return editor;
			}
		};
		return factory;
	}

	@Override
	public void doSubmit() {

		TaskAttribute summaryAttribute = getModel().getTaskData().getRoot().getMappedAttribute(TaskAttribute.SUMMARY);
		if (summaryAttribute != null && summaryAttribute.getValue().length() == 0) {
			getTaskEditor().setMessage(Messages.BugzillaTaskEditorPage_Please_enter_a_short_summary_before_submitting,
					IMessageProvider.ERROR);
			AbstractTaskEditorPart part = getPart(ID_PART_SUMMARY);
			if (part != null) {
				part.setFocus();
			}
			return;
		}

		TaskAttribute componentAttribute = getModel().getTaskData().getRoot().getMappedAttribute(
				BugzillaAttribute.COMPONENT.getKey());
		if (componentAttribute != null && componentAttribute.getValue().length() == 0) {
			getTaskEditor().setMessage(Messages.BugzillaTaskEditorPage_Please_select_a_component_before_submitting,
					IMessageProvider.ERROR);
			AbstractTaskEditorPart part = getPart(ID_PART_ATTRIBUTES);
			if (part != null) {
				part.setFocus();
			}
			return;
		}

		TaskAttribute descriptionAttribute = getModel().getTaskData().getRoot().getMappedAttribute(
				TaskAttribute.DESCRIPTION);
		if (descriptionAttribute != null && descriptionAttribute.getValue().length() == 0) {
			getTaskEditor().setMessage(Messages.BugzillaTaskEditorPage_Please_enter_a_description_before_submitting,
					IMessageProvider.ERROR);
			AbstractTaskEditorPart descriptionPart = getPart(ID_PART_DESCRIPTION);
			if (descriptionPart != null) {
				descriptionPart.setFocus();
			}
			return;
		}

		// Force the most recent known good token onto the outgoing task data to ensure submit
		// bug#263318
		TaskAttribute attrToken = getModel().getTaskData().getRoot().getAttribute(BugzillaAttribute.TOKEN.getKey());
		if (attrToken != null) {
			attrToken.setValue(getModel().getTask().getAttribute(BugzillaAttribute.TOKEN.getKey()));
		}

		super.doSubmit();
	}

	@Override
	protected void createParts() {
		attributeEditorMap.clear();
		super.createParts();
	}

	@Override
	protected TaskDataModel createModel(TaskEditorInput input) throws CoreException {
		TaskDataModel model = super.createModel(input);
		BugzillaVersion bugzillaVersion = null;
		RepositoryConfiguration repositoryConfiguration = BugzillaCorePlugin.getRepositoryConfiguration(
				input.getTaskRepository(), false, new NullProgressMonitor());
		if (repositoryConfiguration != null) {
			bugzillaVersion = repositoryConfiguration.getInstallVersion();
		} else {
			bugzillaVersion = BugzillaVersion.MIN_VERSION;
		}
		if (bugzillaVersion.compareTo(BugzillaVersion.BUGZILLA_3_0) >= 0) {
			productListener = new ProductSelectionListener();
			model.addModelListener(productListener);
		}
		return model;
	}

	/**
	 * @since 3.1
	 */
	private void addToAttributeEditorMap(TaskAttribute attribute, AbstractAttributeEditor editor) {
		if (attributeEditorMap.containsKey(attribute)) {
			attributeEditorMap.remove(attribute);
		}
		attributeEditorMap.put(attribute, editor);
	}

	/**
	 * @since 3.1
	 */
	private AbstractAttributeEditor getEditorForAttribute(TaskAttribute attribute) {
		return attributeEditorMap.get(attribute);
	}

	public void refresh(TaskAttribute attributeComponent) {
		AbstractAttributeEditor editor = getEditorForAttribute(attributeComponent);
		if (editor != null) {
			try {
				editor.refresh();
			} catch (UnsupportedOperationException e) {
				// ignore
			}
		}
	}

	private class ProductSelectionListener extends TaskDataModelListener {
		@Override
		public void attributeChanged(TaskDataModelEvent event) {
			TaskAttribute taskAttribute = event.getTaskAttribute();
			if (taskAttribute != null) {
				if (taskAttribute.getId().equals(BugzillaAttribute.PRODUCT.getKey())) {
					RepositoryConfiguration repositoryConfiguration = null;
					try {
						repositoryConfiguration = BugzillaCorePlugin.getRepositoryConfiguration(
								getModel().getTaskRepository(), false, new NullProgressMonitor());
					} catch (CoreException e) {
						StatusHandler.log(new RepositoryStatus(getTaskRepository(), IStatus.ERROR,
								BugzillaUiPlugin.ID_PLUGIN, 0, "Failed to obtain repository configuration", e)); //$NON-NLS-1$
						getTaskEditor().setMessage("Problem occured when updating attributes", IMessageProvider.ERROR); //$NON-NLS-1$
						return;
					}

					TaskAttribute attributeComponent = taskAttribute.getTaskData().getRoot().getMappedAttribute(
							BugzillaAttribute.COMPONENT.getKey());
					if (attributeComponent != null) {
						List<String> optionValues = repositoryConfiguration.getComponents(taskAttribute.getValue());
						Collections.sort(optionValues);
						attributeComponent.clearOptions();
						for (String option : optionValues) {
							attributeComponent.putOption(option, option);
						}
						if (optionValues.size() == 1) {
							attributeComponent.setValue(optionValues.get(0));
						} else {
							attributeComponent.setValue(""); //$NON-NLS-1$
						}
						refresh(attributeComponent);
					}

					TaskAttribute attributeTargetMilestone = taskAttribute.getTaskData().getRoot().getMappedAttribute(
							BugzillaAttribute.TARGET_MILESTONE.getKey());
					if (attributeTargetMilestone != null) {
						List<String> optionValues = repositoryConfiguration.getTargetMilestones(taskAttribute.getValue());
						Collections.sort(optionValues);
						attributeTargetMilestone.clearOptions();
						for (String option : optionValues) {
							attributeTargetMilestone.putOption(option, option);
						}
						if (optionValues.size() == 1) {
							attributeTargetMilestone.setValue(optionValues.get(0));
						} else {
							attributeTargetMilestone.setValue("---"); //$NON-NLS-1$
						}
						refresh(attributeTargetMilestone);
					}

					TaskAttribute attributeVersion = taskAttribute.getTaskData().getRoot().getMappedAttribute(
							BugzillaAttribute.VERSION.getKey());
					if (attributeVersion != null) {
						List<String> optionValues = repositoryConfiguration.getVersions(taskAttribute.getValue());
						Collections.sort(optionValues);
						attributeVersion.clearOptions();
						for (String option : optionValues) {
							attributeVersion.putOption(option, option);
						}
						if (optionValues.size() == 1) {
							attributeVersion.setValue(optionValues.get(0));
						} else {
							attributeVersion.setValue("unspecified"); //$NON-NLS-1$
						}
						refresh(attributeVersion);
					}

					TaskAttribute attributeDefaultAssignee = taskAttribute.getTaskData().getRoot().getMappedAttribute(
							BugzillaAttribute.SET_DEFAULT_ASSIGNEE.getKey());
					if (attributeDefaultAssignee != null) {
						attributeDefaultAssignee.setValue("1"); //$NON-NLS-1$
						refresh(attributeDefaultAssignee);
					}

/*
 * 					add confirm_product_change to avoid verification page on submit
 */
					TaskAttribute attributeConfirmeProductChange = taskAttribute.getTaskData()
							.getRoot()
							.getMappedAttribute(BugzillaAttribute.CONFIRM_PRODUCT_CHANGE.getKey());
					if (attributeConfirmeProductChange == null) {
						attributeConfirmeProductChange = BugzillaTaskDataHandler.createAttribute(
								taskAttribute.getTaskData().getRoot(), BugzillaAttribute.CONFIRM_PRODUCT_CHANGE);
					}
					if (attributeConfirmeProductChange != null) {
						attributeConfirmeProductChange.setValue("1"); //$NON-NLS-1$
					}
				}
			}
		}
	}

}
