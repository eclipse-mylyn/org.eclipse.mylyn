/*******************************************************************************
 * Copyright (c) 2015 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

public class BugzillaRestGsonUtil {

	private class RemoveAddStringHelper {
		Set<String> add;

		Set<String> remove;

		public RemoveAddStringHelper(Set<String> removeSet, Set<String> addSet) {
			add = new HashSet<String>(addSet);
			remove = new HashSet<String>(removeSet);
			if (remove.contains("")) { //$NON-NLS-1$
				remove.remove(""); //$NON-NLS-1$
			}
			if (add.contains("")) { //$NON-NLS-1$
				add.remove(""); //$NON-NLS-1$
			}
			Set<String> intersection = Sets.intersection(addSet, removeSet);
			remove.removeAll(intersection);
			add.removeAll(intersection);
			if (remove.isEmpty()) {
				remove = null;
			}
			if (add.isEmpty()) {
				add = null;
			}
		}
	}

	private static Gson gson = new Gson();

	public static String convertString2GSonString(String str) {
		str = str.replace("\"", "\\\"").replace("\n", "\\\n"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
		StringBuffer ostr = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if ((ch >= 0x0020) && (ch <= 0x007e)) {
				ostr.append(ch);
			} else {
				ostr.append("\\u"); //$NON-NLS-1$
				String hex = Integer.toHexString(str.charAt(i) & 0xFFFF);
				for (int j = 0; j < 4 - hex.length(); j++) {
					ostr.append("0"); //$NON-NLS-1$
				}
				ostr.append(hex.toLowerCase());
			}
		}
		return (new String(ostr));
	}

	private static BugzillaRestGsonUtil instance;

	public static BugzillaRestGsonUtil getDefault() {
		if (instance == null) {
			instance = new BugzillaRestGsonUtil();
		}
		return instance;
	}

	public static void buildArrayFromHash(JsonWriter out, String id, Set<String> setNew) throws IOException {
		if (!setNew.isEmpty()) {
			out.name(id).beginArray();
			for (String string : setNew) {
				out.value(string);
			}
			out.endArray();
		}
	}

	public void buildAddRemoveHash(JsonWriter out, String id, Set<String> setOld, Set<String> setNew)
			throws IOException {
		RemoveAddStringHelper test = new RemoveAddStringHelper(setOld, setNew);
		out.name(id);
		gson.toJson(test, RemoveAddStringHelper.class, out);
	}
}