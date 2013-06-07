/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.spi.edit.remote;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.mylyn.reviews.core.spi.remote.AbstractDataLocator;
import org.eclipse.mylyn.reviews.core.spi.remote.JobRemoteService;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Miles Parker
 */
public class AbstractRemoteEditFactoryProviderTest extends TestCase {

	class TestEditFactoryProvider extends AbstractRemoteEditFactoryProvider<EPackage, EClass> {
		public TestEditFactoryProvider() {
			super(EcoreFactory.eINSTANCE, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
					EcorePackage.Literals.ENAMED_ELEMENT__NAME, EcorePackage.Literals.ECLASS);
		}

		@Override
		public String getFileExtension(EClass eClass) {
			return "ecore";
		}

		@Override
		public String getContainerSegment() {
			return "Container";
		}

		@Override
		public EditingDomain getEditingDomain() {
			return super.getEditingDomain();
		}
	}

	AbstractDataLocator testDataLocator = new AbstractDataLocator() {

		@Override
		public IPath getSystemPath() {
			return new Path(FileUtils.getTempDirectory().getAbsolutePath() + File.separator
					+ "org.eclipse.mylyn.reviews.tests" + File.separator + "RemoteEditFactoryTest");
		}
	};

	@Override
	@Before
	protected void setUp() throws Exception {
		System.err.println("Sending test output to: " + testDataLocator.getSystemPath());
		File rootDir = new File(testDataLocator.getSystemPath().toPortableString());
		FileUtils.deleteDirectory(rootDir);
	}

	@Test
	public void testCreateRoot() {
		TestEditFactoryProvider provider = new TestEditFactoryProvider();
		provider.setDataLocator(testDataLocator);
		String filePath = testDataLocator.getSystemPath() + File.separator + "Container" + File.separator + "EPackage"
				+ File.separator + "EPackage.ecore";
		File file = new File(filePath);
		assertThat("File should not exist at: " + filePath, file.exists(), is(false));
		provider.open();
		assertThat(provider.getRoot(), instanceOf(EPackage.class));
		assertThat("File should exist at: " + filePath, file.exists(), is(true));
		provider.getRoot().setNsPrefix("RootPackage");
		assertThat(provider.getRoot(), instanceOf(EPackage.class));
		assertThat(provider.getEditingDomain().getResourceSet().getResources().size(), is(1));
		assertThat(provider.getEditingDomain().getResourceSet().getResources().get(0).getContents().size(), is(1));
		assertThat(provider.getEditingDomain().getResourceSet().getResources().get(0).getContents().get(0),
				is((EObject) provider.getRoot()));
		provider.close();
		assertThat(provider.getRoot(), nullValue());
		assertThat("File should exist at: " + filePath, file.exists(), is(true));
		provider.open();
		assertThat(provider.getRoot(), notNullValue());
		assertThat(provider.getRoot().getNsPrefix(), is("RootPackage"));
	}

	@Test
	public void testBadFile() throws Exception {
		TestEditFactoryProvider provider = new TestEditFactoryProvider();
		provider.setDataLocator(testDataLocator);
		String filePath = testDataLocator.getSystemPath() + File.separator + "Container" + File.separator + "EPackage"
				+ File.separator + "EPackage.ecore";
		File parentDir = new File(testDataLocator.getSystemPath() + File.separator + "Container" + File.separator
				+ "EPackage");
		parentDir.mkdirs();
		File file = new File(filePath);
		file.createNewFile();
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
		bufferedWriter.write("Garbage");
		bufferedWriter.close();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		assertThat(reader.readLine(), is("Garbage"));
		reader.close();
		assertThat("File should exist at: " + filePath, file.exists(), is(true));
		provider.open();
		assertThat(provider.getRoot(), instanceOf(EPackage.class));
		provider.save();
		reader = new BufferedReader(new FileReader(file));
		assertThat(reader.readLine(), startsWith("<?xml version"));
		reader.close();
	}

	@Test
	public void testBadContents() throws Exception {
		TestEditFactoryProvider provider = new TestEditFactoryProvider();
		provider.setDataLocator(testDataLocator);
		provider.open();
		Resource resource = provider.getEditingDomain().getResourceSet().getResources().get(0);
		EList<EObject> contents = resource.getContents();
		contents.clear();
		EClass createEClass = EcoreFactory.eINSTANCE.createEClass();
		contents.add(createEClass);
		provider.close();
		provider.open();
	}

	@Test
	public void testCreateChild() {
		TestEditFactoryProvider provider = new TestEditFactoryProvider();
		provider.setDataLocator(testDataLocator);
		provider.setService(new JobRemoteService());
		String filePath = testDataLocator.getSystemPath() + File.separator + "Container" + File.separator + "EClass"
				+ File.separator + "123.ecore";
		File file = new File(filePath);
		assertThat("File should not exist at: " + filePath, file.exists(), is(false));
		provider.open();
		EClass child = provider.open("123");
		assertThat(child.getName(), is("123"));
		assertThat(child.getInstanceClassName(), nullValue());
		child.setInstanceClassName("Foo");
		assertThat(provider.getRoot().getEClassifiers().get(0), sameInstance((EClassifier) child));
		provider.close(child);
		assertThat("File should exist at: " + filePath, file.exists(), is(true));
		assertThat(provider.getRoot().getEClassifiers().size(), is(0));
		provider.close();
		provider.open();
		assertThat(provider.getRoot().getEClassifiers().size(), is(0));
		EClass newChild = provider.open("123");
		assertThat(provider.getRoot().getEClassifiers().size(), is(1));
		assertThat(newChild.getName(), is("123"));
		assertThat(newChild.getInstanceClassName(), is("Foo"));
	}

	@Test
	public void testBadChild() throws Exception {
		TestEditFactoryProvider provider = new TestEditFactoryProvider();
		provider.setDataLocator(testDataLocator);
		provider.setService(new JobRemoteService());
		String filePath = testDataLocator.getSystemPath() + File.separator + "Container" + File.separator + "EClass"
				+ File.separator + "123.ecore";
		File file = new File(filePath);
		assertThat("File should not exist at: " + filePath, file.exists(), is(false));
		provider.open();
		EClass child = provider.open("123");
		assertThat(child.getName(), is("123"));
		child.setInstanceClassName("Foo");
		provider.close(child);
		assertThat("File should exist at: " + filePath, file.exists(), is(true));
		assertThat(provider.getRoot().getEClassifiers().size(), is(0));
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
		bufferedWriter.write("Garbage");
		bufferedWriter.close();
		EClass newChild = provider.open("123");
		assertThat(provider.getRoot().getEClassifiers().size(), is(1));
		assertThat(newChild.getInstanceClassName(), nullValue());
		provider.save(newChild);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		assertThat(reader.readLine(), startsWith("<?xml version"));
		reader.close();
	}

	@Test
	public void testDeleteCache() throws Exception {
		TestEditFactoryProvider provider = new TestEditFactoryProvider();
		provider.setDataLocator(testDataLocator);
		File testFile = new File(testDataLocator.getSystemPath() + File.separator + "Blah");
		testFile.mkdirs();
		File root = new File(testDataLocator.getSystemPath().toOSString());
		assertThat(root.exists(), is(true));
		provider.deleteCache();
		assertThat(root.exists(), is(false));
	}
}
