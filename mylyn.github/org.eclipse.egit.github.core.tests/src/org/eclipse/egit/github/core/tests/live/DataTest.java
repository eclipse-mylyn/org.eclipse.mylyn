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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.egit.github.core.Blob;
import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.Reference;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.Tree;
import org.eclipse.egit.github.core.TreeEntry;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.eclipse.egit.github.core.service.DataService;
import org.eclipse.egit.github.core.util.EncodingUtils;
import org.junit.Test;

/**
 * Unit tests of {@link DataService}
 */
public class DataTest extends LiveTest {

	/**
	 * Create and fetch blob
	 * 
	 * @throws Exception
	 */
	@Test
	public void createAndFetchBlob() throws Exception {
		checkUser();
		assertNotNull("Repo is required for test", writableRepo);

		RepositoryId repo = RepositoryId.create(client.getUser(), writableRepo);
		DataService service = new DataService(client);
		Blob blob = new Blob();
		blob.setContent("test content");
		blob.setEncoding(Blob.ENCODING_UTF8);
		String sha = service.createBlob(repo, blob);
		assertNotNull(sha);
		Blob fetch = service.getBlob(repo, sha);
		assertNotNull(fetch);
		assertEquals(blob.getContent(),
				new String(EncodingUtils.fromBase64(fetch.getContent()),
						IGitHubConstants.CHARSET_UTF8));
		assertEquals(Blob.ENCODING_BASE64, fetch.getEncoding());
	}

	/**
	 * Create and fetch tree
	 * 
	 * @throws Exception
	 */
	@Test
	public void createAndFetchTree() throws Exception {
		checkUser();
		assertNotNull("Repo is required for test", writableRepo);

		RepositoryId repo = RepositoryId.create(client.getUser(), writableRepo);
		DataService service = new DataService(client);
		TreeEntry entry = new TreeEntry();
		entry.setPath("test.txt");
		entry.setSha("0000000000000000000000000000000000000000");
		entry.setType("blob");
		Tree tree = service.createTree(repo, Collections.singleton(entry));
		assertNotNull(tree);
		assertNotNull(tree.getSha());
		assertNotNull(tree.getUrl());
		assertNotNull(tree.getTree());
		assertEquals(1, tree.getTree().size());
		assertEquals(entry.getPath(), tree.getTree().get(0).getPath());
		assertEquals(entry.getSha(), tree.getTree().get(0).getSha());
		assertEquals(entry.getType(), tree.getTree().get(0).getType());

		Tree fetched = service.getTree(repo, tree.getSha());
		assertNotNull(fetched);
		assertEquals(tree.getSha(), fetched.getSha());
		assertEquals(tree.getUrl(), fetched.getUrl());
	}

	/**
	 * Create and fetch commit
	 * 
	 * @throws Exception
	 */
	@Test
	public void createAndFetchCommit() throws Exception {
		checkUser();
		assertNotNull("Repo is required for test", writableRepo);

		RepositoryId repo = RepositoryId.create(client.getUser(), writableRepo);
		DataService service = new DataService(client);
		Commit commit = new Commit();
		commit.setMessage("commit message");
		commit.setTree(new Tree()
				.setSha("0000000000000000000000000000000000000000"));
		Commit created = service.createCommit(repo, commit);
		assertNotNull(created);
		assertNotNull(created.getSha());
		Commit fetched = service.getCommit(repo, created.getSha());
		assertNotNull(fetched);
		assertEquals(created.getSha(), fetched.getSha());
		assertEquals(created.getMessage(), fetched.getMessage());
	}

	/**
	 * Test fetching references
	 * 
	 * @throws IOException
	 */
	@Test
	public void fetchReferences() throws IOException {
		checkUser();
		assertNotNull("Repo is required for test", writableRepo);

		RepositoryId repo = RepositoryId.create(client.getUser(), writableRepo);
		DataService service = new DataService(client);
		List<Reference> refs = service.getReferences(repo);
		assertNotNull(refs);
		assertFalse(refs.isEmpty());
		for (Reference ref : refs) {
			assertNotNull(ref);
			assertNotNull(ref.getRef());
			assertNotNull(ref.getUrl());
			assertNotNull(ref.getObject());
			assertNotNull(ref.getObject().getSha());
			Reference fetched = service.getReference(repo, ref.getRef());
			assertNotNull(fetched);
			assertEquals(ref.getRef(), fetched.getRef());
			assertEquals(ref.getUrl(), fetched.getUrl());
		}
	}
}
