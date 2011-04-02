/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Perforce - fixes for bug 318396
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonFormUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskDiffUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.UpdateRepositoryConfigurationAction;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.sync.TaskJob;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 * @author Kevin Sawicki
 */
public class TaskEditorAttributePart extends AbstractTaskEditorSection {

	private static final int LABEL_WIDTH = 110;

	private static final int COLUMN_WIDTH = 140;

	private static final int COLUMN_GAP = 20;

	private static final int MULTI_COLUMN_WIDTH = COLUMN_WIDTH + 5 + COLUMN_GAP + LABEL_WIDTH + 5 + COLUMN_WIDTH;

	private static final int MULTI_ROW_HEIGHT = 55;

	private List<AbstractAttributeEditor> attributeEditors;

	private boolean hasIncoming;

	private Composite attributesComposite;

	public TaskEditorAttributePart() {
		setPartName(Messages.TaskEditorAttributePart_Attributes);
	}

	private void createAttributeControls(Composite attributesComposite, FormToolkit toolkit, int columnCount) {
		int currentColumn = 1;
		int currentPriority = 0;
		for (AbstractAttributeEditor attributeEditor : attributeEditors) {
			int priority = (attributeEditor.getLayoutHint() != null)
					? attributeEditor.getLayoutHint().getPriority()
					: LayoutHint.DEFAULT_PRIORITY;
			if (priority != currentPriority) {
				currentPriority = priority;
				if (currentColumn > 1) {
					while (currentColumn <= columnCount) {
						getManagedForm().getToolkit().createLabel(attributesComposite, ""); //$NON-NLS-1$
						currentColumn++;
					}
					currentColumn = 1;
				}
			}

			if (attributeEditor.hasLabel()) {
				attributeEditor.createLabelControl(attributesComposite, toolkit);
				Label label = attributeEditor.getLabelControl();
				String text = label.getText();
				String shortenText = TaskDiffUtil.shortenText(label, text, LABEL_WIDTH);
				label.setText(shortenText);
				if (!text.equals(shortenText)) {
					label.setToolTipText(text);
				}
				GridData gd = GridDataFactory.fillDefaults()
						.align(SWT.RIGHT, SWT.CENTER)
						.hint(LABEL_WIDTH, SWT.DEFAULT)
						.create();
				if (currentColumn > 1) {
					gd.horizontalIndent = COLUMN_GAP;
					gd.widthHint = LABEL_WIDTH + COLUMN_GAP;
				}
				label.setLayoutData(gd);
				currentColumn++;
			}

			attributeEditor.createControl(attributesComposite, toolkit);
			LayoutHint layoutHint = attributeEditor.getLayoutHint();
			GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
			RowSpan rowSpan = (layoutHint != null && layoutHint.rowSpan != null) ? layoutHint.rowSpan : RowSpan.SINGLE;
			ColumnSpan columnSpan = (layoutHint != null && layoutHint.columnSpan != null)
					? layoutHint.columnSpan
					: ColumnSpan.SINGLE;
			if (rowSpan == RowSpan.SINGLE && columnSpan == ColumnSpan.SINGLE) {
				gd.widthHint = COLUMN_WIDTH;
				gd.horizontalSpan = 1;
			} else {
				if (rowSpan == RowSpan.MULTIPLE) {
					gd.heightHint = MULTI_ROW_HEIGHT;
				}
				if (columnSpan == ColumnSpan.SINGLE) {
					gd.widthHint = COLUMN_WIDTH;
					gd.horizontalSpan = 1;
				} else {
					gd.widthHint = MULTI_COLUMN_WIDTH;
					gd.horizontalSpan = columnCount - currentColumn + 1;
				}
			}
			attributeEditor.getControl().setLayoutData(gd);

			getTaskEditorPage().getAttributeEditorToolkit().adapt(attributeEditor);

			currentColumn += gd.horizontalSpan;
			currentColumn %= columnCount;
		}
	}

	@Override
	public void createControl(Composite parent, final FormToolkit toolkit) {
		initialize();
		super.createControl(parent, toolkit);
	}

	@Override
	protected String getInfoOverlayText() {
		TaskAttribute product = getModel().getTaskData().getRoot().getMappedAttribute(TaskAttribute.PRODUCT);
		TaskAttribute component = getModel().getTaskData().getRoot().getMappedAttribute(TaskAttribute.COMPONENT);

		String productLabel = ""; //$NON-NLS-1$
		if (product != null) {
			productLabel = getModel().getTaskData().getAttributeMapper().getValueLabel(product);
		}
		String componentLabel = ""; //$NON-NLS-1$
		if (component != null) {
			componentLabel = getModel().getTaskData().getAttributeMapper().getValueLabel(component);
		}

		if (!"".equals(productLabel) && !"".equals(componentLabel)) { //$NON-NLS-1$//$NON-NLS-2$
			return productLabel + " / " + componentLabel; //$NON-NLS-1$
		} else if (!"".equals(productLabel)) { //$NON-NLS-1$
			return productLabel;
		} else if (!"".equals(componentLabel)) { //$NON-NLS-1$
			return componentLabel;
		} else {
			return null;
		}
	}

	/**
	 * Integrator requested the ability to control whether the attributes section is expanded on creation.
	 */
	@Override
	protected boolean shouldExpandOnCreate() {
		return getTaskData().isNew() || hasIncoming;
	}

