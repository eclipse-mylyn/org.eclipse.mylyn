/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.identity.core.spi;

import java.io.Serializable;

import org.eclipse.mylyn.commons.identity.core.IIdentity;
import org.eclipse.mylyn.commons.identity.core.IProfile;

/**
 * @author Steffen Pingel
 * @since 0.8
 */
public final class Profile implements IProfile, Serializable {

	private static final long serialVersionUID = -1079729573911113939L;

	private String city;

	private String country;

	private String email;

	private final IIdentity identity;

	private String name;

	private int timeZoneOffset;

	public Profile(IIdentity identity) {
		this.identity = identity;
	}

	@Override
	public String getCity() {
		return city;
	}

	@Override
	public String getCountry() {
		return country;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public IIdentity getIdentity() {
		return identity;
	}

	@Override
	public String getName() {
		return name;
	}

	public int getTimeZoneOffset() {
		return timeZoneOffset;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTimeZoneOffset(int timeZoneOffset) {
		this.timeZoneOffset = timeZoneOffset;
	}

}
