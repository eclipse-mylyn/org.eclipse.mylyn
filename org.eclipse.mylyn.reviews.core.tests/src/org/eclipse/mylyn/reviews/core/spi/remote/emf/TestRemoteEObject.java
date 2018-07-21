/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
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
