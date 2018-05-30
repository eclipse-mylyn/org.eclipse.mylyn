/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests.live;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.junit.Test;

/**
 * Unit tests of {@link CollaboratorService}
 */
public class CollaboratorTest extends LiveTest {

	/**
	 * Unit test of
	 * {@link CollaboratorService#getCollaborators(org.eclipse.egit.github.core.IRepositoryIdProvider)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void getCollaborators() throws Exception {
		checkUser();

		CollaboratorService service = new CollaboratorService(client);
		List<User> collabs = service.getCollaborators(new RepositoryId(client
				.getUser(), writableRepo));
		assertNotNull(collabs);
		assertFalse(collabs.isEmpty());
		for (User user : collabs) {
			assertNotNull(user);
			assertNotNull(user.getLogin());
		}
	}
}
