/*******************************************************************************
 * Copyright (c) 2009 Steffen Pingel and others. 
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.model;

/**
 * The version of the Trac repository is defined by three numbers: epoch.major.minor. The default value is
 * <code>-1.-1.-1</code> which indicates the version is not known.
 * 
 * <pre>
 * [0.0.0]         Trac 0.9.6
 * [0.0.1, 1.0.0)  Trac 0.10
 * [1.0.0, 2.0.0)  Trac 0.11
 * </pre>
 * 
 * @author Steffen Pingel
 */
public class TracRepositoryInfo {

	private final int apiEpoch;

	private final int apiMajor;

	private final int apiMinor;

	private String version;

	public TracRepositoryInfo() {
		this(null);
	}

	public TracRepositoryInfo(int apiEpoch, int apiMajor, int apiMinor) {
		this(apiEpoch, apiMajor, apiMinor, null);
	}

	public TracRepositoryInfo(int apiEpoch, int apiMajor, int apiMinor, String version) {
		this.apiEpoch = apiEpoch;
		this.apiMajor = apiMajor;
		this.apiMinor = apiMinor;
		this.version = version;
	}

	public TracRepositoryInfo(String version) {
		this(-1, -1, -1, version);
	}

	public int getApiEpoch() {
		return apiEpoch;
	}

	public int getApiMajor() {
		return apiMajor;
	}

	public int getApiMinor() {
		return apiMinor;
	}

	public String getVersion() {
		return version;
	}

	public boolean isApiVersion(int epoch, int major, int minor) {
		return apiEpoch == epoch && apiMajor == major && apiMinor == minor;
	}

	public boolean isApiVersionOrHigher(int epoch, int major, int minor) {
		return apiEpoch > epoch //
				|| apiEpoch == epoch && (apiMajor > major //
				|| apiMajor == major && apiMinor >= minor);
	}

	public boolean isApiVersionOrSmaller(int epoch, int major, int minor) {
		return apiEpoch < epoch //
				|| apiEpoch == epoch && (apiMajor < major //
				|| apiMajor == major && apiMinor <= minor);
	}

	public boolean isStale() {
		return apiEpoch == -1 || apiMajor == -1 || apiMinor == -1;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(apiEpoch);
		sb.append("."); //$NON-NLS-1$
		sb.append(apiMajor);
		sb.append("."); //$NON-NLS-1$
		sb.append(apiMinor);
		if (version != null) {
			sb.append(" ("); //$NON-NLS-1$
			sb.append(version);
			sb.append(")"); //$NON-NLS-1$
		}
		return sb.toString();
	}

}
