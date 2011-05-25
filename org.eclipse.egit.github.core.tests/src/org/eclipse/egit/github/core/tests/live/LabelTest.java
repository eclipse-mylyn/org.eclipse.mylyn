/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests.live;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.http.HttpStatus;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.LabelService;
import org.junit.Test;

/**
 * Live unit tests of {@link LabelService}
 */
public class LabelTest extends LiveTest {

	/**
	 * Test creating and deleting a label
	 * 
	 * @throws Exception
	 */
	@Test
	public void createDelete() throws Exception {
		assertNotNull("User is required for test", client.getUser());
		assertNotNull("Repo is required for test", writableRepo);

		Label l = new Label();
		l.setName("label" + System.currentTimeMillis());
		LabelService service = new LabelService(client);
		Label created = service.createLabel(client.getUser(), writableRepo, l);
		assertNotNull(created);
		assertEquals(l.getName(), created.getName());
		List<Label> labels = service.getLabels(client.getUser(), writableRepo);
		Label fetched = null;
		assertNotNull(labels);
		for (Label label : labels)
			if (created.getName().equals(label.getName())) {
				fetched = label;
				break;
			}
		assertNotNull(fetched);
		assertEquals(created.getName(), fetched.getName());

		fetched = service.getLabel(client.getUser(), writableRepo,
				created.getName());
		assertNotNull(fetched);
		assertEquals(created.getName(), fetched.getName());

		service.deleteLabel(client.getUser(), writableRepo, created.getName());
		try {
			service.getLabel(client.getUser(), writableRepo, created.getName());
			fail("Fetch did not throw exception");
		} catch (RequestException e) {
			assertEquals(HttpStatus.SC_NOT_FOUND, e.getStatus());
		}
	}

}
