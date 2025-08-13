/*******************************************************************************
 * Copyright (c) 2010, 2024 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Itema AS - bug 329897 select event type on open if available
 *     Itema AS - bug 330064 notification filtering and model persistence
 *     Itema AS - bug 331424 handle default event-sink action associations
 *     ArSysOp - ongoing support
 *******************************************************************************/
package org.eclipse.mylyn.internal.commons.notifications.ui;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.SubstringPatternFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.FilteredTree;

/**
 * @author Steffen Pingel
 * @author Torkild Ulvøy Resheim
 */
public class NotificationsPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

	/**
	 * We need this in order to make sure that the correct element is selected in the {@link TreeViewer} when the selection is set.
	 *
	 * @author Torkild Ulvøy Resheim
	 */
	public class NotificationEventComparer implements IElementComparer {

		@Override
		public boolean equals(Object a, Object b) {
			if (a instanceof NotificationEvent && b instanceof NotificationEvent) {
				String idA = ((NotificationEvent) a).getId();
				String idB = ((NotificationEvent) b).getId();
				return idA.equals(idB);
			}
			return a.equals(b);
		}

		@Override
		public int hashCode(Object element) {
			return element.hashCode();
		}

	}

	private static final Object[] EMPTY = {};

	private static final class EventContentProvider implements ITreeContentProvider {

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// ignore
		}

		@Override
		public void dispose() {
			// ignore
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof NotificationCategory) {
				return ((NotificationCategory) element).getEvents().size() > 0;
			}
			return false;
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof NotificationEvent) {
				return ((NotificationEvent) element).getCategory();
			}
			return null;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Object[]) {
				return (Object[]) inputElement;
			} else {
				return EMPTY;
			}
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof NotificationCategory) {
				return ((NotificationCategory) parentElement).getEvents().toArray();
			}
			return EMPTY;
		}

	}

	private static final class NotifiersContentProvider implements IStructuredContentProvider {

		private NotificationHandler handler;

		@Override
		public void dispose() {
			// ignore
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput instanceof NotificationHandler) {
				handler = (NotificationHandler) newInput;
			} else {
				handler = null;
			}
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (handler != null) {
				return handler.getActions().toArray();
			} else {
				return EMPTY;
			}
		}

	}

	public final class EventLabelProvider extends LabelProvider {

		@Override
		public String getText(Object element) {
			if (element instanceof NotificationElement item) {
				return item.getLabel();
			}
			return super.getText(element);
		}

		@Override
		public Image getImage(Object element) {
			if (element instanceof NotificationEvent item) {
				if (model.isSelected(item)) {
					return CommonImages.getImage(CommonImages.CHECKED);
				} else {
					return null;
				}
			}
			if (element instanceof NotificationElement item) {
				ImageDescriptor imageDescriptor = item.getImageDescriptor();
				if (imageDescriptor != null) {
					return CommonImages.getImage(imageDescriptor);
				}
			}
			return super.getImage(element);
		}
	}

	private TreeViewer eventsViewer;

	private CheckboxTableViewer notifiersViewer;

	private Button enableNotificationsButton;

	private NotificationModel model;

	private Text descriptionText;

	public NotificationsPreferencesPage() {
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return NotificationsPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected Control createContents(Composite parent) {
		model = NotificationsPlugin.getDefault().createModelWorkingCopy();

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		enableNotificationsButton = new Button(composite, SWT.CHECK);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(enableNotificationsButton);
		enableNotificationsButton.setText(Messages.NotificationsPreferencesPage_Enable_Notifications_Text);
		enableNotificationsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateEnablement();
			}
		});

		Label label = new Label(composite, SWT.NONE);
		label.setText(" "); //$NON-NLS-1$
		GridDataFactory.fillDefaults().span(2, 1).applyTo(label);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.NotificationsPreferencesPage_Events_Label);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.NotificationsPreferencesPage_Notifiers_Label);
		// Create the tree showing all the various notification types
		FilteredTree tree = new FilteredTree(composite, SWT.BORDER, new SubstringPatternFilter(), true, true);
		eventsViewer = tree.getViewer();
		GridDataFactory.fillDefaults().span(1, 2).grab(false, true).applyTo(tree);
		eventsViewer.setComparer(new NotificationEventComparer());
		eventsViewer.setContentProvider(new EventContentProvider());
		eventsViewer.setLabelProvider(new EventLabelProvider());
		eventsViewer.setInput(model.getCategories().toArray());
		eventsViewer.expandAll();
		eventsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object input = getDetailsInput((IStructuredSelection) event.getSelection());
				notifiersViewer.setInput(input);

				Object item = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (item instanceof NotificationEvent) {
					descriptionText.setText(((NotificationEvent) item).getDescription());
					notifiersViewer.getControl().setEnabled(true);
				} else {
					descriptionText.setText(" "); //$NON-NLS-1$
					notifiersViewer.getControl().setEnabled(false);
				}
			}

			private Object getDetailsInput(IStructuredSelection selection) {
				Object item = selection.getFirstElement();
				if (item instanceof NotificationEvent) {
					return model.getOrCreateNotificationHandler((NotificationEvent) item);
				}
				return null;
			}
		});
		// Create the table listing all notification sinks available for the selected event type.
		notifiersViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(notifiersViewer.getControl());
		notifiersViewer.setContentProvider(new NotifiersContentProvider());
		notifiersViewer.setLabelProvider(new EventLabelProvider());
		notifiersViewer.addCheckStateListener(event -> {
			NotificationAction action = (NotificationAction) event.getElement();
			action.setSelected(event.getChecked());
			model.setDirty(true);
			eventsViewer.refresh();
		});
		notifiersViewer.setCheckStateProvider(new ICheckStateProvider() {
			@Override
			public boolean isChecked(Object element) {
				return ((NotificationAction) element).isSelected();
			}

			@Override
			public boolean isGrayed(Object element) {
				return false;
			}
		});
		notifiersViewer.addSelectionChangedListener(event -> {
			Object item = ((IStructuredSelection) event.getSelection()).getFirstElement();
			if (item instanceof NotificationAction) {
				// TODO show configuration pane
			}
		});

		Group group = new Group(composite, SWT.NONE);
		GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).grab(true, true).applyTo(group);
		group.setText(Messages.NotificationsPreferencesPage_Descriptions_Label);
		FillLayout layout = new FillLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		group.setLayout(layout);

		descriptionText = new Text(group, SWT.WRAP);
		descriptionText.setBackground(group.getBackground());

