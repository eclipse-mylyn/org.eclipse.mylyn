/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.spi.edit.remote;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.reviews.core.spi.remote.AbstractDataLocator;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactoryProvider;
import org.eclipse.mylyn.reviews.edit.ReviewsEditPluginActivator;
import org.eclipse.mylyn.reviews.edit.provider.ReviewsItemProviderAdapterFactory;

/**
 * Supports decoupling of Reviews from remote API as well as job management.
 * 
 * @author Miles Parker
 */
public abstract class AbstractRemoteEditFactoryProvider<ERootObject extends EObject, EChildObject extends EObject>
		extends AbstractRemoteEmfFactoryProvider<ERootObject, EChildObject> {

	private static final String DIRECTORY_METADATA = ".metadata"; //$NON-NLS-1$

	private static final String NAME_DATA_DIR = ".mylyn"; //$NON-NLS-1$

	final EditingDomain editingDomain;

	ERootObject rootObject;

	private final EFactory emfFactory;

	private final EReference parentReference;

	private final EAttribute localKeyAttribute;

	private final EClass childType;

	public AbstractRemoteEditFactoryProvider(final EFactory emfFactory, EReference parentReference,
			final EAttribute localKeyAttribute, EClass childType) {
		this.emfFactory = emfFactory;
		this.parentReference = parentReference;
		this.localKeyAttribute = localKeyAttribute;
		this.childType = childType;
		ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(
				ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

		adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new ReviewsItemProviderAdapterFactory());

		BasicCommandStack commandStack = new BasicCommandStack();
		editingDomain = new AdapterFactoryEditingDomain(adapterFactory, commandStack, new HashMap<Resource, Boolean>());

		Map<Object, Object> loadOptions = editingDomain.getResourceSet().getLoadOptions();
		loadOptions.put(XMLResource.OPTION_DEFER_ATTACHMENT, Boolean.TRUE);
		loadOptions.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
		loadOptions.put(XMLResource.OPTION_USE_DEPRECATED_METHODS, Boolean.TRUE);
		loadOptions.put(XMLResource.OPTION_ENCODING, "UTF-8"); //$NON-NLS-1$
	}

	public EClass getRootClass() {
		return parentReference.getEContainingClass();
	}

	@Override
	public ERootObject open() {
		if (rootObject == null) {
			rootObject = (ERootObject) open(getRootClass(), getRootClass().getName());
			clearChildren();
		}
		return rootObject;
	}

	@Override
	public EChildObject open(Object id) {
		getService().ensureModelThread();
		Object parentList = getRoot().eGet(parentReference);
		if (parentList instanceof List<?>) {
			List<?> members = (List<?>) parentList;
			for (Object object : members) {
				Object localKey = ((EObject) object).eGet(localKeyAttribute);
				if (localKey != null && localKey.equals(id)) {
					return (EChildObject) object;
				}
			}
		}
		EChildObject child = (EChildObject) open(childType, (String) id);
		if (child != null) {
			((List) getRoot().eGet(parentReference)).add(child);
		}
		return child;
	}

	private Resource getResourceImpl(URI uri, boolean loadOnDemand) {
		Resource resource = null;
		String fileString = uri.devicePath();
		IPath filePath = new Path(fileString);
		File file = new File(filePath.toOSString());
		if (!file.exists()) {
			File dir = new File(new Path(fileString).removeFileExtension().removeLastSegments(1).toOSString());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			resource = editingDomain.getResourceSet().createResource(uri);
			save(resource);
		}

		try {
			resource = editingDomain.getResourceSet().getResource(uri, loadOnDemand);
		} catch (Exception e) {
			//If anything else goes wrong, just delete and recreate the file anyway!
			StatusHandler.log(new Status(IStatus.ERROR, ReviewsEditPluginActivator.PLUGIN_ID,
					"Problem with model file. Will be recreated at: " + uri, e));
			file.delete();
			resource = editingDomain.getResourceSet().getResource(uri, loadOnDemand);
		}

		String className = getDataLocator().parseFileType(filePath);
		EClass eClass = null;
		for (EClassifier classifier : emfFactory.getEPackage().getEClassifiers()) {
			if (className.equals(classifier.getName())) {
				eClass = (EClass) classifier;
				break;
			}
		}
		if (eClass == null) {
			throw new RuntimeException("No instances of " + className + " found in "
					+ emfFactory.getEPackage().getEClassifiers());
		}

		if (resource.getContents().size() > 0) {
			Object object = resource.getContents().get(0);
			if (!eClass.isInstance(object)) {
				resource.getContents().clear();
			}
		}
		if (resource.getContents().size() == 0) {
			try {
				EChildObject object = (EChildObject) emfFactory.create(eClass);
				String id = getDataLocator().parseFileName(filePath);
				if (object.eClass().getEAllAttributes().contains(localKeyAttribute)) {
					object.eSet(localKeyAttribute, id);
				}
				resource.getContents().add(object);
				save(resource);
			} catch (AssertionError e) {
				StatusHandler.log(new Status(IStatus.ERROR, ReviewsEditPluginActivator.PLUGIN_ID,
						"Bad provider defintion. Local key attribute must be reference of class child type. Local Key: "
								+ localKeyAttribute.getName() + " Class: " + eClass.getName(), e));
			} catch (ClassCastException e) {
				StatusHandler.log(new Status(IStatus.ERROR, ReviewsEditPluginActivator.PLUGIN_ID,
						"Bad provider definition. Root remote refernce must match child type.", e));
			}
		}

		return resource;
	}

	protected EObject open(EClass eClass, String id) {
		String containerSegment = getContainerSegment();
		URI uri = URI.createFileURI(getDataLocator().getFilePath(containerSegment, eClass.getName(), id,
				getFileExtension(parentReference.getEContainingClass())).toOSString());
		Resource resource = getResourceImpl(uri, true);
		return resource.getContents().get(0);
	}

	public String getContainerSegment() {
		return parentReference.getContainerClass().getName();
	}

	public String getScalablePath(String id) {
		return id;
	}

	public abstract String getFileExtension(EClass eClass);

	public Resource recreateResource(URI fileUri) {
		return editingDomain.getResourceSet().createResource(fileUri);
	}

	@Override
	public void close(EObject child) {
		getService().ensureModelThread();
		if (child == null) {
			return;
		}
		save(child);
		Object parentList = getRoot().eGet(parentReference);
		if (parentList instanceof List<?>) {
			List<?> members = (List<?>) parentList;
			members.remove(child);
		}
		Resource resource = child.eResource();
		if (resource != null) {
			resource.getResourceSet().getResources().remove(resource);
			resource.unload();
		}
	}

	private void clearChildren() {
		//We must allow this to occur outside of model thread for case of workbench shutdown
		if (getRoot() != null) {
			Object parentList = getRoot().eGet(parentReference);
			if (parentList instanceof List<?>) {
				List<?> members = (List<?>) parentList;
				members.clear();
			}
		}
	}

	@Override
	public void close() {
		clearChildren();
		save();
		rootObject = null;
	}

	public void save(EObject object) {
		if (object != null) {
			save(object.eResource());
		}
	}

	public void save() {
		save(getRoot());
	}

	public void save(Resource resource) {
		if (resource == null) {
			return;
		}
		final Map<Object, Object> saveOptions = new HashMap<Object, Object>();
		saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
		if (resource instanceof XMLResource) {
			saveOptions.put(XMLResource.OPTION_ENCODING, "UTF-8"); //$NON-NLS-1$
		}
		try {
			resource.save(saveOptions);
		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ReviewsEditPluginActivator.PLUGIN_ID, "Couldn't save model.", e));
		}
	}

	/**
	 * WARNING: Recursively deletes directory specified by {@link AbstractDataLocator#getSystemPath()}. Ensure that that
	 * directory isn't used by any other resources!
	 */
	public void deleteCache() {
		close();
		IPath systemPath = getDataLocator().getSystemPath();
		File file = new File(systemPath.toOSString());
		if (file.exists()) {
			try {
				FileUtils.deleteDirectory(file);
			} catch (IOException e) {
				StatusHandler.log(new Status(IStatus.ERROR, ReviewsEditPluginActivator.PLUGIN_ID,
						"Problem when deleting cache.", e));
			}
		}
	}

	@Override
	public void modelExec(final Runnable runnable, boolean block) {
		super.modelExec(new Runnable() { //Run in UI thread
					public void run() {
						editingDomain.getCommandStack().execute(new AbstractCommand() {

							public void redo() {
								// noop
							}

							public void execute() {
								runnable.run();
							}

							@Override
							protected boolean prepare() {
								return true;
							}

							@Override
							public boolean canUndo() {
								return false;
							}
						});
					}
				}, block);
	}

	public ERootObject getRoot() {
		return rootObject;
	}

	public EditingDomain getEditingDomain() {
		return editingDomain;
	}
}
