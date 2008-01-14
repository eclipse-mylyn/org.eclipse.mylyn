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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.AbstractAttributeMapper;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.internal.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.mylyn.internal.tasks.ui.views.ResetRepositoryConfigurationAction;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
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
 */
public class TaskEditorAttributePart extends AbstractTaskEditorPart {

	private static final int MULTI_ROW_HEIGHT = 55;

	private static final int COLUMN_WIDTH = 140;

	private static final int MULTI_COLUMN_WIDTH = 380;

	public TaskEditorAttributePart(AbstractTaskEditorPage taskEditorPage) {
		super(taskEditorPage);
	}

	private void createAttributeControls(Composite attributesComposite, FormToolkit toolkit) {
		List<AbstractAttributeEditor> attributeEditors = createAttributeEditors();

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
			currentColumn += gd.horizontalSpan;

			currentColumn %= columnCount;
		}
	}

	private List<AbstractAttributeEditor> createAttributeEditors() {
		List<AbstractAttributeEditor> attributeEditors = new ArrayList<AbstractAttributeEditor>();

		AttributeEditorFactory attributeEditorFactory = getTaskEditorPage().getAttributeEditorFactory();
		AbstractAttributeMapper attributeMapper = getTaskData().getAttributeFactory().getAttributeMapper();
		for (final RepositoryTaskAttribute attribute : getTaskData().getAttributes()) {
			if (attribute.isHidden()
					|| (attribute.isReadOnly() && (attribute.getValue() == null || attribute.getValue().length() == 0))) {
				continue;
			}

			String type = attributeMapper.getType(attribute);
			if (type != null) {
				attributeEditorFactory.createEditor(type, attribute);
				AbstractAttributeEditor attributeEditor = attributeEditorFactory.createEditor(type, attribute);
				if (attributeEditor != null) {
					attributeEditors.add(attributeEditor);
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

		return attributeEditors;
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		// Attributes Composite- this holds all the combo fields and text fields
		Composite attributesComposite = toolkit.createComposite(parent);
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

		setControl(attributesComposite);
	}

	protected void fillToolBar(ToolBarManager toolBar) {
		ResetRepositoryConfigurationAction repositoryConfigRefresh = new ResetRepositoryConfigurationAction() {
			@Override
			public void performUpdate(TaskRepository repository, AbstractRepositoryConnector connector,
					IProgressMonitor monitor) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

					public void run() {
						getTaskEditorPage().setGlobalBusy(true);

					}
				});
				try {
					super.performUpdate(repository, connector, monitor);
					if (connector != null) {
						final AbstractTask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(
								TaskEditorAttributePart.this.getTaskRepository().getUrl(), getTaskData().getId());
						if (task != null) {
							TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true,
									new JobChangeAdapter() {

										@Override
										public void done(IJobChangeEvent event) {
											PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

												public void run() {
													getTaskEditorPage().refreshEditor();
												}
											});

										}
									});
						}
					}
				} catch (Exception e) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

						public void run() {
							getTaskEditorPage().refreshEditor();
						}
					});
				}
			}
		};
		repositoryConfigRefresh.setImageDescriptor(TasksUiImages.REPOSITORY_SYNCHRONIZE);
		repositoryConfigRefresh.selectionChanged(new StructuredSelection(getTaskRepository()));
		repositoryConfigRefresh.setToolTipText("Refresh attributes");
		toolBar.add(repositoryConfigRefresh);
	}

}
