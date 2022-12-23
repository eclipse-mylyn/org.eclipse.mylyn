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

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

class TestCollectionObjectRemoteFactory
		extends AbstractRemoteEmfFactory<EPackage, List<EClassifier>, String, TestRemoteEClass, String, Integer> {

	public TestCollectionObjectRemoteFactory() {
		super(new TestRemoteFactoryProvider(), EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
				EcorePackage.Literals.ENAMED_ELEMENT__NAME);
	}

	@Override
	public TestRemoteEClass pull(EPackage parent, String remoteKey, IProgressMonitor monitor) throws CoreException {
		return TestEClassRemoteFactory.remoteForKey.get(remoteKey);
	}

	@Override
	protected List<EClassifier> createModel(EPackage parent, TestRemoteEClass remoteObject) {
		List<EClassifier> classifiers = parent.getEClassifiers();
		EClass class1 = EcoreFactory.eINSTANCE.createEClass();
		class1.setName("Many " + remoteObject.getName() + "_1");
		classifiers.add(class1);
		EClass class2 = EcoreFactory.eINSTANCE.createEClass();
		class2.setName("Many " + remoteObject.getName() + "_2");
		classifiers.add(class2);
		return classifiers;
	}

	@Override
	public boolean updateModel(EPackage parent, List<EClassifier> classifiers, TestRemoteEClass remoteObject) {
		EClass class2 = EcoreFactory.eINSTANCE.createEClass();
		class2.setName("Many " + remoteObject.getName() + "_" + (classifiers.size() + 1));
		classifiers.add(class2);
		return true;
	}

	@Override
	public String getRemoteKey(TestRemoteEClass remoteObject) {
		return "remoteKeyFor" + remoteObject.getName();
	}

	@Override
	public String getLocalKeyForRemoteObject(TestRemoteEClass remoteObject) {
		return "localKeyFor" + remoteObject.getName();
	}

	@Override
	public String getLocalKeyForRemoteKey(String remoteKey) {
		return remoteKey.replace("remote", "local");
	}
}