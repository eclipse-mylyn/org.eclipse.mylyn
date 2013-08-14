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

abstract class TestRemoteEObject {
	String name;

	String data;

	TestRemoteEObject(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}

class TestRemoteEPackage extends TestRemoteEObject {

	TestRemoteEPackage(String name) {
		super(name);
	}
}

class TestRemoteEClass extends TestRemoteEObject {

	TestRemoteEClass(String name) {
		super(name);
	}
}
