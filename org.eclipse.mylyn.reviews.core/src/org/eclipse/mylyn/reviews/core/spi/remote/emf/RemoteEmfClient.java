/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.spi.remote.emf;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

public abstract class RemoteEmfClient<EParentObjectType extends EObject, EObjectType, LocalKeyType, ObjectCurrentType>
		extends RemoteEmfObserver<EParentObjectType, EObjectType, LocalKeyType, ObjectCurrentType> {

	private ObjectCurrentType lastValue;

	private boolean created;

	private boolean built;

	private boolean rebuilt;

	private boolean forceUpdate;

	public RemoteEmfClient(
			RemoteEmfConsumer<EParentObjectType, EObjectType, LocalKeyType, ?, ?, ObjectCurrentType> consumer) {
		super(consumer);
	}

	public RemoteEmfClient() {
	}

	public void checkUpdate(boolean modified) {
		if (!created && isClientReady() && isModelReady()) {
			create();
			created = true;
			modified = true;
			rebuilt = true; //That is, we don't need to be rebuilt
		}
		if (isClientReady() && isModelReady()) {
			if (!rebuilt) {
				rebuild();
				rebuilt = true;
			}
			if (!isModelCurrent() || modified || forceUpdate) {
				update();
			}
			forceUpdate = false;
			built = true;
		}
	}

	public final synchronized void requestUpdate(boolean force, boolean rebuild) {
		this.rebuilt = !rebuild;
		this.forceUpdate = force || !built;
		built = true;
		getConsumer().retrieve(force);
	}

	public void populate() {
		if (isModelReady()) {
			checkUpdate(false);
		} else {
			requestUpdate(false, false);
		}
	}

	public final synchronized void requestUpdate(boolean force) {
		requestUpdate(force, false);
	}

	public final synchronized void requestUpdate() {
		requestUpdate(false);
	}

	@Override
	public final void updated(EParentObjectType parentObject, EObjectType modelObject, boolean modified) {
		checkUpdate(modified);
	}

	@Override
	public final void updating(EParentObjectType parent, EObjectType object) {
		updating();
	}

	protected void updating() {
	}

	protected void update() {
		lastValue = getConsumer().getFactory().getModelCurrentValue(getConsumer().getParentObject(),
				getConsumer().getModelObject());
	}

	protected void create() {
	}

	protected void rebuild() {
	}

	public boolean isModelReady() {
		return getConsumer().getModelObject() != null
				&& !getConsumer().getFactory().isCreateModelNeeded(getConsumer().getParentObject(),
						getConsumer().getModelObject())
				&& (!(getConsumer().getModelObject() instanceof Collection) || !((Collection) getConsumer().getModelObject()).isEmpty());
	}

	private boolean isModelCurrent() {
		return getConsumer().getModelObject() != null
				&& lastValue != null
				&& lastValue.equals(getConsumer().getFactory().getModelCurrentValue(getConsumer().getParentObject(),
						getConsumer().getModelObject()));
	}

	protected abstract boolean isClientReady();
}
