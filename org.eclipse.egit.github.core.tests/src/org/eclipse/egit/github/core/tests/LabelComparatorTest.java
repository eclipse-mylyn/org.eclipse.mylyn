/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.util.LabelComparator;
import org.junit.Test;

/**
 * Unit tests of {@link LabelComparator}
 */
public class LabelComparatorTest {

	/**
	 * Compare labels
	 */
	@Test
	public void compareLabels() {
		LabelComparator cmp = new LabelComparator();
		Label l1 = new Label().setName("a");
		Label l2 = new Label().setName("b");
		assertEquals(-1, cmp.compare(l1, l2));
		assertEquals(0, cmp.compare(l1, l1));
		assertEquals(1, cmp.compare(l2, l1));
	}

}
