/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests.live;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.service.GistService;

/**
 * @author Kevin Sawicki (kevin@github.com)
 */
public class GistTest extends LiveTest {

	/**
	 * Test list a user's gists
	 * 
	 * @throws IOException
	 */
	public void testList() throws IOException {
		GistService service = new GistService(client);
		Collection<Gist> gists = service.getGists("kevinsawicki");
		assertNotNull(gists);
		assertFalse(gists.isEmpty());
		for (Gist gist : gists) {
			assertNotNull(gist);
			assertNotNull(gist.getCreatedAt());
			assertNotNull(gist.getId());
			assertNotNull(gist.getFiles());
			for (GistFile file : gist.getFiles().values()) {
				assertNotNull(file);
			}
			List<Comment> comments = service.getComments(gist.getId());
			assertNotNull(comments);
			for (Comment comment : comments) {
				assertNotNull(comment);
				assertNotNull(comment.getUrl());
				assertNotNull(comment.getCreatedAt());
				assertNotNull(comment.getUpdatedAt());
				assertNotNull(comment.getUser());
			}
		}
	}
}
