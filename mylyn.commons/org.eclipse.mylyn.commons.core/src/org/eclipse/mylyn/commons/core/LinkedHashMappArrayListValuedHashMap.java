/*******************************************************************************
 * Copyright (c) 2023 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.core;

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
public class LinkedHashMappArrayListValuedHashMap<K, V> extends AbstractListValuedMap<K, V> {
	public LinkedHashMappArrayListValuedHashMap() {
		super(new LinkedHashMap<>());
	}

	@Override
	protected List<V> createCollection() {
		return new ArrayList<>();
	}
}
