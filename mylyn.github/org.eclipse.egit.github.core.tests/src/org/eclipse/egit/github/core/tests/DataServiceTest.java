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
 *    See git history
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

import org.eclipse.egit.github.core.Blob;
import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.Reference;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.ShaResource;
import org.eclipse.egit.github.core.Tag;
import org.eclipse.egit.github.core.Tree;
import org.eclipse.egit.github.core.TypedResource;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.service.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Unit tests of {@link DataService}
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("nls")
public class DataServiceTest {

	@Mock
	private GitHubClient client;

	@Mock
	private GitHubResponse response;

	private DataService service;

	private RepositoryId repo;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@BeforeEach
	public void before() throws IOException {
		doReturn(response).when(client).get(any(GitHubRequest.class));
		service = new DataService(client);
		repo = new RepositoryId("o", "n");
	}

	/**
	 * Create service using default constructor
	 */
	@Test
	public void constructor() {
		assertNotNull(new DataService().getClient());
	}

	/**
	 * Get blob with null sha
	 *
	 * @throws IOException
	 */
	@Test
	public void getBlobNullSha() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.getBlob(repo, null));
	}

	/**
	 * Get blob with empty sha
	 *
	 * @throws IOException
	 */
	@Test
	public void getBlobEmptySha() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.getBlob(repo, ""));
	}

	/**
	 * Get blob
	 *
	 * @throws IOException
	 */
	@Test
	public void getBlob() throws IOException {
		service.getBlob(repo, "aaa");
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/o/n/git/blobs/aaa");
		verify(client).get(request);
	}

	/**
	 * Create blob with null blob
	 *
	 * @throws IOException
	 */
	@Test
	public void createBlobNullBlob() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.createBlob(repo, null));
	}

	/**
	 * Create blob
	 *
	 * @throws IOException
	 */
	@Test
	public void createBlob() throws IOException {
		Blob blob = new Blob().setContent("a");
		service.createBlob(repo, blob);
		verify(client).post("/repos/o/n/git/blobs", blob, ShaResource.class);
	}

	/**
	 * Get tree with null sha
	 *
	 * @throws IOException
	 */
	@Test
	public void getTreeNullSha() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.getTree(repo, null));
	}

	/**
	 * Get tree with empty sha
	 *
	 * @throws IOException
	 */
	@Test
	public void getTreeEmptySha() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.getTree(repo, ""));
	}

	/**
	 * Get tree
	 *
	 * @throws IOException
	 */
	@Test
	public void getTree() throws IOException {
		service.getTree(repo, "abc");
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/o/n/git/trees/abc");
		verify(client).get(request);
	}

	/**
	 * Create tree
	 *
	 * @throws IOException
	 */
	@Test
	public void createTree() throws IOException {
		service.createTree(repo, null);
		verify(client).post("/repos/o/n/git/trees", new HashMap<>(), Tree.class);
	}

	/**
	 * Get reference with null name
	 *
	 * @throws IOException
	 */
	@Test
	public void getReferenceNullName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.getReference(repo, null));
	}

	/**
	 * Get reference for empty name
	 *
	 * @throws IOException
	 */
	@Test
	public void getReferenceEmptyName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.getReference(repo, ""));
	}

	/**
	 * Get reference
	 *
	 * @throws IOException
	 */
	@Test
	public void getReference() throws IOException {
		service.getReference(repo, "refs/heads/master");
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/o/n/git/refs/heads/master");
		verify(client).get(request);
	}

	/**
	 * Get references
	 *
	 * @throws IOException
	 */
	@Test
	public void getReferences() throws IOException {
		service.getReferences(repo);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/o/n/git/refs"));
		verify(client).get(request);
	}

	/**
	 * Create reference with null reference
	 *
	 * @throws IOException
	 */
	@Test
	public void createReferenceNullReference() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.createReference(repo, null));
	}

	/**
	 * Create reference with null object
	 *
	 * @throws IOException
	 */
	@Test
	public void createReferenceNullObject() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.createReference(repo, new Reference()));
	}

	/**
	 * Create reference
	 *
	 * @throws IOException
	 */
	@Test
	public void createReference() throws IOException {
		Reference ref = new Reference();
		ref.setRef("refs/heads/master");
		TypedResource object = new TypedResource();
		object.setSha("abcdef");
		ref.setObject(object);
		service.createReference(repo, ref);
		verify(client).post(eq("/repos/o/n/git/refs"), any(), eq(Reference.class));
	}

	/**
	 * Edit reference with null reference
	 *
	 * @throws IOException
	 */
	@Test
	public void editReferenceNullReference() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.editReference(repo, null));
	}

	/**
	 * Edit reference with null object
	 *
	 * @throws IOException
	 */
	@Test
	public void editReferenceNullObject() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.editReference(repo, new Reference().setRef("a")));
	}

	/**
	 * Edit reference with null name
	 *
	 * @throws IOException
	 */
	@Test
	public void editReferenceNullName() throws IOException {
		assertThrows(IllegalArgumentException.class,
				() -> service.editReference(repo, new Reference().setObject(new TypedResource())));
	}

	/**
	 * Edit reference with empty name
	 *
	 * @throws IOException
	 */
	@Test
	public void editReferenceEmptyName() throws IOException {
		assertThrows(IllegalArgumentException.class,
				() -> service.editReference(repo, new Reference().setObject(new TypedResource()).setRef("")));
	}

	/**
	 * Edit reference
	 *
	 * @throws IOException
	 */
	@Test
	public void editReference() throws IOException {
		Reference ref = new Reference();
		ref.setRef("refs/heads/master");
		TypedResource object = new TypedResource();
		object.setSha("00aa");
		ref.setObject(object);
		service.editReference(repo, ref);
		verify(client).post(eq("/repos/o/n/git/refs/heads/master"), any(), eq(Reference.class));
	}

	/**
	 * Get commit with null id
	 *
	 * @throws IOException
	 */
	@Test
	public void getCommitNullId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.getCommit(repo, null));
	}

	/**
	 * Get commit with empty id
	 *
	 * @throws IOException
	 */
	@Test
	public void getCommitEmptyId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.getCommit(repo, ""));
	}

	/**
	 * Get commit
	 *
	 * @throws IOException
	 */
	@Test
	public void getCommit() throws IOException {
		service.getCommit(repo, "ccc");
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/o/n/git/commits/ccc");
		verify(client).get(request);
	}

	/**
	 * Create commit with null commit
	 *
	 * @throws IOException
	 */
	@Test
	public void createCommitNullCommit() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.createCommit(repo, null));
	}

	/**
	 * Create commit
	 *
	 * @throws IOException
	 */
	@Test
	public void createCommit() throws IOException {
		Commit commit = new Commit();
		commit.setParents(Collections.singletonList(new Commit().setSha("abcd")));
		commit.setTree(new Tree().setSha("aaa"));
		service.createCommit(repo, commit);
		verify(client).post(eq("/repos/o/n/git/commits"), any(), eq(Commit.class));
	}

	/**
	 * Get tag with null id
	 *
	 * @throws IOException
	 */
	@Test
	public void getTagNullId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.getTag(repo, null));
	}

	/**
	 * Get tag with empty id
	 *
	 * @throws IOException
	 */
	@Test
	public void getTagEmptyId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.getTag(repo, ""));
	}

	/**
	 * Get tag
	 *
	 * @throws IOException
	 */
	@Test
	public void getTag() throws IOException {
		service.getTag(repo, "abcdef");
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/o/n/git/tags/abcdef");
		verify(client).get(request);
	}

	/**
	 * List tags
	 *
	 * @throws IOException
	 */
	@Test
	public void listTags() throws IOException {
		service.listTags(repo);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/o/n/git/refs/tags"));
		verify(client).get(request);
	}

	/**
	 * Create tag with null tag
	 *
	 * @throws IOException
	 */
	@Test
	public void createTagNullTag() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.createTag(repo, null));
	}

	/**
	 * Create tag
	 *
	 * @throws IOException
	 */
	@Test
	public void createTag() throws IOException {
		Tag tag = new Tag();
		tag.setObject(new TypedResource());
		service.createTag(repo, tag);
		verify(client).post(eq("/repos/o/n/git/tags"), any(), eq(Tag.class));
	}

	/**
	 * Delete reference
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteReference() throws IOException {
		Reference ref = new Reference();
		ref.setRef("refs/heads/master");
		service.deleteReference(repo, ref);
		verify(client).delete(eq("/repos/o/n/git/refs/heads/master"));
	}

	/**
	 * Delete branch
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteBranch() throws IOException {
		String branch = "branch";
		service.deleteBranch(repo, branch);
		verify(client).delete(eq("/repos/o/n/git/refs/heads/branch"));
	}

	/**
	 * Delete tag
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteTag() throws IOException {
		Tag tag = new Tag();
		tag.setTag("tag");
		service.deleteTag(repo, tag);
		verify(client).delete(eq("/repos/o/n/git/refs/tags/tag"));
	}
}
