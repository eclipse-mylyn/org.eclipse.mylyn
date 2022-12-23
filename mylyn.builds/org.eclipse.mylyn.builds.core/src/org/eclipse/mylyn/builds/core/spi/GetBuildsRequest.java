/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.core.spi;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.builds.core.IBuildPlan;

/**
 * @author Steffen Pingel
 */
public class GetBuildsRequest {

	public enum Kind {
		ALL, LAST, SELECTED;
	}

	public enum Scope {
		FULL, HISTORY;
	}

	private Set<String> ids;

	private final Kind kind;

	private final IBuildPlan plan;

	private final Scope scope;

	public GetBuildsRequest(IBuildPlan plan, Collection<String> ids, Scope scope) {
		this(plan, Kind.SELECTED, Scope.FULL);
		Assert.isNotNull(ids);
		this.ids = Collections.unmodifiableSet(new HashSet<String>(ids));
	}

	public GetBuildsRequest(IBuildPlan plan, Kind kind) {
		this(plan, kind, Scope.FULL);
	}

	public GetBuildsRequest(IBuildPlan plan, Kind kind, Scope scope) {
		Assert.isNotNull(kind);
		Assert.isNotNull(plan);
		Assert.isNotNull(scope);
		this.kind = kind;
		this.plan = plan;
		this.scope = scope;
	}

	public Set<String> getIds() {
		return ids;
	}

	public Kind getKind() {
		return kind;
	}

	public IBuildPlan getPlan() {
		return plan;
	}

	public Scope getScope() {
		return scope;
	}

}