//		Button testButton = new Button(composite, SWT.NONE);
//		testButton.setText("Test");
//		testButton.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				ISelection selection = eventsViewer.getSelection();
//				if (selection instanceof IStructuredSelection) {
//					Object object = ((IStructuredSelection) selection).getFirstElement();
//					if (object instanceof NotificationEvent) {
//						final NotificationEvent event = (NotificationEvent) object;
//						getControl().getDisplay().asyncExec(new Runnable() {
//							public void run() {
//								Notifications.getService().notify(
//										Collections.singletonList(new TestNotification(event)));
//							}
//						});
//					}
//				}
//			}
//
//		});

		reset();
		Dialog.applyDialogFont(composite);
		return composite;
	}

	@Override
	public void applyData(Object data) {
		// We may or may not have a NotificationEvent supplied when this
		// preference dialog is opened. If we do have this data we want to
		// highlight the appropriate instance.
		if (data instanceof String selectedEventId && model != null) {
			Collection<NotificationCategory> items = model.getCategories();
			NotificationEvent selectedEvent = null;
			for (NotificationCategory notificationCategory : items) {
				List<NotificationEvent> event = notificationCategory.getEvents();
				for (NotificationEvent notificationEvent : event) {
					if (notificationEvent.getId().equals(selectedEventId)) {
						selectedEvent = notificationEvent;
						break;
					}
				}
			}
			if (selectedEvent != null) {
				eventsViewer.setSelection(new StructuredSelection(selectedEvent), true);
			}
		}
	}

	private void updateEnablement() {
		boolean enabled = enableNotificationsButton.getSelection();
		eventsViewer.getControl().setEnabled(enabled);
		notifiersViewer.getControl().setEnabled(enabled);// FIXME enabled && notifiersViewer.getInput() != null);
		descriptionText.setEnabled(enabled);
		if (!enabled) {
			eventsViewer.setSelection(StructuredSelection.EMPTY);
		}
	}

	@Override
	public void init(IWorkbench workbench) {
		// ignore
	}

	public void reset() {
		enableNotificationsButton
		.setSelection(getPreferenceStore().getBoolean(NotificationsPlugin.PREF_NOTICATIONS_ENABLED));
		updateEnablement();
	}

	@Override
	public boolean performOk() {
		if (model != null) {
			getPreferenceStore().setValue(NotificationsPlugin.PREF_NOTICATIONS_ENABLED,
					enableNotificationsButton.getSelection());
			if (model.isDirty()) {
				NotificationsPlugin.getDefault().saveWorkingCopy(model);
				model.setDirty(false);
			}
		}
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		enableNotificationsButton
		.setSelection(getPreferenceStore().getDefaultBoolean(NotificationsPlugin.PREF_NOTICATIONS_ENABLED));
		for (NotificationCategory category : model.getCategories()) {
			for (NotificationEvent event : category.getEvents()) {
				NotificationHandler handler = model.getOrCreateNotificationHandler(event);
				for (NotificationAction action : handler.getActions()) {
					action.setSelected(event.defaultHandledBySink(action.getSinkDescriptor().getId()));
				}
			}
		}
		// assume that the model has become dirty
		model.setDirty(true);
		// refresh UI
		eventsViewer.refresh();
		notifiersViewer.refresh();
		updateEnablement();
	}

}
