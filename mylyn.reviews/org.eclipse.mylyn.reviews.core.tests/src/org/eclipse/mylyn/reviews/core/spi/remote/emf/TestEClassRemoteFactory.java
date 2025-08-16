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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.spi.remote.emf;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

@SuppressWarnings("nls")
class TestEClassRemoteFactory
extends AbstractRemoteEmfFactory<EPackage, EClass, String, TestRemoteEClass, String, Integer> {

	static TestRemoteEClass remote1 = new TestRemoteEClass("Remote Object 1");

	static TestRemoteEClass remote2 = new TestRemoteEClass("Remote Object 2");

	static Map<String, TestRemoteEClass> remoteForKey = new HashMap<>();

	{
		remoteForKey.put("remoteKeyFor Object 1", remote1);
		remoteForKey.put("remoteKeyFor Object 2", remote2);
	}

	public TestEClassRemoteFactory() {
		super(new TestRemoteFactoryProvider(), EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
				EcorePackage.Literals.ECLASSIFIER__INSTANCE_CLASS_NAME);
	}

	@Override
	public TestRemoteEClass pull(EPackage parent, String remoteKey, IProgressMonitor monitor) throws CoreException {
		return remoteForKey.get(remoteKey);
	}

	@Override
	protected EClass createModel(EPackage parent, TestRemoteEClass remoteObject) {
		EClass clazz = EcoreFactory.eINSTANCE.createEClass();
		clazz.setName(remoteObject.name.replace("Remote", "Local"));
		return clazz;
	}

	@Override
	public boolean updateModel(EPackage parent, EClass object, TestRemoteEClass remoteObject) {
		if (remoteObject != null) {
			object.setInstanceTypeName(remoteObject.data);
		}
		return true;
	}

	@Override
	public String getRemoteKey(TestRemoteEClass remoteObject) {
		if (remoteObject == remote1) {
			return "remoteKeyFor Object 1";
		} else if (remoteObject == remote2) {
			return "remoteKeyFor Object 2";
		}
		throw new RuntimeException();
	}

	@Override
	public String getLocalKeyForRemoteKey(String remoteKey) {
		return remoteKey.replace("remote", "local");
	}

}