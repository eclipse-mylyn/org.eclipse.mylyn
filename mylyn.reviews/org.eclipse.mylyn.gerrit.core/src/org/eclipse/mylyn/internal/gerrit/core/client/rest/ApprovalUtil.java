/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gerrit.common.data.ApprovalType;
import com.google.gerrit.reviewdb.ApprovalCategory;
import com.google.gerrit.reviewdb.ApprovalCategoryValue;

public final class ApprovalUtil {

	public static final ApprovalType VRIF;

	public static final ApprovalType CRVW;

	public static final ApprovalType IPCL;

	private static final Map<String, ApprovalType> BY_NAME;

	static final Map<String, ApprovalType> BY_ID;

	static {
		ApprovalCategory vrifCategory = new ApprovalCategory(new ApprovalCategory.Id("VRIF"), "Verified"); //$NON-NLS-1$ //$NON-NLS-2$
		vrifCategory.setAbbreviatedName("V"); //$NON-NLS-1$
		vrifCategory.setPosition((short) 0);
		List<ApprovalCategoryValue> vrifValues = new ArrayList<>(3);
		vrifValues.add(
				new ApprovalCategoryValue(new ApprovalCategoryValue.Id(vrifCategory.getId(), (short) -1), "Fails")); //$NON-NLS-1$
		vrifValues.add(
				new ApprovalCategoryValue(new ApprovalCategoryValue.Id(vrifCategory.getId(), (short) 0), "No score")); //$NON-NLS-1$
		vrifValues.add(
				new ApprovalCategoryValue(new ApprovalCategoryValue.Id(vrifCategory.getId(), (short) 1), "Verified")); //$NON-NLS-1$
		VRIF = new ApprovalType(vrifCategory, vrifValues);

		ApprovalCategory crvwCategory = new ApprovalCategory(new ApprovalCategory.Id("CRVW"), "Code Review"); //$NON-NLS-1$ //$NON-NLS-2$
		crvwCategory.setAbbreviatedName("R"); //$NON-NLS-1$
		crvwCategory.setPosition((short) 1);
		List<ApprovalCategoryValue> crvwValues = new ArrayList<>(5);
		crvwValues.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(crvwCategory.getId(), (short) -2),
				"Do not submit")); //$NON-NLS-1$
		crvwValues.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(crvwCategory.getId(), (short) -1),
				"I would prefer that you didn\u0027t submit this")); //$NON-NLS-1$
		crvwValues.add(
				new ApprovalCategoryValue(new ApprovalCategoryValue.Id(crvwCategory.getId(), (short) 0), "No score")); //$NON-NLS-1$
		crvwValues.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(crvwCategory.getId(), (short) 1),
				"Looks good to me, but someone else must approve")); //$NON-NLS-1$
		crvwValues.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(crvwCategory.getId(), (short) 2),
				"Looks good to me, approved")); //$NON-NLS-1$
		CRVW = new ApprovalType(crvwCategory, crvwValues);

		ApprovalCategory ipclCategory = new ApprovalCategory(new ApprovalCategory.Id("IPCL"), "IP Clean"); //$NON-NLS-1$ //$NON-NLS-2$
		ipclCategory.setAbbreviatedName("I"); //$NON-NLS-1$
		ipclCategory.setPosition((short) 2);
		List<ApprovalCategoryValue> ipclValues = new ArrayList<>(3);
		ipclValues.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(ipclCategory.getId(), (short) -1),
				"Unclean IP, do not check in")); //$NON-NLS-1$
		ipclValues.add(
				new ApprovalCategoryValue(new ApprovalCategoryValue.Id(ipclCategory.getId(), (short) 0), "No score")); //$NON-NLS-1$
		ipclValues.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(ipclCategory.getId(), (short) 1),
				"IP review completed")); //$NON-NLS-1$
		IPCL = new ApprovalType(ipclCategory, ipclValues);

		BY_NAME = new HashMap<>(3);
		BY_NAME.put(VRIF.getCategory().getName(), VRIF);
		BY_NAME.put(CRVW.getCategory().getName(), CRVW);
		BY_NAME.put(IPCL.getCategory().getName(), IPCL);

		BY_ID = new HashMap<>(3);
		BY_ID.put(VRIF.getCategory().getId().get(), VRIF);
		BY_ID.put(CRVW.getCategory().getId().get(), CRVW);
		BY_ID.put(IPCL.getCategory().getId().get(), IPCL);
	}

	private static ApprovalCategory findCategoryByName(String name) {
		if (BY_NAME.containsKey(name)) {
			return BY_NAME.get(name).getCategory();
		}
		return null;
	}

	static ApprovalCategory findCategoryByNameWithDash(String name) {
		return findCategoryByName(name.replace('-', ' '));
	}

	static ApprovalCategory.Id findCategoryIdByName(String name) {
		ApprovalCategory cat = findCategoryByName(name);
		if (cat != null) {
			return cat.getId();
		}
		return null;
	}

	static ApprovalCategory.Id findCategoryIdByNameWithDash(String name) {
		return findCategoryIdByName(name.replace('-', ' '));
	}

	static String findCategoryNameById(String id) {
		if (BY_ID.containsKey(id)) {
			return BY_ID.get(id).getCategory().getName();
		}
		return null;
	}

	public static String toNameWithDash(String name) {
		return name.replace(' ', '-');
	}

	static short parseShort(String s) {
		s = s.trim();
		// only Java7 handles a plus sign as indication of a positive value
		if (s.startsWith("+")) { //$NON-NLS-1$
			s = s.substring(1);
		}
		return Short.parseShort(s);
	}

	private ApprovalUtil() {
	}
}
