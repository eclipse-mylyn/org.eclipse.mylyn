/*******************************************************************************
 * Copyright (c) 2017 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.reviews.core.model.IChange;
import org.junit.Test;

import com.google.gerrit.common.data.ChangeInfo;
import com.google.gerrit.reviewdb.Change.Id;

@SuppressWarnings("restriction")
public class GerritReviewRemoteFactoryTest {
	@Test
	public void isDependenciesDifferent() throws Exception {
		GerritReviewRemoteFactory factory = new GerritReviewRemoteFactory(mock(GerritRemoteFactoryProvider.class));

		assertFalse(factory.isDependenciesDifferent(emptyList(), emptyList()));
		assertFalse(factory.isDependenciesDifferent(changes(1), changeInfos(1)));
		assertFalse(factory.isDependenciesDifferent(changes(1, 2, 3), changeInfos(3, 1, 2)));

		assertTrue(factory.isDependenciesDifferent(changes(1), emptyList()));
		assertTrue(factory.isDependenciesDifferent(emptyList(), changeInfos(1)));
		assertTrue(factory.isDependenciesDifferent(changes(1), changeInfos(2)));
		assertTrue(factory.isDependenciesDifferent(changes(1), changeInfos(2, 3)));
		assertTrue(factory.isDependenciesDifferent(changes(1, 2), changeInfos(2, 3)));
	}

	private static List<IChange> changes(int... ids) {
		List<IChange> result = new ArrayList<>(ids.length);
		for (int id : ids) {
			IChange change = mock(IChange.class);
			when(change.getId()).thenReturn(Integer.toString(id));
			result.add(change);
		}
		return result;
	}

	private static List<ChangeInfo> changeInfos(int... ids) {
		List<ChangeInfo> result = new ArrayList<>(ids.length);
		for (int id : ids) {
			ChangeInfo changeInfo = mock(ChangeInfo.class);
			when(changeInfo.getId()).thenReturn(new Id(id));
			result.add(changeInfo);
		}
		return result;
	}
}
