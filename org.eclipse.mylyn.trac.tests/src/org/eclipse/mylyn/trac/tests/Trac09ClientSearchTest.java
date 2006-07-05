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

import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.Trac09Client;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.trac.tests.support.AbstractTracRepositoryFactory;

/**
 * @author Steffen Pingel
 */
public class Trac09ClientSearchTest extends AbstractTracClientSearchTest {

	public Trac09ClientSearchTest() {
		super(new AbstractTracRepositoryFactory() {
			protected ITracClient createRepository(String url, String username, String password) throws Exception {
				return new Trac09Client(new URL(url), Version.TRAC_0_9, username, password);
			}
		});
	}

}
