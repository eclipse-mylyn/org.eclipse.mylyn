/*******************************************************************************
 * Copyright (c) 2015 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.PutUpdateResult;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

public class BugzillaRestPutUpdateTask extends BugzillaRestPutRequest<PutUpdateResult> {
	private final TaskData taskData;

	private static final List<String> legalUpdateAttributes = Stream.of( //
			BugzillaRestTaskSchema.getDefault().PRODUCT.getKey(), //
			BugzillaRestTaskSchema.getDefault().COMPONENT.getKey(), //
			BugzillaRestTaskSchema.getDefault().SUMMARY.getKey(), //
			BugzillaRestTaskSchema.getDefault().VERSION.getKey(), //
			BugzillaRestTaskSchema.getDefault().DESCRIPTION.getKey(), //
			BugzillaRestTaskSchema.getDefault().OS.getKey(), //
			BugzillaRestTaskSchema.getDefault().PLATFORM.getKey(), //
			BugzillaRestTaskSchema.getDefault().PRIORITY.getKey(), //
			BugzillaRestTaskSchema.getDefault().SEVERITY.getKey(), //
			BugzillaRestTaskSchema.getDefault().ALIAS.getKey(), //
			BugzillaRestTaskSchema.getDefault().ASSIGNED_TO.getKey(), //
			BugzillaRestTaskSchema.getDefault().QA_CONTACT.getKey(), //
			TaskAttribute.OPERATION, //
			BugzillaRestTaskSchema.getDefault().TARGET_MILESTONE.getKey(), //
			BugzillaRestTaskSchema.getDefault().NEW_COMMENT.getKey(), //
			"resolutionInput", //$NON-NLS-1$
			BugzillaRestTaskSchema.getDefault().RESOLUTION.getKey(), //
			BugzillaRestTaskSchema.getDefault().DUPE_OF.getKey(), //
			BugzillaRestTaskSchema.getDefault().BLOCKS.getKey(), //
			BugzillaRestTaskSchema.getDefault().DEPENDS_ON.getKey(), //
			BugzillaRestTaskSchema.getDefault().KEYWORDS.getKey(), //
			BugzillaRestTaskSchema.getDefault().RESET_QA_CONTACT.getKey(), //
			BugzillaRestTaskSchema.getDefault().RESET_ASSIGNED_TO.getKey()) //
			.collect( //
					Collectors.collectingAndThen( //
							Collectors.toList(), Collections::unmodifiableList));

	public BugzillaRestPutUpdateTask(CommonHttpClient client, @NonNull TaskData taskData,
			@NonNull Set<TaskAttribute> oldAttributes) {
		super(client, "/bug/" + taskData.getTaskId(), false, oldAttributes); //$NON-NLS-1$
		Assert.isNotNull(taskData);
		this.taskData = taskData;
	}

	public static String convert(String str) {
		str = str.replace("\"", "\\\"").replace("\n", "\\\n"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
		StringBuffer ostr = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch >= 0x0020 && ch <= 0x007e) {
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
		return new String(ostr);
	}

	@Override
	protected PutUpdateResult parseFromJson(InputStreamReader in) {
		TypeToken<PutUpdateResult> type = new TypeToken<>() {
		};
		return new Gson().fromJson(in, type.getType());
	}

	@Override
	public void addPrefixToOutput(JsonWriter out) throws IOException {
		// not needed
	}

	@Override
	public void addAttributeToOutput(JsonWriter out, TaskAttribute element) throws IOException {
		TaskAttribute taskAttribute = taskData.getRoot().getAttribute(element.getId());
		String id = taskAttribute.getId();
		String value = BugzillaRestGsonUtil.convertString2GSonString(taskAttribute.getValue());
		if ((legalUpdateAttributes.contains(id) || id.startsWith("cf_")) && value != null) { //$NON-NLS-1$
			id = BugzillaRestTaskSchema.getFieldNameFromAttributeName(id);
			if (id.equals("status")) { //$NON-NLS-1$
				if (value.equals("duplicate")) { //$NON-NLS-1$
					TaskAttribute res = element.getParentAttribute()
							.getAttribute(BugzillaRestTaskSchema.getDefault().RESOLUTION.getKey());
					if (!taskAttributes.contains(res)) {
						out.name("resolution").value("DUPLICATE"); //$NON-NLS-1$ //$NON-NLS-2$
					} else {
						TaskAttribute res1 = taskData.getRoot()
								.getAttribute(BugzillaRestTaskSchema.getDefault().RESOLUTION.getKey());
						res1.setValue("DUPLICATE"); //$NON-NLS-1$
					}
					value = "RESOLVED"; //$NON-NLS-1$
				}
			}
			if (taskAttribute.getMetaData().getType() != null
					&& taskAttribute.getMetaData().getType().equals(TaskAttribute.TYPE_MULTI_SELECT)) {
				value = taskAttribute.getValues()
						.stream()
						.filter(Objects::nonNull)
						.map(function)
						.collect(Collectors.joining(",")); //$NON-NLS-1$
			}
			if (id.equals(BugzillaRestTaskSchema.getDefault().NEW_COMMENT.getKey())) {
				out.name("comment").beginObject(); //$NON-NLS-1$
				out.name("body").value(value); //$NON-NLS-1$
				out.endObject();
			} else if (id.equals(BugzillaRestTaskSchema.getDefault().BLOCKS.getKey())
					|| id.equals(BugzillaRestTaskSchema.getDefault().DEPENDS_ON.getKey())) {
				Set<String> setOld;
				if (element.getValues().size() > 1) {
					setOld = new HashSet<>(element.getValues());
				} else {
					setOld = new HashSet<>(Arrays.asList(element.getValue().split("\\s*,\\s*"))); //$NON-NLS-1$
				}
				Set<String> setNew;
				if (taskAttribute.getValues().size() > 1) {
					setNew = new HashSet<>(taskAttribute.getValues());
				} else {
					setNew = new HashSet<>(Arrays.asList(taskAttribute.getValue().split("\\s*,\\s*"))); //$NON-NLS-1$
				}
				BugzillaRestGsonUtil.getDefault().buildAddRemoveIntegerHash(out, id, setOld, setNew);
			} else if (id.equals(BugzillaRestTaskSchema.getDefault().KEYWORDS.getKey())) {
				Set<String> setOld = new HashSet<>(element.getValues());
				Set<String> setNew = new HashSet<>(taskAttribute.getValues());
				BugzillaRestGsonUtil.getDefault().buildAddRemoveHash(out, id, setOld, setNew);
			} else {
				out.name(id).value(value);
				if (id.equals("description")) { //$NON-NLS-1$
					TaskAttribute descriptionpri = taskAttribute
							.getAttribute(BugzillaRestTaskSchema.getDefault().COMMENT_ISPRIVATE.getKey());
					Boolean descriptionprivalue = descriptionpri != null
							? descriptionpri.getValue().equals("1") //$NON-NLS-1$
							: false;
					out.name("comment_is_private").value(Boolean.toString(descriptionprivalue)); //$NON-NLS-1$
				}
			}
		}
	}

	@Override
	public void addSuffixToOutput(JsonWriter out) throws IOException {
		TaskAttribute cc = taskData.getRoot().getAttribute(BugzillaRestTaskSchema.getDefault().CC.getKey());
		TaskAttribute addCC = taskData.getRoot().getAttribute(BugzillaRestTaskSchema.getDefault().ADD_CC.getKey());
		TaskAttribute removeCC = taskData.getRoot()
				.getAttribute(BugzillaRestTaskSchema.getDefault().REMOVE_CC.getKey());
		TaskAttribute addSelfCC = taskData.getRoot()
				.getAttribute(BugzillaRestTaskSchema.getDefault().ADD_SELF_CC.getKey());
		if (Boolean.parseBoolean(addSelfCC.getValue())) {
			String userName = addSelfCC.getMetaData().getValue("UserName"); //$NON-NLS-1$
			if (userName != null) {
				if (removeCC.getValues().contains(userName)) {
					removeCC.removeValue(userName);
				} else if (!cc.getValues().contains(userName)) {
					List<String> addCCList = Arrays.asList(addCC.getValue().split("\\s*,\\s*")); //$NON-NLS-1$
					if (!addCCList.contains(userName)) {
						addCC.setValue(userName + (addCCList.size() > 0 && !addCCList.get(0).equals("") ? ", " : "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								+ addCC.getValue());
					}
				}
			}
		}
		if (addCC.getValues().size() > 0 || removeCC.getValues().size() > 0) {
			Set<String> setOld = new HashSet<>(removeCC.getValues());
			HashSet<String> setNew = new HashSet<>(Arrays.asList(addCC.getValue().split("\\s*,\\s*"))); //$NON-NLS-1$
			BugzillaRestGsonUtil.getDefault().buildAddRemoveHash(out, "cc", setOld, setNew); //$NON-NLS-1$
		}
		BugzillaRestGsonUtil.buildFlags(out, taskAttributes.getTaskAttributes(), taskData.getRoot());
	}

}