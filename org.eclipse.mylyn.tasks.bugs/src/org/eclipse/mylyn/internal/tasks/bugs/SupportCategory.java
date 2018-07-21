/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.tasks.bugs.IProvider;

public class SupportCategory extends AbstractSupportElement {

	private static final int DEFAULT_WEIGHT = 1000;

	private List<IProvider> providers;

	private int weight;

	public SupportCategory() {
		setWeight(DEFAULT_WEIGHT);
	}

	public void add(IProvider provider) {
		if (providers == null) {
			providers = new ArrayList<IProvider>();
		}
		providers.add(provider);
	}

	public void remove(IProvider provider) {
		if (providers != null) {
			providers.remove(provider);
		}
	}

	public List<IProvider> getProviders() {
		if (providers == null) {
			return Collections.emptyList();
		}
		return new ArrayList<IProvider>(providers);
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

}
