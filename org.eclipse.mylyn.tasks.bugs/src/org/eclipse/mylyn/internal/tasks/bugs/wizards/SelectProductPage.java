/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs.wizards;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.core.runtime.IBundleGroupProvider;
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.bugs.AttributeTaskMapper;
import org.eclipse.mylyn.internal.tasks.bugs.IRepositoryConstants;
import org.eclipse.mylyn.internal.tasks.bugs.PluginRepositoryMappingManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.branding.IBundleGroupConstants;

/**
 * @author Steffen Pingel
 */
public class SelectProductPage extends WizardPage {

	private static final int TABLE_HEIGHT = 200;

	private static final String DEFAULT_CATEGORY = "Other";

	private ImageRegistry imageRegistry;

	private final PluginRepositoryMappingManager manager;

	private FeatureGroup selectedFeatureGroup;

	public SelectProductPage(String pageName, PluginRepositoryMappingManager manager) {
		super(pageName);
		this.manager = manager;
		setTitle("Select a product");
	}

	@Override
	public boolean canFlipToNextPage() {
		return selectedFeatureGroup != null && selectedFeatureGroup.requiresSelection();
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		composite.setLayout(layout);

		imageRegistry = new ImageRegistry(getShell().getDisplay());

		final Map<String, FeatureGroup> containerByName = getProducts();

		TableViewer viewer = new TableViewer(composite, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.FILL)
				.grab(true, true)
				.hint(SWT.DEFAULT, TABLE_HEIGHT)
				.applyTo(viewer.getControl());
		viewer.setContentProvider(new IStructuredContentProvider() {

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				return containerByName.values().toArray();
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		viewer.setLabelProvider(new LabelProvider() {

			@Override
			public Image getImage(Object element) {
				if (element instanceof FeatureGroup) {
					FeatureGroup product = (FeatureGroup) element;
					return imageRegistry.get(product.getName());
				}
				return null;
			}

			@Override
			public String getText(Object element) {
				if (element instanceof FeatureGroup) {
					FeatureGroup product = (FeatureGroup) element;
					if (product.getTitle() != null) {
						return product.getName() + "\n  " + product.getTitle();
					}
					return product.getName();
				}
				return "";
			}

		});
		viewer.setInput(TasksUiPlugin.getRepositoryManager().getRepositoryConnectors());

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.getFirstElement() instanceof FeatureGroup) {
					selectedFeatureGroup = (FeatureGroup) selection.getFirstElement();
					if (selectedFeatureGroup.requiresSelection()) {
						setMessage(null);
					} else {
						setMessage(selectedFeatureGroup.getDescription());
					}
					setPageComplete(true);
				} else {
					setMessage(null);
					setPageComplete(false);
				}
			}
		});

		viewer.addOpenListener(new IOpenListener() {

			public void open(OpenEvent event) {
				if (canFlipToNextPage()) {
					getContainer().showPage(getNextPage());
				} else if (isPageComplete()) {
					if (getWizard().performFinish()) {
						((WizardDialog) getContainer()).close();
					}
				}
			}
		});

		viewer.getTable().showSelection();
		viewer.getTable().setFocus();

		viewer.setSorter(new ViewerSorter() {
			@Override
			public int compare(Viewer viewer, Object o1, Object o2) {
				FeatureGroup g1 = (FeatureGroup) o1;
				FeatureGroup g2 = (FeatureGroup) o2;
				int i = g1.getCategory().compareTo(g2.getCategory());
				if (i != 0) {
					return i;
				}
				return g1.getName().compareTo(g2.getName());
			}
		});

		setControl(composite);
	}

	@Override
	public void dispose() {
		if (imageRegistry != null) {
			imageRegistry.dispose();
		}
		super.dispose();
	}

	@Override
	public IWizardPage getNextPage() {
		if (canFlipToNextPage()) {
			SelectFeaturePage page = new SelectFeaturePage("selectBundle", getSelectedBundleGroups());
			page.setWizard(getWizard());
			return page;
		}
		return null;
	}

	private Map<String, FeatureGroup> getProducts() {
		final Map<String, FeatureGroup> containerByName = new HashMap<String, FeatureGroup>();
		IBundleGroupProvider[] providers = Platform.getBundleGroupProviders();
		if (providers != null) {
			for (IBundleGroupProvider provider : providers) {
				for (IBundleGroup bundleGroup : provider.getBundleGroups()) {
					addProduct(containerByName, bundleGroup);
				}
			}
		}
		return containerByName;
	}

	private void addProduct(Map<String, FeatureGroup> featureGroupByName, IBundleGroup bundleGroup) {
		Map<String, String> attributes = manager.getAllAttributes(bundleGroup.getIdentifier());

		AttributeTaskMapper mapper = new AttributeTaskMapper(attributes);
		if (!mapper.isMappingComplete()) {
			return;
		}

		String imageUrl = bundleGroup.getProperty(IBundleGroupConstants.FEATURE_IMAGE);
		if (imageUrl == null) {
			return;
		}

		String productDescription = attributes.get(IRepositoryConstants.PRODUCT_DESCRIPTION);
		if (productDescription == null) {
			productDescription = bundleGroup.getDescription();
		}

		String productName = attributes.get(IRepositoryConstants.PRODUCT_NAME);
		if (productName == null) {
			productName = bundleGroup.getName();
		}

		String productTitle = attributes.get(IRepositoryConstants.PRODUCT_TITLE);

		String productCategory = attributes.get(IRepositoryConstants.PRODUCT_CATEGORY);
		if (productCategory == null) {
			productCategory = DEFAULT_CATEGORY;
		}

		String branding = attributes.get(IRepositoryConstants.BRANDING);
		if (branding == null) {
			branding = bundleGroup.getName();
		}

		try {
			ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(new URL(imageUrl));
			imageRegistry.put(productName, imageDescriptor);
		} catch (MalformedURLException e) {
			// ignore bundles that do not have a feature image 
			return;
		}

		FeatureGroup container = featureGroupByName.get(productName);
		if (container == null) {
			container = new FeatureGroup(productName, productDescription, productTitle, productCategory);
			container.addBundleGroup(bundleGroup, branding);
			featureGroupByName.put(productName, container);
		} else {
			container.addBundleGroup(bundleGroup, branding);
		}
	}

	public IBundleGroup[] getSelectedBundleGroups() {
		if (selectedFeatureGroup != null) {
			return selectedFeatureGroup.getBundleGroups().toArray(new IBundleGroup[0]);
		}
		return null;
	}

}