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

package org.eclipse.mylyn.reviews.edit.remote;

import java.util.HashMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.spi.remote.AbstractRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.edit.ReviewsEditPlugin;
import org.eclipse.mylyn.reviews.edit.provider.ReviewsItemProviderAdapterFactory;

/**
 * Supports decoupling of Reviews from remote API as well as job management.
 * 
 * @author Miles Parker
 */
public abstract class AbstractRemoteEditFactoryProvider<ERootObject extends EObject> extends
		AbstractRemoteFactoryProvider {

	private final EditingDomain editingDomain;

	ERootObject rootObject;

	public AbstractRemoteEditFactoryProvider(EFactory emfFactory, EClass rootClass) {
		ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(
				ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

		adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new ReviewsItemProviderAdapterFactory());

		BasicCommandStack commandStack = new BasicCommandStack();
		editingDomain = new AdapterFactoryEditingDomain(adapterFactory, commandStack, new HashMap<Resource, Boolean>());

		Resource resource = new ResourceImpl();

		if (resource.getContents().size() > 0 && resource.getContents().get(0) instanceof IRepository) {
			try {
				rootObject = (ERootObject) resource.getContents().get(0);
			} catch (ClassCastException e) {
				StatusHandler.log(new Status(IStatus.ERROR, ReviewsEditPlugin.PLUGIN_ID,
						"Problem creating editing domain. Unexpected root model content.", e));
			}

		} else {
			try {
				rootObject = (ERootObject) emfFactory.create(rootClass);
				resource.getContents().add(rootObject);
			} catch (ClassCastException e) {
				StatusHandler.log(new Status(IStatus.ERROR, ReviewsEditPlugin.PLUGIN_ID,
						"Problem creating editing domain. Root remote class must match remote editing domain type.", e));
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
}
