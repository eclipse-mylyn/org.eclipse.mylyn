/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.mylyn.internal.wikitext.MockMarkupLanguage;
import org.eclipse.mylyn.internal.wikitext.MockMarkupLanguageProvider;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguageProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

/**
 * Tests for {@link ServiceLocator}
 * 
 * @author David Green
 */
public class ServiceLocatorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private ServiceLocator locator;

	@Before
	public void setUp() {
		ServiceLocator.setImplementation(null);
		locator = ServiceLocator.getInstance(ServiceLocatorTest.class.getClassLoader());
	}

	@Test
	public void testKnownLanguageMetaInf() {
		setupServiceLocatorWithMockMarkupLanguage(true);
		assertKnownMarkupLanguage();
	}

	@Test
	public void testKnownLanguageServices() {
		setupServiceLocatorWithMockMarkupLanguage(false);
		assertKnownMarkupLanguage();
	}

	@Test
	public void testKnownLanguageProviderMetaInf() {
		setupServiceLocatorWithMockMarkupLanguageProvider(true);
		assertKnownMarkupLanguage();
	}

	@Test
	public void testKnownLanguageProviderServices() {
		setupServiceLocatorWithMockMarkupLanguageProvider(false);
		assertKnownMarkupLanguage();
	}

	@Test
	public void testLoadClassNotFound() throws MalformedURLException {
		final URL url = new URL("file://example");
		final AtomicBoolean wasThrown = new AtomicBoolean();
		locator = new ServiceLocator(ServiceLocatorTest.class.getClassLoader()) {
			@Override
			protected Class<?> loadClass(ResourceDescriptor resource, String className) throws ClassNotFoundException {
				wasThrown.set(true);
				throw new ClassNotFoundException(className);
			}

			@Override
			protected List<ResourceDescriptor> discoverServiceResources() {
				return Lists.newArrayList(new ResourceDescriptor(url));
			}

			@Override
			protected List<String> readServiceClassNames(URL url) {
				return Lists.newArrayList("test.TestLanguage");
			}
		};
		Set<MarkupLanguage> languages = locator.getAllMarkupLanguages();
		assertTrue(wasThrown.get());
		assertNotNull(languages);
		assertTrue(languages.isEmpty());
	}

	protected void assertKnownMarkupLanguage() {
		MarkupLanguage markupLanguage = locator.getMarkupLanguage(MockMarkupLanguage.class.getSimpleName());
		assertNotNull(markupLanguage);
		assertEquals(new MockMarkupLanguage().getName(), markupLanguage.getName());
	}

	protected void setupServiceLocatorWithMockMarkupLanguage(boolean metaInf) {
		try {
			ClassLoader classLoader = mock(ClassLoader.class);
			Collection<URL> resources = Lists.newArrayList(new URL("file:" + MockMarkupLanguage.class.getName()));

			Enumeration<Object> empty = Collections.enumeration(Collections.emptyList());
			doReturn(empty).when(classLoader).getResources(any(String.class));
			doReturn(metaInf ? Collections.enumeration(resources) : empty).when(classLoader).getResources(
					eq("META-INF/services/" + MarkupLanguage.class.getName()));
			doReturn(!metaInf ? Collections.enumeration(resources) : empty).when(classLoader).getResources(
					eq("services/" + MarkupLanguage.class.getName()));
			doReturn(MockMarkupLanguage.class).when(classLoader).loadClass(MockMarkupLanguage.class.getName());

			locator = new ServiceLocator(classLoader) {
				@Override
				protected List<String> readServiceClassNames(URL url) {
					return super.readServiceClassNames(new ByteArrayInputStream(MockMarkupLanguage.class.getName()
							.getBytes(Charsets.UTF_8)));
				}
			};
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	protected void setupServiceLocatorWithMockMarkupLanguageProvider(boolean metaInf) {
		try {
			ClassLoader classLoader = mock(ClassLoader.class);
			Collection<URL> resources = Lists.newArrayList(new URL("file:" + MockMarkupLanguageProvider.class.getName()));

			Enumeration<Object> empty = Collections.enumeration(Collections.emptyList());
			doReturn(empty).when(classLoader).getResources(any(String.class));
			doReturn(metaInf ? Collections.enumeration(resources) : empty).when(classLoader).getResources(
					eq("META-INF/services/" + MarkupLanguageProvider.class.getName()));
			doReturn(!metaInf ? Collections.enumeration(resources) : empty).when(classLoader).getResources(
					eq("services/" + MarkupLanguageProvider.class.getName()));
			doReturn(MockMarkupLanguageProvider.class).when(classLoader).loadClass(
					MockMarkupLanguageProvider.class.getName());
			locator = new ServiceLocator(classLoader) {
				@Override
				protected List<String> readServiceClassNames(URL url) {
					return super.readServiceClassNames(new ByteArrayInputStream(
							MockMarkupLanguageProvider.class.getName().getBytes(Charsets.UTF_8)));
				}
			};
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	@Test
	public void testUnknownLanguage() {
		setupServiceLocatorWithMockMarkupLanguage(true);
		thrown.expect(IllegalArgumentException.class);
		locator.getMarkupLanguage("No Such Language asdlkfjal;sjdf");
	}

	@Test
	public void testFQN() {
		MarkupLanguage markupLanguage = locator.getMarkupLanguage(MockMarkupLanguage.class.getName());
		assertNotNull(markupLanguage);
		assertEquals(MockMarkupLanguage.class, markupLanguage.getClass());
	}

	@Test
	public void getMarkupLanguageNull() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Must provide a languageName");
		locator.getMarkupLanguage(null);
	}

	@Test
	public void getMarkupLanguageUnknown() {
		setupServiceLocatorWithMockMarkupLanguage(true);
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Cannot load markup language 'UnknownLanguage'. Known markup languages are 'MockMarkupLanguage'");
		locator.getMarkupLanguage("UnknownLanguage");
	}

	@Test
	public void readServicesFileNone() {
		List<String> names = locator.readServiceClassNames(createInput(""));
		assertNotNull(names);
		assertTrue(names.isEmpty());
	}

	@Test
	public void readServicesFileCommentsAndEmptyLinesOnly() {
		List<String> names = locator.readServiceClassNames(createInput("#" + Object.class.getName() + "\n\n\n\n#last"));
		assertNotNull(names);
		assertTrue(names.isEmpty());
	}

	@Test
	public void readServicesFile() {
		List<String> names = locator.readServiceClassNames(createInput(Object.class.getName() + "\n"
				+ String.class.getName() + " # trailing comment"));
		assertNotNull(names);
		assertEquals(2, names.size());
		assertEquals(Object.class.getName(), names.get(0));
		assertEquals(String.class.getName(), names.get(1));
	}

	@Test
	public void getClasspathServiceResourceNames() {
		List<String> names = locator.getClasspathServiceResourceNames();
		assertNotNull(names);
		assertTrue(names.contains("META-INF/services/" + MarkupLanguage.class.getName()));
		assertTrue(names.contains("services/" + MarkupLanguage.class.getName()));
	}

	private InputStream createInput(String content) {
		return new ByteArrayInputStream(content.getBytes(Charsets.UTF_8));
	}
}
