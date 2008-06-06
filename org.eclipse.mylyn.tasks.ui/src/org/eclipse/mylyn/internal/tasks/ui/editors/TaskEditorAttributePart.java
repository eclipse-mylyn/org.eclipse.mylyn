/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.UpdateRepositoryConfigurationAction;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.sync.TaskJob;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorFactory;
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
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Steffen Pingel
 */
public class TaskEditorAttributePart extends AbstractTaskEditorPart {

	private static final int COLUMN_WIDTH = 140;

	private static final int MULTI_COLUMN_WIDTH = 380;

	private static final int MULTI_ROW_HEIGHT = 55;

	private List<AbstractAttributeEditor> attributeEditors;

	private boolean hasIncoming;

	public TaskEditorAttributePart() {
		setPartName("Attributes");
	}

	private void createAttributeControls(Composite attributesComposite, FormToolkit toolkit) {
		int columnCount = ((GridLayout) attributesComposite.getLayout()).numColumns;
		((GridLayout) attributesComposite.getLayout()).verticalSpacing = 6;

		int currentColumn = 1;
		int currentPriority = 0;
		for (AbstractAttributeEditor attributeEditor : attributeEditors) {
			int priority = (attributeEditor.getLayoutHint() != null) ? attributeEditor.getLayoutHint().getPriority()
					: LayoutHint.DEFAULT_PRIORITY;
			if (priority != currentPriority) {
				currentPriority = priority;
				if (currentColumn > 1) {
					while (currentColumn <= columnCount) {
						getManagedForm().getToolkit().createLabel(attributesComposite, "");
						currentColumn++;
					}
					currentColumn = 1;
				}
			}

			if (attributeEditor.hasLabel()) {
				attributeEditor.createLabelControl(attributesComposite, toolkit);
				Label label = attributeEditor.getLabelControl();
				GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
				currentColumn++;
			}

			attributeEditor.createControl(attributesComposite, toolkit);
			LayoutHint layoutHint = attributeEditor.getLayoutHint();
			GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
			if (layoutHint != null
					&& !(layoutHint.rowSpan == RowSpan.SINGLE && layoutHint.columnSpan == ColumnSpan.SINGLE)) {
				if (layoutHint.rowSpan == RowSpan.MULTIPLE) {
					gd.heightHint = MULTI_ROW_HEIGHT;
				}
				if (layoutHint.columnSpan == ColumnSpan.SINGLE) {
					gd.widthHint = COLUMN_WIDTH;
					gd.horizontalSpan = 1;
				} else {
					gd.widthHint = MULTI_COLUMN_WIDTH;
					gd.horizontalSpan = columnCount - currentColumn + 1;
				}
			} else {
				gd.widthHint = COLUMN_WIDTH;
				gd.horizontalSpan = 1;
			}
			attributeEditor.getControl().setLayoutData(gd);

			getTaskEditorPage().getAttributeEditorToolkit().adapt(attributeEditor);

			currentColumn += gd.horizontalSpan;
			currentColumn %= columnCount;
		}
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		initialize();

		Section section = createSection(parent, toolkit, getTaskData().isNew() || hasIncoming);

		// Attributes Composite- this holds all the combo fields and text fields
		Composite attributesComposite = toolkit.createComposite(section);
		attributesComposite.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Control focus = event.display.getFocusControl();
				if (focus instanceof Text && ((Text) focus).getEditable() == false) {
					getManagedForm().getForm().setFocus();
				}
			}
		});

		GridLayout attributesLayout = new GridLayout();
		attributesLayout.numColumns = 4;
		attributesLayout.horizontalSpacing = 5;
		attributesLayout.verticalSpacing = 6;
		attributesComposite.setLayout(attributesLayout);

		GridData attributesData = new GridData(GridData.FILL_BOTH);
		attributesData.horizontalSpan = 1;
		attributesData.grabExcessVerticalSpace = false;
		attributesComposite.setLayoutData(attributesData);

		createAttributeControls(attributesComposite, toolkit);
		toolkit.paintBordersFor(attributesComposite);

		section.setClient(attributesComposite);
		setSection(toolkit, section);
	}

	@Override
	protected void fillToolBar(ToolBarManager toolBar) {
		UpdateRepositoryConfigurationAction repositoryConfigRefresh = new UpdateRepositoryConfigurationAction() {
			@Override
			public void run() {
				getTaskEditorPage().showEditorBusy(true);
				final TaskJob job = TasksUiInternal.getJobFactory().createUpdateRepositoryConfigurationJob(
						getTaskEditorPage().getConnector(), getTaskEditorPage().getTaskRepository());
				job.addJobChangeListener(new JobChangeAdapter() {
					@Override
					public void done(IJobChangeEvent event) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								getTaskEditorPage().showEditorBusy(false);
								if (job.getStatus() != null) {
									getTaskEditorPage().getTaskEditor().setStatus(
											"Updating of repository configuration failed", "Update Failed",
											job.getStatus());
								} else {
									getTaskEditorPage().refreshFormContent();
								}
							}
						});
					}
				});
				job.setUser(true);
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
		repositoryConfigRefresh.setImageDescriptor(TasksUiImages.REPOSITORY_SYNCHRONIZE);
		repositoryConfigRefresh.selectionChanged(new StructuredSelection(getTaskEditorPage().getTaskRepository()));
		repositoryConfigRefresh.setToolTipText("Refresh attributes");
		toolBar.add(repositoryConfigRefresh);
	}

	private void initialize() {
		attributeEditors = new ArrayList<AbstractAttributeEditor>();
		hasIncoming = false;

		AttributeEditorFactory attributeEditorFactory = getTaskEditorPage().getAttributeEditorFactory();
		TaskAttributeMapper attributeMapper = getTaskData().getAttributeMapper();
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

		Collections.sort(attributeEditors, new Comparator<AbstractAttributeEditor>() {
			public int compare(AbstractAttributeEditor o1, AbstractAttributeEditor o2) {
				int p1 = (o1.getLayoutHint() != null) ? o1.getLayoutHint().getPriority() : LayoutHint.DEFAULT_PRIORITY;
				int p2 = (o2.getLayoutHint() != null) ? o2.getLayoutHint().getPriority() : LayoutHint.DEFAULT_PRIORITY;
				return p1 - p2;
			}
		});
	}

}
