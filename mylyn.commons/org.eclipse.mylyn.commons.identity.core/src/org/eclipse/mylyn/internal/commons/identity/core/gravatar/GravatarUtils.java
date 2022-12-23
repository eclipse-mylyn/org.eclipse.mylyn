/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc. and others
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *    Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.identity.core.gravatar;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;

import org.eclipse.core.runtime.IAdaptable;

/**
 * Gravatar utililites.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public abstract class GravatarUtils {

	private static String digest(String value) {
		String hashed = null;
		try {
			byte[] input = value.getBytes(IGravatarConstants.CHARSET);
			byte[] digested = MessageDigest.getInstance(IGravatarConstants.HASH_ALGORITHM).digest(input);
			hashed = new BigInteger(1, digested).toString(16);
			int padding = IGravatarConstants.HASH_LENGTH - hashed.length();
			if (padding > 0) {
				char[] zeros = new char[padding];
				Arrays.fill(zeros, '0');
				hashed = new String(zeros) + hashed;
			}
		} catch (NoSuchAlgorithmException e) {
			hashed = null;
		} catch (UnsupportedEncodingException e) {
			hashed = null;
		}
		return hashed;
	}

	/**
	 * Get hash for object by attempting to adapt it to an {@link IGravatarHashProvider} and fall back on
	 * {@link Object#toString()} value if adaptation fails and {@link Object#toString()} is or can be transformed into a
	 * valid hash.
	 * 
	 * @param element
	 * @return hash
	 */
	public String getAdaptedHash(Object element) {
		if (element == null) {
			return null;
		}

		String hash = null;
		IGravatarHashProvider provider = null;
		if (element instanceof IGravatarHashProvider) {
			provider = (IGravatarHashProvider) element;
		} else if (element instanceof IAdaptable) {
			provider = (IGravatarHashProvider) ((IAdaptable) element).getAdapter(IGravatarHashProvider.class);
		}
		if (provider != null) {
			hash = provider.getGravatarHash();
		} else {
			String potentialHash = element.toString();
			if (isValidHash(potentialHash)) {
				hash = potentialHash;
			} else {
				hash = getHash(potentialHash);
			}
		}
		return hash;
	}

	/**
	 * Is the specified string a valid graavatar hash?
	 * 
	 * @param hash
	 * @return true if valid hash, false otherwise
	 */
	public static boolean isValidHash(String hash) {
		return hash != null && hash.length() == IGravatarConstants.HASH_LENGTH
				&& IGravatarConstants.HASH_PATTERN.matcher(hash).matches();
	}

	/**
	 * Get gravatar hash for specified e-mail address
	 * 
	 * @param email
	 * @return hash
	 */
	public static String getHash(String email) {
		String hash = null;
		if (email != null) {
			email = email.trim().toLowerCase(Locale.US);
			if (email.length() > 0) {
				hash = digest(email);
			}
		}
		return hash;
	}

	public static boolean isValidEmail(String alias) {
		return alias != null && alias.contains("@"); //$NON-NLS-1$
	}

}
