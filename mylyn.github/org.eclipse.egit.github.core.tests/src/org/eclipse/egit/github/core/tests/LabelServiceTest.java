/*******************************************************************************
 * *  Copyright (c) 2011 Christian Trutz
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *    See git history
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.service.LabelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.google.gson.reflect.TypeToken;

/**
 * Unit tests of {@link LabelService}
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("nls")
public class LabelServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	@Mock
	private GitHubResponse response;

	private LabelService labelService;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@BeforeEach
	public void before() throws IOException {
		doReturn(response).when(gitHubClient).get(any(GitHubRequest.class));
		labelService = new LabelService(gitHubClient);
	}

	/**
	 * Create label service with null client
	 */
	@Test
	public void constructorNullArgument() {
		assertThrows(IllegalArgumentException.class, () -> new LabelService(null));
	}

	/**
	 * Create label service using default constructor
	 */
	@Test
	public void defaultConstuctor() {
		assertNotNull(new LabelService().getClient());
	}

	/**
	 * Get labels with null user
	 *
	 * @throws IOException
	 */
	@Test
	public void getLabelsNullUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> labelService.getLabels(null, "not null"));
	}

	/**
	 * Get labels with null repository name
	 *
	 * @throws IOException
	 */
	@Test
	public void getLabelsNullRepositoryName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> labelService.getLabels("not null", null));
	}

	/**
	 * Get labels with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void getLabels() throws IOException {
		labelService.getLabels("lu", "lr");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/lu/lr/labels"));
		verify(gitHubClient).get(request);
	}

	/**
	 * Get labels with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void getLabelsWithRepositoryId() throws IOException {
		RepositoryId repo = new RepositoryId("lu", "lr");
		labelService.getLabels(repo);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/lu/lr/labels"));
		verify(gitHubClient).get(request);
	}

	/**
	 * Set labels with null user
	 *
	 * @throws IOException
	 */
	@Test
	public void setLabelsNullUser() throws IOException {
		assertThrows(IllegalArgumentException.class,
				() -> labelService.setLabels(null, "not null", "not null", new LinkedList<>()));
	}

	/**
	 * Set labels with null repository name
	 *
	 * @throws IOException
	 */
	@Test
	public void setLabelsNullRepositoryName() throws IOException {
		assertThrows(IllegalArgumentException.class,
				() -> labelService.setLabels("not null", null, "not null", new LinkedList<>()));
	}

	/**
	 * Set labels with null issue id
	 *
	 * @throws IOException
	 */
	@Test
	public void setLabelsNullIssueId() throws IOException {
		assertThrows(IllegalArgumentException.class,
				() -> labelService.setLabels("not null", "not null", null, new LinkedList<>()));
	}

	/**
	 * Set labels with empty issue id
	 *
	 * @throws IOException
	 */
	@Test
	public void setLabelsEmptyIssueId() throws IOException {
		assertThrows(IllegalArgumentException.class,
				() -> labelService.setLabels("not null", "not null", "", new LinkedList<>()));
	}

	/**
	 * Set labels with null labels list
	 *
	 * @throws IOException
	 */
	@Test
	public void setLabelsNullLabels() throws IOException {
		labelService.setLabels("test_user", "test_repository", "1", null);
		TypeToken<List<Label>> labelsToken = new TypeToken<>() {
		};
		verify(gitHubClient).put(
				"/repos/test_user/test_repository/issues/1/labels", null, labelsToken.getType());
	}

	/**
	 * Set labels with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void setLabels() throws IOException {
		List<Label> labels = new LinkedList<>();
		labelService.setLabels("test_user", "test_repository", "1", labels);
		TypeToken<List<Label>> labelsToken = new TypeToken<>() {
		};
		verify(gitHubClient).put(
				"/repos/test_user/test_repository/issues/1/labels", labels, labelsToken.getType());
	}

	/**
	 * Set labels with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void setLabelsWithRepositoryId() throws IOException {
		RepositoryId repo = new RepositoryId("test_user", "test_repository");
		List<Label> labels = new LinkedList<>();
		labelService.setLabels(repo, "1", labels);
		TypeToken<List<Label>> labelsToken = new TypeToken<>() {
		};
		verify(gitHubClient).put(
				"/repos/test_user/test_repository/issues/1/labels", labels, labelsToken.getType());
	}

	/**
	 * Create label with null user
	 *
	 * @throws IOException
	 */
	@Test
	public void createLabelNullUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> labelService.createLabel(null, "not null", new Label()));
	}

	/**
	 * Create label with null repository name
	 *
	 * @throws IOException
	 */
	@Test
	public void createLabelNullRepositoryName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> labelService.createLabel("not null", null, new Label()));
	}

	/**
	 * Create label with null label
	 *
	 * @throws IOException
	 */
	@Test
	public void createLabelNullLabel() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> labelService.createLabel("not null", "not null", null));
	}

	/**
	 * Create label with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void createLabel() throws IOException {
		Label label = new Label();
		labelService.createLabel("test_user", "test_repository", label);
		verify(gitHubClient).post("/repos/test_user/test_repository/labels", label, Label.class);
	}

	/**
	 * Create label with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void createLabelWithRepositoryId() throws IOException {
		RepositoryId repo = new RepositoryId("test_user", "test_repository");
		Label label = new Label();
		labelService.createLabel(repo, label);
		verify(gitHubClient).post("/repos/test_user/test_repository/labels", label, Label.class);
	}

	/**
	 * Delete label with null user
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteNullUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> labelService.deleteLabel(null, "repo", "label"));
	}

	/**
	 * Delete label with empty user
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteEmptyUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> labelService.deleteLabel("", "repo", "label"));
	}

	/**
	 * Delete label with null repository name
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteNullRepositoryName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> labelService.deleteLabel("user", null, "label"));
	}

	/**
	 * Delete label with empty repository name
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteEmptyRepositoryName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> labelService.deleteLabel("user", "", "label"));
	}

	/**
	 * Delete label with null name
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteNullLabel() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> labelService.deleteLabel("user", "repo", null));
	}

	/**
	 * Delete label with empty name
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteEmptyLabel() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> labelService.deleteLabel("user", "repo", ""));
	}

	/**
	 * Get label with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteLabel() throws IOException {
		labelService.deleteLabel("user", "repo", "label");
		verify(gitHubClient).delete("/repos/user/repo/labels/label");
	}

	/**
	 * Get label with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteLabelWithRepositoryId() throws IOException {
		RepositoryId repo = new RepositoryId("user", "repo");
		labelService.deleteLabel(repo, "label");
		verify(gitHubClient).delete("/repos/user/repo/labels/label");
	}

	/**
	 * Get label with null user
	 *
	 * @throws IOException
	 */
	@Test
	public void getNullUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> labelService.getLabel(null, "repo", "label"));
	}

	/**
	 * Get label with empty user
	 *
	 * @throws IOException
	 */
	@Test
	public void getEmptyUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> labelService.getLabel("", "repo", "label"));
	}

	/**
	 * Get label with null repository name
	 *
	 * @throws IOException
	 */
	@Test
	public void getNullRepositoryName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> labelService.getLabel("user", null, "label"));
	}

	/**
	 * Get label with empty repository name
	 *
	 * @throws IOException
	 */
	@Test
	public void getEmptyRepositoryName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> labelService.getLabel("user", "", "label"));
	}

	/**
	 * Get label with null name
	 *
	 * @throws IOException
	 */
	@Test
	public void getNullLabel() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> labelService.getLabel("user", "repo", null));
	}

	/**
	 * Get label with empty name
	 *
	 * @throws IOException
	 */
	@Test
	public void getEmptyLabel() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> labelService.getLabel("user", "repo", ""));
	}

	/**
	 * Get label with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void getLabel() throws IOException {
		labelService.getLabel("user", "repo", "bugs");
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/user/repo/labels/bugs");
		verify(gitHubClient).get(request);
	}

	/**
	 * Get label with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void getLabelWithRepositoryId() throws IOException {
		RepositoryId repo = new RepositoryId("user", "repo");
		labelService.getLabel(repo, "bugs");
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/user/repo/labels/bugs");
		verify(gitHubClient).get(request);
	}

	/**
	 * Edit label with null label
	 *
	 * @throws IOException
	 */
	@Test
	public void editLabelNullLabel() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> labelService.editLabel(RepositoryId.create("a", "b"), null));
	}

	/**
	 * Edit label with null label
	 *
	 * @throws IOException
	 */
	@Test
	public void editLabelNullLabelName() throws IOException {
		assertThrows(IllegalArgumentException.class,
				() -> labelService.editLabel(RepositoryId.create("a", "b"), new Label()));
	}

	/**
	 * Edit label with null label
	 *
	 * @throws IOException
	 */
	@Test
	public void editLabelEmptyLabelName() throws IOException {
		assertThrows(IllegalArgumentException.class,
				() -> labelService.editLabel(RepositoryId.create("a", "b"), new Label().setName("")));
	}

	/**
	 * Edit label
	 *
	 * @throws IOException
	 */
	@Test
	public void editLabel() throws IOException {
		Label label = new Label();
		label.setName("l1");
		label.setColor("#FF");
		labelService.editLabel(RepositoryId.create("a", "b"), label);
		verify(gitHubClient).post("/repos/a/b/labels/l1", label, Label.class);
	}
}
