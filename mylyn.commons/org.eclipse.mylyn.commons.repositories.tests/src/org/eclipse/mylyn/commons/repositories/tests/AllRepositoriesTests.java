/*******************************************************************************
 * Copyright (c) 2026 George Lindholm
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https:www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.tests;

import org.eclipse.mylyn.commons.repositories.tests.core.CertificateCredentialsTest;
import org.eclipse.mylyn.commons.repositories.tests.core.CredentialsFactoryTest;
import org.eclipse.mylyn.commons.repositories.tests.core.CredentialsStoreTest;
import org.eclipse.mylyn.commons.repositories.tests.core.CredentialsStoresTest;
import org.eclipse.mylyn.commons.repositories.tests.core.InMemoryCredentialsStoreTest;
import org.eclipse.mylyn.commons.repositories.tests.core.RepositoryLocationTest;
import org.eclipse.mylyn.commons.repositories.tests.core.SecureCredentialsStoreTest;
import org.eclipse.mylyn.commons.repositories.tests.core.UiSecureCredentialsStoreTest;
import org.eclipse.mylyn.commons.repositories.tests.ui.RepositoriesViewTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({ CertificateCredentialsTest.class, CredentialsFactoryTest.class, CredentialsStoreTest.class,
	CredentialsStoresTest.class, InMemoryCredentialsStoreTest.class, RepositoryLocationTest.class,
	SecureCredentialsStoreTest.class, UiSecureCredentialsStoreTest.class, //
	RepositoriesViewTest.class
})
public class AllRepositoriesTests {
}
