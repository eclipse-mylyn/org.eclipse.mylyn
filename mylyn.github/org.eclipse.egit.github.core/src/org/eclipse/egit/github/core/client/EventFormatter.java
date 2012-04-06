/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Jason Tsay (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.client;

import static org.eclipse.egit.github.core.event.Event.TYPE_COMMIT_COMMENT;
import static org.eclipse.egit.github.core.event.Event.TYPE_CREATE;
import static org.eclipse.egit.github.core.event.Event.TYPE_DELETE;
import static org.eclipse.egit.github.core.event.Event.TYPE_DOWNLOAD;
import static org.eclipse.egit.github.core.event.Event.TYPE_FOLLOW;
import static org.eclipse.egit.github.core.event.Event.TYPE_FORK;
import static org.eclipse.egit.github.core.event.Event.TYPE_FORK_APPLY;
import static org.eclipse.egit.github.core.event.Event.TYPE_GIST;
import static org.eclipse.egit.github.core.event.Event.TYPE_GOLLUM;
import static org.eclipse.egit.github.core.event.Event.TYPE_ISSUES;
import static org.eclipse.egit.github.core.event.Event.TYPE_ISSUE_COMMENT;
import static org.eclipse.egit.github.core.event.Event.TYPE_MEMBER;
import static org.eclipse.egit.github.core.event.Event.TYPE_PUBLIC;
import static org.eclipse.egit.github.core.event.Event.TYPE_PULL_REQUEST;
import static org.eclipse.egit.github.core.event.Event.TYPE_PUSH;
import static org.eclipse.egit.github.core.event.Event.TYPE_TEAM_ADD;
import static org.eclipse.egit.github.core.event.Event.TYPE_WATCH;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import org.eclipse.egit.github.core.event.CommitCommentPayload;
import org.eclipse.egit.github.core.event.CreatePayload;
import org.eclipse.egit.github.core.event.DeletePayload;
import org.eclipse.egit.github.core.event.DownloadPayload;
import org.eclipse.egit.github.core.event.Event;
import org.eclipse.egit.github.core.event.EventPayload;
import org.eclipse.egit.github.core.event.FollowPayload;
import org.eclipse.egit.github.core.event.ForkApplyPayload;
import org.eclipse.egit.github.core.event.ForkPayload;
import org.eclipse.egit.github.core.event.GistPayload;
import org.eclipse.egit.github.core.event.GollumPayload;
import org.eclipse.egit.github.core.event.IssueCommentPayload;
import org.eclipse.egit.github.core.event.IssuesPayload;
import org.eclipse.egit.github.core.event.MemberPayload;
import org.eclipse.egit.github.core.event.PublicPayload;
import org.eclipse.egit.github.core.event.PullRequestPayload;
import org.eclipse.egit.github.core.event.PushPayload;
import org.eclipse.egit.github.core.event.TeamAddPayload;
import org.eclipse.egit.github.core.event.WatchPayload;

/**
 * Formats an event's payload with the appropriate class given a certain event
 * type
 */
public class EventFormatter {

	private class PayloadDeserializer implements JsonDeserializer<EventPayload> {

		public EventPayload deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			String type = event.get().getType();
			Class<? extends EventPayload> clazz = EventPayload.class;
			if (TYPE_COMMIT_COMMENT.equals(type))
				clazz = CommitCommentPayload.class;
			else if (TYPE_CREATE.equals(type))
				clazz = CreatePayload.class;
			else if (TYPE_DELETE.equals(type))
				clazz = DeletePayload.class;
			else if (TYPE_DOWNLOAD.equals(type))
				clazz = DownloadPayload.class;
			else if (TYPE_FOLLOW.equals(type))
				clazz = FollowPayload.class;
			else if (TYPE_FORK.equals(type))
				clazz = ForkPayload.class;
			else if (TYPE_FORK_APPLY.equals(type))
				clazz = ForkApplyPayload.class;
			else if (TYPE_GIST.equals(type))
				clazz = GistPayload.class;
			else if (TYPE_GOLLUM.equals(type))
				clazz = GollumPayload.class;
			else if (TYPE_ISSUE_COMMENT.equals(type))
				clazz = IssueCommentPayload.class;
			else if (TYPE_ISSUES.equals(type))
				clazz = IssuesPayload.class;
			else if (TYPE_MEMBER.equals(type))
				clazz = MemberPayload.class;
			else if (TYPE_PUBLIC.equals(type))
				clazz = PublicPayload.class;
			else if (TYPE_PULL_REQUEST.equals(type))
				clazz = PullRequestPayload.class;
			else if (TYPE_PUSH.equals(type))
				clazz = PushPayload.class;
			else if (TYPE_TEAM_ADD.equals(type))
				clazz = TeamAddPayload.class;
			else if (TYPE_WATCH.equals(type))
				clazz = WatchPayload.class;

			// payload not recognized
			if (clazz == EventPayload.class)
				return new EventPayload();

			return context.deserialize(json, clazz);
		}
	}

	private class EventCreator implements InstanceCreator<Event> {

		public Event createInstance(Type type) {
			final Event event = new Event();
			EventFormatter.this.event.set(event);
			return event;
		}
	}

	private final ThreadLocal<Event> event = new ThreadLocal<Event>();

	private final PayloadDeserializer payloadDeserializer = new PayloadDeserializer();

	private final EventCreator eventCreator = new EventCreator();

	/**
	 * Get deserializer for {@link EventPayload} objects
	 *
	 * @return deserializer
	 */
	public JsonDeserializer<EventPayload> getPayloadDeserializer() {
		return payloadDeserializer;
	}

	/**
	 * Get instance creator for {@link Event} objects
	 *
	 * @return instance creator
	 */
	public InstanceCreator<Event> getEventCreator() {
		return eventCreator;
	}
}
