/*******************************************************************************
 * Copyright (c) 2023 George Lindholm and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.core.collections;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections4.multimap.AbstractListValuedMap;

/**
 * MultiValuedMap that preserves insert order of keys.
 * <p/>
 * Mainly here to duplicate guavas Multimaps.index() behaviour
 *
 * @author George Lindholm
 * @since 4.1
 */
public final class LinkedHashMappArrayListValuedHashMap<K, V> extends AbstractListValuedMap<K, V> {

	public LinkedHashMappArrayListValuedHashMap() {
		super(new LinkedHashMap<>());
	}

	@Override
	protected List<V> createCollection() {
		return new ArrayList<>();
	}
}
