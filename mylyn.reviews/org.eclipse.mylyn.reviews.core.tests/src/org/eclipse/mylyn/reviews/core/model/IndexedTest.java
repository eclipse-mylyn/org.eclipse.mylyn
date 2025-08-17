/**
 * Copyright (c) 2012, 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.core.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.reviews.internal.core.model.ReviewsFactory;
import org.junit.Before;
import org.junit.Test;

public class IndexedTest {

	private ILineLocation l1;

	private ILineLocation l2;

	@Before
	public void setup() {
		l1 = ReviewsFactory.eINSTANCE.createLineLocation();
		ILineRange l1r1 = ReviewsFactory.eINSTANCE.createLineRange();
		l1r1.setStart(100);
		l1r1.setEnd(100);
		l1.getRanges().add(l1r1);
		ILineRange l1r2 = ReviewsFactory.eINSTANCE.createLineRange();
		l1r2.setStart(200);
		l1r2.setEnd(200);
		l1.getRanges().add(l1r2);
		assertThat(l1.getRangeMin(), is(100));
		assertThat(l1.getRangeMax(), is(200));

		l2 = ReviewsFactory.eINSTANCE.createLineLocation();
		ILineRange l2r1 = ReviewsFactory.eINSTANCE.createLineRange();
		l2r1.setStart(10000);
		l2r1.setEnd(10000);
		l2.getRanges().add(l2r1);
		ILineRange l2r2 = ReviewsFactory.eINSTANCE.createLineRange();
		l2r2.setStart(30);
		l2r2.setEnd(30);
		l2.getRanges().add(l2r2);
		assertThat(l2.getRangeMin(), is(30));
		assertThat(l2.getRangeMax(), is(10000));
	}

	@Test
	public void testLineLocation() {
		List<ILineLocation> orderable = new ArrayList<>();
		orderable.add(l1);
		orderable.add(l2);
		Collections.sort(orderable, IIndexed.COMPARATOR);
		assertThat(orderable.get(0), sameInstance(l2));
		assertThat(orderable.get(1), sameInstance(l1));
	}

	@Test
	public void testComment() {
		IComment c1 = ReviewsFactory.eINSTANCE.createComment();
		IComment c2 = ReviewsFactory.eINSTANCE.createComment();
		c1.getLocations().add(l1);
		c2.getLocations().add(l2);
		List<IComment> orderable = new ArrayList<>();
		orderable.add(c1);
		orderable.add(c2);

		Collections.sort(orderable, IIndexed.COMPARATOR);
		assertThat(orderable.get(0), sameInstance(c2));
		assertThat(orderable.get(1), sameInstance(c1));
	}
}
