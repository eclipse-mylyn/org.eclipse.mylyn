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

package org.eclipse.mylyn.gerrit.tests.core.client.rest;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.JSonSupport;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ChangeInfo;
import org.junit.Test;

import com.google.gerrit.reviewdb.Change;

public class ChangeInfoTest extends TestCase {
	@Test
	public void testFromEmptyJson() throws Exception {
		ChangeInfo changeInfo = parseFile("testdata/EmptyWithMagic.json");

		assertNotNull(changeInfo);
		assertNull(changeInfo.getKind());
		assertNull(changeInfo.getId());
		assertNull(changeInfo.getProject());
		assertNull(changeInfo.getBranch());
		assertNull(changeInfo.getChangeId());
		assertNull(changeInfo.getSubject());
		assertNull(changeInfo.getStatus());
		assertNull(changeInfo.getCreated());
		assertNull(changeInfo.getUpdated());
		assertEquals(false, changeInfo.isReviewed());
		assertEquals(false, changeInfo.isMergeable());
		assertNull(changeInfo.getOwner());
	}

	@Test
	public void testFromInvalid() throws Exception {
		ChangeInfo changeInfo = parseFile("testdata/InvalidWithMagic.json");

		assertNotNull(changeInfo);
		assertNotNull(changeInfo);
		assertNull(changeInfo.getKind());
		assertNull(changeInfo.getId());
		assertNull(changeInfo.getProject());
		assertNull(changeInfo.getBranch());
		assertNull(changeInfo.getChangeId());
		assertNull(changeInfo.getSubject());
		assertNull(changeInfo.getStatus());
		assertNull(changeInfo.getCreated());
		assertNull(changeInfo.getUpdated());
		assertEquals(false, changeInfo.isReviewed());
		assertEquals(false, changeInfo.isMergeable());
		assertNull(changeInfo.getOwner());
	}

	@Test
	public void testCodeReviewMinusOne() throws Exception {
		ChangeInfo changeInfo = parseFile("testdata/ChangeInfo_abandoned.json");

		assertNotNull(changeInfo);
		assertEquals(changeInfo.getKind(), "gerritcodereview#change");
		assertEquals(changeInfo.getId(), "myProject~master~I8473b95934b5732ac55d26311a706c9c2bde9940");
		assertEquals(changeInfo.getProject(), "myProject");
		assertEquals(changeInfo.getBranch(), "master");
		assertEquals("I8473b95934b5732ac55d26311a706c9c2bde9940", changeInfo.getChangeId());
		assertEquals("Implementing Feature X", changeInfo.getSubject());
		assertEquals(Change.Status.ABANDONED, changeInfo.getStatus());
		assertEquals(timestamp("2013-02-01 09:59:32.126"), changeInfo.getCreated());
		assertEquals(timestamp("2013-02-21 11:16:36.775"), changeInfo.getUpdated());
		assertEquals(true, changeInfo.isReviewed());
		assertEquals(true, changeInfo.isMergeable());
		AccountInfo changeOwner = changeInfo.getOwner();
		assertNotNull(changeOwner);
		assertEquals("John Doe", changeOwner.getName());
		assertNull(changeOwner.getEmail());
		assertNull(changeOwner.getUsername());
		assertEquals(-1, changeOwner.getId());
	}

	private static Timestamp timestamp(String date) throws ParseException {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		cal.setTime(sdf.parse(date));
		cal.add(Calendar.MILLISECOND, TimeZone.getDefault().getRawOffset());
		return new Timestamp(cal.getTimeInMillis());
	}

	private ChangeInfo parseFile(String path) throws IOException {
		File file = CommonTestUtil.getFile(this, path);
		String content = CommonTestUtil.read(file);
		return new JSonSupport().parseResponse(content, ChangeInfo.class);
	}
}
