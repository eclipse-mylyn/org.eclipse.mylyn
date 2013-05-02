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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

class TestRemoteFactory extends AbstractRemoteEmfFactory<EPackage, EClass, String, TestRemoteObject, String, Integer> {

	static TestRemoteObject remote1 = new TestRemoteObject("Remote Object 1");

	static TestRemoteObject remote2 = new TestRemoteObject("Remote Object 2");

	static Map<String, TestRemoteObject> remoteForKey = new HashMap<String, TestRemoteObject>();

	Integer currentVal;

	{
		remoteForKey.put("remoteKeyFor Object 1", remote1);
		remoteForKey.put("remoteKeyFor Object 2", remote2);
	}

	public TestRemoteFactory() {
		super(new TestRemoteFactoryProvider(), EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
				EcorePackage.Literals.ECLASSIFIER__INSTANCE_CLASS_NAME);
	}

	@Override
	public TestRemoteObject pull(EPackage parent, String remoteKey, IProgressMonitor monitor) throws CoreException {
		return remoteForKey.get(remoteKey);
	}

	@Override
	protected EClass createModel(EPackage parent, TestRemoteObject remoteObject) {
		EClass clazz = EcoreFactory.eINSTANCE.createEClass();
		clazz.setName(remoteObject.name.replaceAll("Remote", "Local"));
		return clazz;
	}

	@Override
	public boolean updateModel(EPackage parent, EClass object, TestRemoteObject remoteObject) {
		if (remoteObject != null) {
			object.setInstanceTypeName(remoteObject.data);
		}
		return true;
	}

	@Override
	public String getRemoteKey(TestRemoteObject remoteObject) {
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

	@Override
	public Integer getModelCurrentValue(EPackage parentObject, EClass object) {
		return currentVal;
	}
}