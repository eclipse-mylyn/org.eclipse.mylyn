/*******************************************************************************
 *  Copyright (c) 2011 Christian Trutz
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.service.LabelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests of {@link LabelService}
 */
@RunWith(MockitoJUnitRunner.class)
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
	@Before
	public void before() throws IOException {
		doReturn(response).when(gitHubClient).get(any(GitHubRequest.class));
		labelService = new LabelService(gitHubClient);
	}

	/**
	 * Create label service with null client
	 */
	@Test(expected = IllegalArgumentException.class)
	public void constructorNullArgument() {
		new LabelService(null);
	}

	/**
	 * Get labels with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getLabelsNullUser() throws IOException {
		labelService.getLabels(null, "not null");
	}

	/**
	 * Get labels with null repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getLabelsNullRepositoryName() throws IOException {
		labelService.getLabels("not null", null);
	}

	/**
	 * Get labels with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void getLabelsOK() throws IOException {
		labelService.getLabels("test_user", "test_repository");
		verify(gitHubClient).get(any(GitHubRequest.class));
	}

	/**
	 * Set labels with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLabelsNullUser() throws IOException {
		labelService.setLabels(null, "not null", "not null",
				new LinkedList<Label>());
	}

	/**
	 * Set labels with null repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLabelsNullRepositoryName() throws IOException {
		labelService.setLabels("not null", null, "not null",
				new LinkedList<Label>());
	}

	/**
	 * Set labels with null issue id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLabelsNullIssueId() throws IOException {
		labelService.setLabels("not null", "not null", null,
				new LinkedList<Label>());
	}

	/**
	 * Set labels with null labels list
	 *
	 * @throws IOException
	 */
	@Test
	public void setLabelsNullLabels() throws IOException {
		labelService.setLabels("test_user", "test_repository", "1", null);
		TypeToken<List<Label>> labelsToken = new TypeToken<List<Label>>() {
		};
		verify(gitHubClient).put(
				"/repos/test_user/test_repository/issues/1/labels", null,
				labelsToken.getType());
	}

	/**
	 * Set labels with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void setLabelsOK() throws IOException {
		List<Label> labels = new LinkedList<Label>();
		labelService.setLabels("test_user", "test_repository", "1", labels);
		TypeToken<List<Label>> labelsToken = new TypeToken<List<Label>>() {
		};
		verify(gitHubClient).put(
				"/repos/test_user/test_repository/issues/1/labels", labels,
				labelsToken.getType());
	}

	/**
	 * Create label with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createLabelNullUser() throws IOException {
		labelService.createLabel(null, "not null", new Label());
	}

	/**
	 * Create label with null repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createLabelNullRepositoryName() throws IOException {
		labelService.createLabel("not null", null, new Label());
	}

	/**
	 * Create label with null label
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createLabelNullLabel() throws IOException {
		labelService.createLabel("not null", "not null", null);
	}

	/**
	 * Create label with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void createLabelOK() throws IOException {
		Label label = new Label();
		labelService.createLabel("test_user", "test_repository", label);
		verify(gitHubClient).post("/repos/test_user/test_repository/labels",
				label, Label.class);
	}
}
