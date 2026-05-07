/*******************************************************************************
 * Copyright (c) 2013, 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.mylyn.gerrit.tests.AbstractGerritFixtureTest;
import org.eclipse.mylyn.internal.commons.core.FileUtil;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.spi.remote.JobRemoteService;
import org.eclipse.mylyn.reviews.spi.edit.remote.review.ReviewsRemoteEditFactoryProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@SuppressWarnings("nls")
@Disabled("No gerrit instance available")
public class GerritDataLocatorTest extends AbstractGerritFixtureTest {
	@TempDir
	private File tempDir;

	@BeforeEach
	void skipIfExcluded() {
		assumeFalse(fixture.isExcluded(), "Fixture is excluded");
	}

	private ReviewHarness reviewHarness;

	private final TestDataLocator locator = new TestDataLocator();

	private GerritRemoteFactoryProvider provider;

	@BeforeEach
	void setUp() throws Exception {
		File rootDir = new File(locator.getSystemDataPath().toOSString());
		FileUtil.deleteTree(rootDir);
		Files.createDirectories(rootDir.toPath());
		reviewHarness = new ReviewHarness();
		provider = new GerritRemoteFactoryProvider(reviewHarness.getClient());
		provider.setDataLocator(locator);
		provider.setService(new JobRemoteService());
	}

	@AfterEach
	void tearDown() throws Exception {
		reviewHarness.dispose();
	}

	@Test
	public void testCreateRoot() throws Exception {
		Path testPath = tempDir.toPath().resolve("gerrit_tests");
		Path filePath = testPath.resolve("reviews_bin")
				.resolve("org.eclipse.mylyn.gerrit-"
						+ ReviewsRemoteEditFactoryProvider.asFileName(reviewHarness.getTaskRepository().getUrl()))
				.resolve("Repository")
				.resolve("Repository.reviews");
		File file = filePath.toFile();
		assertThat("File should not exist at: " + filePath, file.exists(), is(false));
		provider.open();
		assertThat("File should exist at: " + filePath, file.exists(), is(true));
		assertThat(provider.getRoot(), instanceOf(IRepository.class));
		assertThat(provider.getRoot().getTaskRepositoryUrl(), is(reviewHarness.getTaskRepository().getUrl()));
		assertThat(provider.getEditingDomain().getResourceSet().getResources().size(), is(1));
		assertThat(provider.getEditingDomain().getResourceSet().getResources().get(0).getContents().size(), is(1));
		assertThat(provider.getEditingDomain().getResourceSet().getResources().get(0).getContents().get(0),
				is((EObject) provider.getRoot()));
		provider.close();
		assertThat(provider.getRoot(), nullValue());
		assertThat("File should exist at: " + filePath, file.exists(), is(true));
		provider.open();
		assertThat(provider.getRoot(), notNullValue());
		assertThat(provider.getRoot().getTaskRepositoryUrl(), is(reviewHarness.getTaskRepository().getUrl()));
	}

	@Test
	public void testMigrate() throws Exception {
		Path testPath = tempDir.toPath().resolve("gerrit_tests");
		File binDir = testPath.resolve("reviews_bin").toFile();
		File xmlDir = testPath.resolve("reviews_xml").toFile();
		Files.createDirectories(xmlDir.toPath());
		File xmlFile = testPath.resolve("reviews_xml").resolve("SomeFile.txt").toFile();
		xmlFile.createNewFile();
		File modelDir = testPath.resolve("model").toFile();
		Files.createDirectories(modelDir.toPath());

		assertThat(binDir.exists(), is(false));
		assertThat(xmlDir.exists(), is(true));
		assertThat(xmlFile.exists(), is(true));
		assertThat(modelDir.exists(), is(true));
		provider.setDataLocator(new TestDataLocator());
		provider.open();
		assertThat(binDir.exists(), is(true));
		assertThat(xmlDir.exists(), is(false));
		assertThat(xmlFile.exists(), is(false));
		assertThat(modelDir.exists(), is(false));
		provider.close();
	}

	@Test
	public void testCreateChild() throws Exception {
		Path testPath = tempDir.toPath().resolve("gerrit_tests");
		Path filePath = testPath.resolve("reviews_bin")
				.resolve("org.eclipse.mylyn.gerrit-"
						+ ReviewsRemoteEditFactoryProvider.asFileName(reviewHarness.getTaskRepository().getUrl()))
				.resolve("Review")
				.resolve("2.reviews");
		File file = filePath.toFile();
		assertThat("File should not exist at: " + filePath, file.exists(), is(false));
		provider.open();
		IReview child = provider.open("2");
		assertThat(child.getId(), is("2"));
		child.setMessage("Foo");
		assertThat(provider.getRoot().getReviews().get(0), sameInstance(child));
		provider.close(child);
		assertThat("File should exist at: " + filePath, file.exists(), is(true));
		assertThat(provider.getRoot().getReviews().size(), is(0));
		provider.close();
		provider.open();
		assertThat(provider.getRoot().getReviews().size(), is(0));
		IReview newChild = provider.open("2");
		assertThat(provider.getRoot().getReviews().size(), is(1));
		assertThat(newChild.getId(), is("2"));
		assertThat(newChild.getMessage(), is("Foo"));
	}
}
