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
			if ("CommitCommentEvent".equals(type))
				clazz = CommitCommentPayload.class;
			else if ("CreateEvent".equals(type))
				clazz = CreatePayload.class;
			else if ("DeleteEvent".equals(type))
				clazz = DeletePayload.class;
			else if ("DownloadEvent".equals(type))
				clazz = DownloadPayload.class;
			else if ("FollowEvent".equals(type))
				clazz = FollowPayload.class;
			else if ("ForkEvent".equals(type))
				clazz = ForkPayload.class;
			else if ("ForkApplyEvent".equals(type))
				clazz = ForkApplyPayload.class;
			else if ("GistEvent".equals(type))
				clazz = GistPayload.class;
			else if ("GollumEvent".equals(type))
				clazz = GollumPayload.class;
			else if ("IssueCommentEvent".equals(type))
				clazz = IssueCommentPayload.class;
			else if ("IssuesEvent".equals(type))
				clazz = IssuesPayload.class;
			else if ("MemberEvent".equals(type))
				clazz = MemberPayload.class;
			else if ("PublicEvent".equals(type))
				clazz = PublicPayload.class;
			else if ("PullRequestEvent".equals(type))
				clazz = PullRequestPayload.class;
			else if ("PushEvent".equals(type))
				clazz = PushPayload.class;
			else if ("TeamAddEvent".equals(type))
				clazz = TeamAddPayload.class;
			else if ("WatchEvent".equals(type))
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
