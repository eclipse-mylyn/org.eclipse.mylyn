/**
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.core.model;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

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
		List<ILineLocation> orderable = new ArrayList<ILineLocation>();
		orderable.add(l1);
		orderable.add(l2);
		Collections.sort(orderable, IIndexed.COMPARATOR);
		assertThat(orderable.get(0), sameInstance(l2));
		assertThat(orderable.get(1), sameInstance(l1));
	}

	@Test
	public void testTopic() {
		ITopic t1 = ReviewsFactory.eINSTANCE.createTopic();
		t1.getLocations().add(l1);
		ITopic t2 = ReviewsFactory.eINSTANCE.createTopic();
		t2.getLocations().add(l2);
		List<ITopic> orderable = new ArrayList<ITopic>();
		orderable.add(t1);
		orderable.add(t2);

		Collections.sort(orderable, IIndexed.COMPARATOR);
		assertThat(orderable.get(0), sameInstance(t2));
		assertThat(orderable.get(1), sameInstance(t1));
	}

	@Test
	public void testComment() {
		IComment c1 = ReviewsFactory.eINSTANCE.createComment();
		IComment c2 = ReviewsFactory.eINSTANCE.createComment();
		ITopic t1 = ReviewsFactory.eINSTANCE.createTopic();
		t1.getComments().add(c1);
		t1.getLocations().add(l1);
		ITopic t2 = ReviewsFactory.eINSTANCE.createTopic();
		t2.getComments().add(c2);
		t2.getLocations().add(l2);
		List<IComment> orderable = new ArrayList<IComment>();
		orderable.add(c1);
		orderable.add(c2);

		Collections.sort(orderable, IIndexed.COMPARATOR);
		assertThat(orderable.get(0), sameInstance(c2));
		assertThat(orderable.get(1), sameInstance(c1));
	}
}
