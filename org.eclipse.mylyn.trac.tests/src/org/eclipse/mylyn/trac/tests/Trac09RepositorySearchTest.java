/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.trac.tests;

import java.net.URL;

import org.eclipse.mylar.internal.trac.core.ITracRepository;
import org.eclipse.mylar.internal.trac.core.Trac09Repository;
import org.eclipse.mylar.internal.trac.core.ITracRepository.Version;
import org.eclipse.mylar.trac.tests.support.AbstractTracRepositoryFactory;

/**
 * @author Steffen Pingel
 */
public class Trac09RepositorySearchTest extends AbstractTracRepositorySearchTest {

	public Trac09RepositorySearchTest() {
		super(new AbstractTracRepositoryFactory() {
			protected ITracRepository createRepository(String url, String username, String password) throws Exception {
				return new Trac09Repository(new URL(url), Version.TRAC_0_9, username, password);
			}
		});
	}

}
