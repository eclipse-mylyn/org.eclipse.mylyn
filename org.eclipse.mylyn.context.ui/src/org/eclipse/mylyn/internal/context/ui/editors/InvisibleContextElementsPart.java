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

package org.eclipse.mylyn.internal.context.ui.editors;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.commons.ui.SwtUtil;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author Shawn Minto
 */
public class InvisibleContextElementsPart {

	private final class InteractionElementTableSorter extends ViewerSorter {

		private int criteria = 0;

		private boolean isDecending = true;

		private final ITableLabelProvider labelProvider;

		public InteractionElementTableSorter(ITableLabelProvider labelProvider) {
			this.labelProvider = labelProvider;
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			int result = 0;

			String value1 = labelProvider.getColumnText(e1, criteria);
			String value2 = labelProvider.getColumnText(e2, criteria);

			if (value1 == null && value2 != null) {
				result = -1;
			} else if (value1 != null && value2 == null) {
				result = 1;
			} else if (value1 != null && value2 != null) {
				result = value1.compareTo(value2);
			}

			return isDecending() ? (result * -1) : result;
		}

		public boolean isDecending() {
			return isDecending;
		}

		public void setCriteria(int index) {
			if (criteria == index) {
				isDecending = !isDecending;
			} else {
				isDecending = false;
			}
			criteria = index;
		}

	}