	@Override
	protected Control createContent(FormToolkit toolkit, Composite parent) {
		attributesComposite = toolkit.createComposite(parent);
		attributesComposite.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Control focus = event.display.getFocusControl();
				if (focus instanceof Text && ((Text) focus).getEditable() == false) {
					getManagedForm().getForm().setFocus();
				}
			}
		});

		GridLayout attributesLayout = EditorUtil.createSectionClientLayout();
		attributesLayout.numColumns = 4;
		attributesLayout.horizontalSpacing = 7;
		attributesLayout.verticalSpacing = 6;
		attributesComposite.setLayout(attributesLayout);

		GridData attributesData = new GridData(GridData.FILL_BOTH);
		attributesData.horizontalSpan = 1;
		attributesData.grabExcessVerticalSpace = false;
		attributesComposite.setLayoutData(attributesData);

		createAttributeControls(attributesComposite, toolkit, attributesLayout.numColumns);
		toolkit.paintBordersFor(attributesComposite);

		return attributesComposite;
	}

	@Override
	protected void fillToolBar(ToolBarManager toolBar) {
		UpdateRepositoryConfigurationAction repositoryConfigRefresh = new UpdateRepositoryConfigurationAction() {
			@Override
			public void run() {
				getTaskEditorPage().showEditorBusy(true);
				final TaskJob job = TasksUiInternal.getJobFactory().createUpdateRepositoryConfigurationJob(
						getTaskEditorPage().getConnector(), getTaskEditorPage().getTaskRepository(),
						getTaskEditorPage().getTask());
				job.addJobChangeListener(new JobChangeAdapter() {
					@Override
					public void done(IJobChangeEvent event) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								getTaskEditorPage().showEditorBusy(false);
								if (job.getStatus() != null) {
									getTaskEditorPage().getTaskEditor()
											.setStatus(
													Messages.TaskEditorAttributePart_Updating_of_repository_configuration_failed,
													Messages.TaskEditorAttributePart_Update_Failed, job.getStatus());
								} else {
									getTaskEditorPage().refresh();
								}
							}
						});
					}
				});
				job.setUser(true);
				// show the progress in the system task bar if this is a user job (i.e. forced)
				job.setProperty(WorkbenchUtil.SHOW_IN_TASKBAR_ICON_PROPERTY, Boolean.TRUE);
				job.setPriority(Job.INTERACTIVE);
				job.schedule();
			};

//			@Override
//			public void performUpdate(TaskRepository repository, AbstractRepositoryConnector connector,
//					IProgressMonitor monitor) {
//				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//					public void run() {
//						getTaskEditorPage().showEditorBusy(true);
//					}
//				});
//				try {
//					super.performUpdate(repository, connector, monitor);
//					AbstractTask task = getTaskEditorPage().getTask();
//					Job job = TasksUi.synchronizeTask(connector, task, true, null);
//					job.join();
//				} catch (InterruptedException e) {
//					// ignore
//				} finally {
//					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//						public void run() {
//							getTaskEditorPage().refreshFormContent();
//						}
//					});
//				}
//			}
		};
		repositoryConfigRefresh.setImageDescriptor(TasksUiImages.REPOSITORY_SYNCHRONIZE_SMALL);
		repositoryConfigRefresh.selectionChanged(new StructuredSelection(getTaskEditorPage().getTaskRepository()));
		repositoryConfigRefresh.setToolTipText(Messages.TaskEditorAttributePart_Refresh_Attributes);
		toolBar.add(repositoryConfigRefresh);
	}

	private void initialize() {
		attributeEditors = new ArrayList<AbstractAttributeEditor>();
		hasIncoming = false;

		Map<String, TaskAttribute> attributes = getTaskData().getRoot().getAttributes();
		for (TaskAttribute attribute : attributes.values()) {
			TaskAttributeMetaData properties = attribute.getMetaData();
			if (!TaskAttribute.KIND_DEFAULT.equals(properties.getKind())) {
				continue;
			}

			AbstractAttributeEditor attributeEditor = createAttributeEditor(attribute);
			if (attributeEditor != null) {
				attributeEditors.add(attributeEditor);
				if (getModel().hasIncomingChanges(attribute)) {
					hasIncoming = true;
				}
			}
		}

		Comparator<AbstractAttributeEditor> attributeSorter = createAttributeEditorSorter();
		if (attributeSorter != null) {
			Collections.sort(attributeEditors, attributeSorter);
		}
	}

	/**
	 * Create a comparator by which attribute editors will be sorted. By default attribute editors are sorted by layout
	 * hint priority. Subclasses may override this method to sort attribute editors in a custom way.
	 * 
	 * @return comparator for {@link AbstractAttributeEditor} objects
	 */
	protected Comparator<AbstractAttributeEditor> createAttributeEditorSorter() {
		return new Comparator<AbstractAttributeEditor>() {
			public int compare(AbstractAttributeEditor o1, AbstractAttributeEditor o2) {
				int p1 = (o1.getLayoutHint() != null) ? o1.getLayoutHint().getPriority() : LayoutHint.DEFAULT_PRIORITY;
				int p2 = (o2.getLayoutHint() != null) ? o2.getLayoutHint().getPriority() : LayoutHint.DEFAULT_PRIORITY;
				return p1 - p2;
			}
		};
	}

	@Override
	public boolean setFormInput(Object input) {
		if (input instanceof String) {
			String text = (String) input;
			Map<String, TaskAttribute> attributes = getTaskData().getRoot().getAttributes();
			for (TaskAttribute attribute : attributes.values()) {
				if (text.equals(attribute.getId())) {
					TaskAttributeMetaData properties = attribute.getMetaData();
					if (TaskAttribute.KIND_DEFAULT.equals(properties.getKind())) {
						selectReveal(attribute);
					}
				}
			}
		}
		return super.setFormInput(input);
	}

	public void selectReveal(TaskAttribute attribute) {
		if (attribute == null) {
			return;
		}
		if (!getSection().isExpanded()) {
			CommonFormUtil.setExpanded(getSection(), true);
		}
		EditorUtil.reveal(getTaskEditorPage().getManagedForm().getForm(), attribute.getId());
	}

}
