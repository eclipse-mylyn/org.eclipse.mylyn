/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.commons.ui.notifications;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.SubstringPatternFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.FilteredTree;

/**
 * @author Steffen Pingel
 */
public class NotificationsPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final Object[] EMPTY = new Object[0];

	private final class EventContentProvider implements ITreeContentProvider {

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// ignore
		}

		public void dispose() {
			// ignore
		}

		public boolean hasChildren(Object element) {
			if (element instanceof NotificationCategory) {
				return ((NotificationCategory) element).getEvents().size() > 0;
			}
			return false;
		}

		public Object getParent(Object element) {
			if (element instanceof NotificationEvent) {
				return ((NotificationEvent) element).getCategory();
			}
			return null;
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Object[]) {
				return (Object[]) inputElement;
			} else {
				return EMPTY;
			}
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof NotificationCategory) {
				return ((NotificationCategory) parentElement).getEvents().toArray();
			}
			return EMPTY;
		}

	}

	public final class NotificationLabelProvider extends LabelProvider {

		@Override
		public String getText(Object element) {
			if (element instanceof NotificationElement) {
				NotificationElement item = (NotificationElement) element;
				return item.getLabel();
			}
			return super.getText(element);
		}

		@Override
		public Image getImage(Object element) {
			if (element instanceof NotificationElement) {
				NotificationElement item = (NotificationElement) element;
				ImageDescriptor imageDescriptor = item.getImageDescriptor();
				if (imageDescriptor != null) {
					return CommonImages.getImage(imageDescriptor);
				}
			}
			return super.getImage(element);
		}
	}

	private TreeViewer eventViewer;

	private Button enableNotificationsButton;

	public NotificationsPreferencesPage() {
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return NotificationsPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		enableNotificationsButton = new Button(composite, SWT.CHECK);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(enableNotificationsButton);
		enableNotificationsButton.setText("&Enable notifications");
		enableNotificationsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateEnablement();
			}
		});

		FilteredTree tree = new FilteredTree(composite, SWT.BORDER, new SubstringPatternFilter(), true);
		eventViewer = tree.getViewer();
		GridDataFactory.fillDefaults().grab(false, true).applyTo(tree);
		eventViewer.setContentProvider(new EventContentProvider());
		eventViewer.setLabelProvider(new NotificationLabelProvider());
		eventViewer.setInput(NotificationsExtensionReader.getCategories().toArray());
		eventViewer.expandAll();

		reset();
		Dialog.applyDialogFont(composite);
		return composite;
	}

	private void updateEnablement() {
		eventViewer.getControl().setEnabled(enableNotificationsButton.getSelection());
	}

	public void init(IWorkbench workbench) {
		// ignore
	}

	public void reset() {
		enableNotificationsButton.setSelection(getPreferenceStore().getBoolean(
				NotificationsPlugin.PREF_NOTICATIONS_ENABLED));
		updateEnablement();
	}

	@Override
	public boolean performOk() {
		getPreferenceStore().setValue(NotificationsPlugin.PREF_NOTICATIONS_ENABLED,
				enableNotificationsButton.getSelection());
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		enableNotificationsButton.setSelection(getPreferenceStore().getDefaultBoolean(
				NotificationsPlugin.PREF_NOTICATIONS_ENABLED));
		updateEnablement();
	}

}
