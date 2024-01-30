/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Leah Findalter - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.core.collection;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores the type, ID, name, and usage count of a single function that can be stored in the context.
 * 
 * @author Leah Findlater
 * @author Mik Kersten
 */
public class InteractionEventSummary {
	private String type;

	private String name;

	private int usageCount;

	private float interestContribution;

	private String delta;

	private Set<Integer> userIds = new HashSet<>();

	public InteractionEventSummary(String type, String name, int usageCount) {
		this.type = type;
		this.name = name;
		this.usageCount = usageCount;

	}

	public InteractionEventSummary() {
		type = ""; //$NON-NLS-1$
		name = ""; //$NON-NLS-1$
		usageCount = 0;
	}

	public InteractionEventSummary(InteractionEventSummary another) {
		type = another.type;
		name = another.name;
		usageCount = another.usageCount;
		userIds.addAll(another.getUserIds());
	}

	public void combine(InteractionEventSummary another) {
		usageCount = usageCount + another.getUsageCount();
		userIds.addAll(another.getUserIds());
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return Returns the usageCount.
	 */
	public int getUsageCount() {
		return usageCount;
	}

	/**
	 * @param usageCount
	 *            The usageCount to set.
	 */
	public void setUsageCount(int usageCount) {
		this.usageCount = usageCount;
	}

	public float getInterestContribution() {
		return interestContribution;
	}

	public void setInterestContribution(float interestContribution) {
		this.interestContribution = interestContribution;
	}

	public String getDelta() {
		if ("null".equals(delta)) { //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		} else {
			return delta;
		}
	}

	public void setDelta(String delta) {
		this.delta = delta;
	}

	public Set<Integer> getUserIds() {
		return userIds;
	}

	public void setUserIds(Set<Integer> userIds) {
		this.userIds = userIds;
	}

	public void addUserId(int userId) {
		if (!userIds.contains(userId)) {
			userIds.add(userId);
		}
	}
}
