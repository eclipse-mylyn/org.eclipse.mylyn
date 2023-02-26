/*******************************************************************************
 * Copyright (c) 2016 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.rest.core.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestFlagMapper;
import org.eclipse.mylyn.internal.bugzilla.rest.core.IBugzillaRestConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

public class BugzillaRestFlagMapperTest {

	private TaskData mockTestData;

	@Before
	public void setUp() throws Exception {
		TaskAttributeMapper mapper = new TaskAttributeMapper(new TaskRepository("", ""));

		mockTestData = new TaskData(mapper, "", "", "");
	}

	@Test
	public void testReadFromJson() throws IOException {
		String jsonElement = IOUtils.toString(CommonTestUtil.getResource(this, "testdata/flag.json"));

		BugzillaRestFlagMapper flagMapper = new Gson().fromJson(jsonElement, BugzillaRestFlagMapper.class);
		assertThat(flagMapper.getCreationDate(), is("2016-10-22T14:19:13Z"));
		assertNull(flagMapper.getDescription());
		assertThat(flagMapper.getModificationDate(), is("2016-10-22T14:19:33Z"));
		assertThat(flagMapper.getName(), is("BugFlag1"));
		assertThat(flagMapper.getNumber(), is(11));
		assertNull(flagMapper.getRequestee());
		assertThat(flagMapper.getSetter(), is("tests@mylyn.eclipse.org"));
		assertThat(flagMapper.getState(), is("-"));
		assertThat(flagMapper.getTypeId(), is(1));
	}

	@Test
	public void testReadFromJson1() throws IOException {
		String jsonElement = IOUtils.toString(CommonTestUtil.getResource(this, "testdata/flag1.json"));

		BugzillaRestFlagMapper flagMapper = new Gson().fromJson(jsonElement, BugzillaRestFlagMapper.class);
		assertThat(flagMapper.getCreationDate(), is("2016-10-22T14:19:13Z"));
		assertThat(flagMapper.getDescription(), is("Description of FlagType 1"));
		assertThat(flagMapper.getModificationDate(), is("2016-10-22T14:19:33Z"));
		assertThat(flagMapper.getName(), is("BugFlag1"));
		assertThat(flagMapper.getNumber(), is(11));
		assertThat(flagMapper.getRequestee(), is("admin@mylyn.eclipse.org"));
		assertThat(flagMapper.getSetter(), is("tests@mylyn.eclipse.org"));
		assertThat(flagMapper.getState(), is("?"));
		assertThat(flagMapper.getTypeId(), is(1));
	}

	@Test
	public void testWrite2Json() throws IOException {
		BugzillaRestFlagMapper flagMapper = new BugzillaRestFlagMapper();
		flagMapper.setCreationDate("2016-10-22T14:19:13Z");
		flagMapper.setDescription(null);
		flagMapper.setModificationDate("2016-10-22T14:19:33Z");
		flagMapper.setName("BugFlag1");
		flagMapper.setNumber(11);
		flagMapper.setRequestee(null);
		flagMapper.setSetter("tests@mylyn.eclipse.org");
		flagMapper.setState("-");
		flagMapper.setTypeId(1);
		assertEquals(IOUtils.toString(CommonTestUtil.getResource(this, "testdata/flag.json")),
				new Gson().toJson(flagMapper));
	}

	@Test
	public void testWrite2Json1() throws IOException {
		BugzillaRestFlagMapper flagMapper = new BugzillaRestFlagMapper();
		flagMapper.setCreationDate("2016-10-22T14:19:13Z");
		flagMapper.setDescription("Description of FlagType 1");
		flagMapper.setModificationDate("2016-10-22T14:19:33Z");
		flagMapper.setName("BugFlag1");
		flagMapper.setNumber(11);
		flagMapper.setRequestee("admin@mylyn.eclipse.org");
		flagMapper.setSetter("tests@mylyn.eclipse.org");
		flagMapper.setState("?");
		flagMapper.setTypeId(1);
		assertEquals(IOUtils.toString(CommonTestUtil.getResource(this, "testdata/flag1.json")),
				new Gson().toJson(flagMapper));
	}

	@Test
	public void testApplyToTaskAttribute() throws IOException {
		String jsonElement = IOUtils.toString(CommonTestUtil.getResource(this, "testdata/flag.json"));

		BugzillaRestFlagMapper flagMapper = new Gson().fromJson(jsonElement, BugzillaRestFlagMapper.class);
		TaskAttribute taskAttribute = mockTestData.getRoot()
				.createAttribute(IBugzillaRestConstants.KIND_FLAG_TYPE + "11");
		flagMapper.applyTo(taskAttribute);
		assertEquals(IOUtils.toString(CommonTestUtil.getResource(this, "testdata/flag.txt")), taskAttribute.toString());
	}

	@Test
	public void testApplyToTaskAttribute1() throws IOException {
		String jsonElement = IOUtils.toString(CommonTestUtil.getResource(this, "testdata/flag1.json"));

		BugzillaRestFlagMapper flagMapper = new Gson().fromJson(jsonElement, BugzillaRestFlagMapper.class);
		TaskAttribute taskAttribute = mockTestData.getRoot()
				.createAttribute(IBugzillaRestConstants.KIND_FLAG_TYPE + "11");
		flagMapper.applyTo(taskAttribute);
		assertEquals(IOUtils.toString(CommonTestUtil.getResource(this, "testdata/flag1.txt")),
				taskAttribute.toString());
	}

	@Test
	public void testCreateFromTaskAttribute() throws IOException {
		String jsonElement = IOUtils.toString(CommonTestUtil.getResource(this, "testdata/flag.json"));

		BugzillaRestFlagMapper flagMapper = new Gson().fromJson(jsonElement, BugzillaRestFlagMapper.class);
		TaskAttribute taskAttribute = mockTestData.getRoot()
				.createAttribute(IBugzillaRestConstants.KIND_FLAG_TYPE + "11");
		flagMapper.applyTo(taskAttribute);
		assertEquals(IOUtils.toString(CommonTestUtil.getResource(this, "testdata/flag.txt")), taskAttribute.toString());

		flagMapper = BugzillaRestFlagMapper.createFrom(taskAttribute);
		assertEquals(IOUtils.toString(CommonTestUtil.getResource(this, "testdata/flag.json")),
				new Gson().toJson(flagMapper));
	}

	@Test
	public void testCreateFromTaskAttribute1() throws IOException {
		String jsonElement = IOUtils.toString(CommonTestUtil.getResource(this, "testdata/flag1.json"));

		BugzillaRestFlagMapper flagMapper = new Gson().fromJson(jsonElement, BugzillaRestFlagMapper.class);
		TaskAttribute taskAttribute = mockTestData.getRoot()
				.createAttribute(IBugzillaRestConstants.KIND_FLAG_TYPE + "11");
		flagMapper.applyTo(taskAttribute);
		assertEquals(IOUtils.toString(CommonTestUtil.getResource(this, "testdata/flag1.txt")),
				taskAttribute.toString());

		flagMapper = BugzillaRestFlagMapper.createFrom(taskAttribute);
		assertEquals(IOUtils.toString(CommonTestUtil.getResource(this, "testdata/flag1.json")),
				new Gson().toJson(flagMapper));
	}

}
