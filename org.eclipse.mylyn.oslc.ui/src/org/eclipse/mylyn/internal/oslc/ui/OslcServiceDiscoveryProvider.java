/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *      Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.oslc.ui;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.oslc.core.IOslcConnector;
import org.eclipse.mylyn.internal.oslc.core.IOslcCoreConstants;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceDescriptor;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceProvider;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceProviderCatalog;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.progress.DeferredTreeContentManager;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;

/**
 * @author Robert Elves
 */
public class OslcServiceDiscoveryProvider implements ITreeContentProvider {

	private final IOslcConnector connector;

	private final TaskRepository repository;

	private OslcServiceProvider selectedPovider;

	private DeferredTreeContentManager manager;

	OslcServiceDiscoveryProvider(IOslcConnector connector, TaskRepository repository, String base) {
		this.connector = connector;
		this.repository = repository;
	}

	public Object[] getChildren(Object parentElement) {
		return manager.getChildren(parentElement);
	}

	public Object getParent(Object element) {
		return null;
	}

	public OslcServiceProvider getOSLServiceProvider() {
		return selectedPovider;
	}

	public boolean hasChildren(Object element) {
		return manager.mayHaveChildren(element);
	}

	@SuppressWarnings("unchecked")
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof List<?>) {
			List<OslcServiceProvider> rootProviders = (List<OslcServiceProvider>) inputElement;
			Object[] result = new Object[rootProviders.size()];
			for (int x = 0; x < rootProviders.size(); x++) {
				result[x] = new ServiceProviderCatalogWrapper(rootProviders.get(x));
			}
			return result;
		} else {
			Object[] result = { inputElement };
			return result;
		}
	}

	public void dispose() {
		// ignore
	}

	@SuppressWarnings("deprecation")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (viewer instanceof AbstractTreeViewer) {
			manager = new DeferredTreeContentManager(null, (AbstractTreeViewer) viewer);
		}
	}

	protected class ServiceProviderCatalogWrapper implements IDeferredWorkbenchAdapter {
		private final Object element;

		public ServiceProviderCatalogWrapper(Object catalog) {
			this.element = catalog;
		}

		public void fetchDeferredChildren(Object object, IElementCollector collector, IProgressMonitor monitor) {
			try {
				monitor.beginTask("Loading...", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
				ServiceProviderCatalogWrapper wrapper = (ServiceProviderCatalogWrapper) object;
				Object parentElement = wrapper.getServiceObject();
				if (parentElement instanceof OslcServiceProviderCatalog) {
					OslcServiceProviderCatalog remoteFolder = (OslcServiceProviderCatalog) parentElement;
					List<OslcServiceProvider> providers = connector.getAvailableServices(repository,
							remoteFolder.getUrl(), monitor);
					for (OslcServiceProvider oslcServiceProvider : providers) {
						collector.add(new ServiceProviderCatalogWrapper(oslcServiceProvider), monitor);
					}
				} else if (parentElement instanceof OslcServiceProvider) {
					selectedPovider = (OslcServiceProvider) parentElement;
					OslcServiceDescriptor serviceDescriptor = connector.getServiceDescriptor(repository,
							selectedPovider, monitor);
					collector.add(new ServiceProviderCatalogWrapper(serviceDescriptor), monitor);
//					for (OslcCreationDialogDescriptor oslcRecordType : serviceDescriptor.getCreationDialogs()) {
//						collector.add(new ServiceProviderCatalogWrapper(oslcRecordType), monitor);
//					}
				}
			} catch (CoreException e) {
				StatusHandler.log(new Status(IStatus.ERROR, IOslcCoreConstants.ID_PLUGIN,
						"Error occurred during service discovery", e)); //$NON-NLS-1$
			} finally {
				monitor.done();
			}

		}

		public ISchedulingRule getRule(Object object) {
			return null;
		}

		public boolean isContainer() {
			return (element instanceof OslcServiceProviderCatalog || element instanceof OslcServiceProvider);
		}

		public Object[] getChildren(Object o) {
			return null;
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return null;
		}

		public String getLabel(Object o) {
			return null;
		}

		public Object getParent(Object o) {
			return null;
		}

		public Object getServiceObject() {
			return element;
		}

	}

}