	private final class InteractionElementTableLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getText(Object element) {
			if (element instanceof IInteractionElement) {
				return ((IInteractionElement) element).getHandleIdentifier();
			}
			return super.getText(element);
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof IInteractionElement) {
				if (columnIndex == 0) {
					return ((IInteractionElement) element).getHandleIdentifier();
				} else if (columnIndex == 1) {
					return ((IInteractionElement) element).getContentType();
				}
			}
			return ""; //$NON-NLS-1$
		}
	}

	private final class RemoveInvisibleAction extends Action {
		public RemoveInvisibleAction() {
			setText(Messages.ContextEditorFormPage_Remove_Invisible_);
			setToolTipText(Messages.ContextEditorFormPage_Remove_Invisible_);
			setImageDescriptor(TasksUiImages.CONTEXT_CLEAR);
		}

		@Override
		public void run() {
			if (commonViewer == null) {
				MessageDialog.openWarning(WorkbenchUtil.getShell(), Messages.ContextEditorFormPage_Remove_Invisible,
						Messages.ContextEditorFormPage_Activate_task_to_remove_invisible);
				return;
			}

			boolean confirmed = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
					Messages.ContextEditorFormPage_Remove_Invisible,
					Messages.ContextEditorFormPage_Remove_every_element_not_visible);
			if (confirmed) {

				if (ContextCore.getContextManager().isContextActive()) {
					try {
						final Collection<Object> allVisible = getAllVisibleElementsInContextPage();
						PlatformUI.getWorkbench().getProgressService().busyCursorWhile(new IRunnableWithProgress() {

							public void run(IProgressMonitor monitor) throws InvocationTargetException,
									InterruptedException {
								monitor.beginTask(Messages.InvisibleContextElementsPart_Collecting_all_invisible,
										IProgressMonitor.UNKNOWN);
								if (allVisible != null) {
									IInteractionContext context = ContextCore.getContextManager().getActiveContext();
									final List<IInteractionElement> allToRemove = getAllInvisibleElements(context,
											allVisible);
									Display.getDefault().asyncExec(new Runnable() {
										public void run() {
											ContextCore.getContextManager().deleteElements(allToRemove);
										}
									});

								} else {
									MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
											Messages.ContextEditorFormPage_Remove_Invisible,
											Messages.ContextEditorFormPage_No_context_active);
								}
							}
						});
					} catch (InvocationTargetException e) {
						StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, e.getMessage(), e));
					} catch (InterruptedException e) {
						StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, e.getMessage(), e));
					}

				} else {
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
							Messages.ContextEditorFormPage_Remove_Invisible,
							Messages.ContextEditorFormPage_No_context_active);
				}
			}
		}
	}

	private TableViewer invisibleTable;

	private Section invisibleSection;

	private final CommonViewer commonViewer;

	public InvisibleContextElementsPart(CommonViewer commonViewer) {
		this.commonViewer = commonViewer;
	}

	public Control createControl(FormToolkit toolkit, Composite composite) {
		invisibleSection = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		invisibleSection.setText(NLS.bind(Messages.InvisibleContextElementsPart_Invisible_elements, "0")); //$NON-NLS-1$
		invisibleSection.setEnabled(false);

		Composite toolbarComposite = toolkit.createComposite(invisibleSection);
		toolbarComposite.setBackground(null);
		invisibleSection.setTextClient(toolbarComposite);
		RowLayout rowLayout = new RowLayout();
		rowLayout.marginTop = 0;
		rowLayout.marginBottom = 0;
		toolbarComposite.setLayout(rowLayout);

		ToolBarManager toolbarManager = new ToolBarManager(SWT.FLAT);
		toolbarManager.add(new RemoveInvisibleAction());
		toolbarManager.createControl(toolbarComposite);
		toolbarManager.markDirty();
		toolbarManager.update(true);

		Composite invisibleSectionClient = toolkit.createComposite(invisibleSection);
		invisibleSectionClient.setLayout(new GridLayout());
		invisibleSection.setClient(invisibleSectionClient);

		Composite tableComposite = toolkit.createComposite(invisibleSectionClient);
		GridDataFactory.fillDefaults().hint(500, 200).grab(true, false).applyTo(tableComposite);
		TableColumnLayout layout = new TableColumnLayout();
		tableComposite.setLayout(layout);

		invisibleTable = new TableViewer(tableComposite, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		invisibleTable.setColumnProperties(new String[] { Messages.InvisibleContextElementsPart_Structure_handle,
				Messages.InvisibleContextElementsPart_Structure_kind });
		invisibleTable.getTable().setHeaderVisible(true);

		Table table = invisibleTable.getTable();
		toolkit.adapt(table);
		table.setMenu(null);

		InteractionElementTableLabelProvider labelProvider = new InteractionElementTableLabelProvider();
		invisibleTable.setLabelProvider(labelProvider);
		invisibleTable.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// ignore
			}

			public void dispose() {
				// ignore
			}

			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof Collection<?>) {
					return ((Collection<?>) inputElement).toArray();
				}
				return new Object[0];
			}
		});

		InteractionElementTableSorter invisibleTableSorter = new InteractionElementTableSorter(labelProvider);
		invisibleTableSorter.setCriteria(0);
		invisibleTable.setSorter(invisibleTableSorter);
		createColumn(layout, 0, Messages.InvisibleContextElementsPart_Structure_handle, 400, table,
				invisibleTableSorter);
		createColumn(layout, 1, Messages.InvisibleContextElementsPart_Structure_kind, 100, table, invisibleTableSorter);
		table.setSortColumn(table.getColumn(0));
		table.setSortDirection(SWT.DOWN);
		if (ContextCore.getContextManager().isContextActive()) {
			Collection<Object> allVisible = getAllVisibleElementsInContextPage();
			if (allVisible != null) {
				IInteractionContext context = ContextCore.getContextManager().getActiveContext();
				updateInvisibleSectionInBackground(context, allVisible);
			}
		}

		return invisibleSection;
	}

	private void createColumn(TableColumnLayout layout, final int index, String label, int weight, final Table table,
			final InteractionElementTableSorter invisibleTableSorter) {
		final TableColumn column = new TableColumn(table, SWT.LEFT, index);
		column.setText(label);
		column.setToolTipText(label);
		column.setResizable(true);

		layout.setColumnData(column, new ColumnPixelData(weight, true));

		column.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				invisibleTableSorter.setCriteria(index);
				table.setSortColumn(column);
				if (invisibleTableSorter.isDecending()) {
					table.setSortDirection(SWT.UP);
				} else {
					table.setSortDirection(SWT.DOWN);
				}
				invisibleTable.refresh();
			}

		});
	}

	public void updateInvisibleElementsSection() {
		if (ContextCore.getContextManager().isContextActive()) {
			Collection<Object> allVisible = getAllVisibleElementsInContextPage();
			if (allVisible != null) {
				IInteractionContext context = ContextCore.getContextManager().getActiveContext();
				updateInvisibleSectionInBackground(context, allVisible);
			}
		}
	}

	private void updateInvisibleSectionInBackground(final IInteractionContext context,
			final Collection<Object> allVisible) {

		Job j = new Job(Messages.InvisibleContextElementsPart_Updating_invisible_element_list) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask(Messages.InvisibleContextElementsPart_Computing_invisible_elements,
						IProgressMonitor.UNKNOWN);
				final List<IInteractionElement> allInvisibleElements = getAllInvisibleElements(context, allVisible);
				Display.getDefault().asyncExec(new Runnable() {

					public void run() {
						if (invisibleSection != null && !invisibleSection.isDisposed()) {
							invisibleSection.setText(NLS.bind(Messages.InvisibleContextElementsPart_Invisible_elements,
									allInvisibleElements.size()));
							invisibleSection.layout();
							if (allInvisibleElements.size() == 0) {
								invisibleSection.setExpanded(false);
								invisibleSection.setEnabled(false);
							} else {
								invisibleSection.setEnabled(true);
							}
						}

						if (invisibleTable != null && !invisibleTable.getTable().isDisposed()) {
							invisibleTable.setInput(allInvisibleElements);
						}
					}
				});

				return Status.OK_STATUS;
			};
		};
		j.schedule();

	}

	private List<IInteractionElement> getAllInvisibleElements(IInteractionContext context, Collection<Object> allVisible) {
		List<IInteractionElement> allToRemove = context.getAllElements();

		List<IInteractionElement> allVisibleElements = new ArrayList<IInteractionElement>();
		for (Object visibleObject : allVisible) {
			for (AbstractContextStructureBridge bridge : ContextCorePlugin.getDefault().getStructureBridges().values()) {
//			AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(visibleObject);
				if (bridge != null) {
					String handle = bridge.getHandleIdentifier(visibleObject);
					if (handle != null) {
						IInteractionElement element = context.get(handle);
						if (element != null) {
							allVisibleElements.add(element);
						}
					}
				}
			}
			AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(
					ContextCore.CONTENT_TYPE_RESOURCE);
			if (bridge != null) {
				String handle = bridge.getHandleIdentifier(visibleObject);
				if (handle != null) {
					IInteractionElement element = context.get(handle);
					if (element != null) {
						allVisibleElements.add(element);
					}
				}
			}
		}
		IInteractionElement emptyElement = context.get(""); //$NON-NLS-1$
		if (emptyElement != null) {
			allVisibleElements.add(emptyElement);
		}

		allToRemove.removeAll(allVisibleElements);

		return allToRemove;
	}

	private Collection<Object> getAllVisibleElementsInContextPage() {
		if (commonViewer == null || commonViewer.getTree() == null || commonViewer.getTree().isDisposed()) {
			return null;
		}
		Set<Object> allVisible = new HashSet<Object>();
		SwtUtil.collectItemData(commonViewer.getTree().getItems(), allVisible);
		return allVisible;
	}

}
