/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.spi.remote.JobRemoteService;
import org.eclipse.mylyn.reviews.spi.edit.remote.review.ReviewsRemoteEditFactoryProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MylynDataLocatorTest extends TestCase {

	private ReviewHarness reviewHarness;

	private final TestDataLocator locator = new TestDataLocator();

	private GerritRemoteFactoryProvider provider;

	@Override
	@Before
	public void setUp() throws Exception {
		System.err.println("*** Setup");
		File rootDir = new File(locator.getSystemPath().toPortableString());
		FileUtils.deleteDirectory(rootDir);
		reviewHarness = new ReviewHarness(System.currentTimeMillis() + "");
		provider = new GerritRemoteFactoryProvider(reviewHarness.repository, reviewHarness.client);
		provider.setDataLocator(locator);
		provider.setService(new JobRemoteService());
	}

	@Override
	@After
	public void tearDown() throws Exception {
		System.err.println("*** tear down");
		reviewHarness.dispose();
	}

	@Test
	public void testCreateRoot() throws Exception {
		System.err.println("*** Create Root");
		String filePath = FileUtils.getTempDirectory().getAbsolutePath() + File.separator
				+ "org.eclipse.mylyn.gerrit.tests" + File.separator + "org.eclipse.mylyn.gerrit-"
				+ ReviewsRemoteEditFactoryProvider.asFileName(reviewHarness.repository.getUrl()) + File.separator
				+ "Repository" + File.separator + "Repository.reviews";
		System.err.println(filePath);
		File file = new File(filePath);
		assertThat("File should not exist at: " + filePath, file.exists(), is(false));
		provider.open();
		assertThat("File should exist at: " + filePath, file.exists(), is(true));
		assertThat(provider.getRoot(), instanceOf(IRepository.class));
		assertThat(provider.getRoot().getTaskRepositoryUrl(), is(reviewHarness.repository.getUrl()));
		assertThat(provider.getEditingDomain().getResourceSet().getResources().size(), is(1));
		assertThat(provider.getEditingDomain().getResourceSet().getResources().get(0).getContents().size(), is(1));
		assertThat(provider.getEditingDomain().getResourceSet().getResources().get(0).getContents().get(0),
				is((EObject) provider.getRoot()));
		provider.close();
		assertThat(provider.getRoot(), nullValue());
		assertThat("File should exist at: " + filePath, file.exists(), is(true));
		provider.open();
		assertThat(provider.getRoot(), notNullValue());
		assertThat(provider.getRoot().getTaskRepositoryUrl(), is(reviewHarness.repository.getUrl()));
	}

	@Test
	public void testCreateChild() throws Exception {
		System.err.println("*** Create Child");
		String filePath = locator.getSystemPath() + File.separator + "org.eclipse.mylyn.gerrit-"
				+ ReviewsRemoteEditFactoryProvider.asFileName(reviewHarness.repository.getUrl()) + File.separator
				+ "Review" + File.separator + "2.reviews";
		File file = new File(filePath);
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
