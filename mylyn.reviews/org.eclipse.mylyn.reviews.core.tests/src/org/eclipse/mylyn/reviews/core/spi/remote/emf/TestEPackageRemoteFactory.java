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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

class TestEPackageRemoteFactory
		extends AbstractRemoteEmfFactory<EPackage, EPackage, String, TestRemoteEPackage, String, Integer> {

	static TestRemoteEPackage remote1 = new TestRemoteEPackage("Remote Package 1");

	static TestRemoteEPackage remote2 = new TestRemoteEPackage("Remote Package 2");

	static Map<String, TestRemoteEPackage> remoteForKey = new HashMap<>();

	{
		remoteForKey.put("remoteKeyFor Package 1", remote1);
		remoteForKey.put("remoteKeyFor Package 2", remote2);
	}

	public TestEPackageRemoteFactory() {
		super(new TestRemoteFactoryProvider(), EcorePackage.Literals.EPACKAGE__ESUBPACKAGES,
				EcorePackage.Literals.EPACKAGE__NS_URI);
	}

	@Override
	public TestRemoteEPackage pull(EPackage parent, String remoteKey, IProgressMonitor monitor) throws CoreException {
		return remoteForKey.get(remoteKey);
	}

	@Override
	protected EPackage createModel(EPackage parent, TestRemoteEPackage remotePackage) {
		EPackage pckg = EcoreFactory.eINSTANCE.createEPackage();
		pckg.setName(remotePackage.name.replace("Remote", "Local"));
		return pckg;
	}

	@Override
	public boolean updateModel(EPackage parent, EPackage object, TestRemoteEPackage remotePackage) {
		return true;
	}

	@Override
	public String getRemoteKey(TestRemoteEPackage remotePackage) {
		if (remotePackage == remote1) {
			return "remoteKeyFor Package 1";
		} else if (remotePackage == remote2) {
			return "remoteKeyFor Package 2";
		}
		throw new RuntimeException();
	}

	@Override
	public String getLocalKeyForRemoteKey(String remoteKey) {
		return remoteKey.replace("remote", "local");
	}

}
