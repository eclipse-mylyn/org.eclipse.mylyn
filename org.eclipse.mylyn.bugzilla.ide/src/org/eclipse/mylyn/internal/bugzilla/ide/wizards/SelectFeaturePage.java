/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ide.wizards;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.branding.IBundleGroupConstants;

/**
 * @author Steffen Pingel
 */
public class SelectFeaturePage extends WizardPage {

	private static final int TABLE_HEIGHT = IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH;

	private IBundleGroup selectedBundleGroup;

	private final IBundleGroup[] bundleGroups;

	private ImageRegistry imageRegistry;

	public SelectFeaturePage(String pageName, IBundleGroup[] bundleGroups) {
		super(pageName);
		this.bundleGroups = bundleGroups;
		setTitle("Select a feature");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, true);
		container.setLayout(layout);

		imageRegistry = new ImageRegistry(getShell().getDisplay());

		for (IBundleGroup bundleGroup : bundleGroups) {
			String imageUrl = bundleGroup.getProperty(IBundleGroupConstants.FEATURE_IMAGE);
			if (imageUrl != null) {
				try {
					ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(new URL(imageUrl));
					imageRegistry.put(bundleGroup.getIdentifier(), imageDescriptor);
				} catch (MalformedURLException e) {
					// ignore
				}
			}
		}

		TableViewer viewer = new TableViewer(container, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).hint(SWT.DEFAULT, TABLE_HEIGHT).applyTo(viewer.getControl());
		viewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				return bundleGroups;
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		viewer.setLabelProvider(new LabelProvider() {

			@Override
			public Image getImage(Object element) {
				if (element instanceof IBundleGroup) {
					IBundleGroup bundleGroup = (IBundleGroup) element;
					return imageRegistry.get(bundleGroup.getIdentifier());
				}
				return null;
			}
			
			@Override
			public String getText(Object element) {
				if (element instanceof IBundleGroup) {
					IBundleGroup bundleGroup = (IBundleGroup) element;
					return bundleGroup.getName();
				}
				return "";
			}

		});
		viewer.setInput(TasksUiPlugin.getRepositoryManager().getRepositoryConnectors());

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.getFirstElement() instanceof IBundleGroup) {
					selectedBundleGroup = (IBundleGroup) selection.getFirstElement();
					setMessage(selectedBundleGroup.getDescription());
					setPageComplete(true);
				} else {
					setMessage(null);
					setPageComplete(false);
				}
			}
		});

		viewer.addOpenListener(new IOpenListener() {

			public void open(OpenEvent event) {
				if (getWizard().performFinish()) {
					((WizardDialog) getContainer()).close();
				}
			}
		});

		viewer.getTable().showSelection();
		viewer.getTable().setFocus();

		viewer.setSorter(new ViewerSorter());

		setControl(container);
	}

	@Override
	public void dispose() {
		if (imageRegistry != null) {
			imageRegistry.dispose();
		}
		super.dispose();
	}

	public IBundleGroup getSelectedBundleGroup() {
		return selectedBundleGroup;
	}

}