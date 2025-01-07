/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.tracwiki;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.Block;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.token.ImpliedHyperlinkReplacementToken;
import org.eclipse.mylyn.wikitext.tracwiki.internal.block.DefinitionListBlock;
import org.eclipse.mylyn.wikitext.tracwiki.internal.block.HeadingBlock;
import org.eclipse.mylyn.wikitext.tracwiki.internal.block.ListBlock;
import org.eclipse.mylyn.wikitext.tracwiki.internal.block.ParagraphBlock;
import org.eclipse.mylyn.wikitext.tracwiki.internal.block.PreformattedBlock;
import org.eclipse.mylyn.wikitext.tracwiki.internal.block.QuoteBlock;
import org.eclipse.mylyn.wikitext.tracwiki.internal.block.TableBlock;
import org.eclipse.mylyn.wikitext.tracwiki.internal.phrase.EscapePhraseModifier;
import org.eclipse.mylyn.wikitext.tracwiki.internal.phrase.MonospacePhraseModifier;
import org.eclipse.mylyn.wikitext.tracwiki.internal.phrase.SimplePhraseModifier;
import org.eclipse.mylyn.wikitext.tracwiki.internal.token.BangEscapeToken;
import org.eclipse.mylyn.wikitext.tracwiki.internal.token.ChangesetLinkReplacementToken;
import org.eclipse.mylyn.wikitext.tracwiki.internal.token.HyperlinkReplacementToken;
import org.eclipse.mylyn.wikitext.tracwiki.internal.token.LineBreakToken;
import org.eclipse.mylyn.wikitext.tracwiki.internal.token.MacroReplacementToken;
import org.eclipse.mylyn.wikitext.tracwiki.internal.token.MilestoneLinkReplacementToken;
import org.eclipse.mylyn.wikitext.tracwiki.internal.token.ReportLinkReplacementToken;
import org.eclipse.mylyn.wikitext.tracwiki.internal.token.RevisionLogReplacementToken;
import org.eclipse.mylyn.wikitext.tracwiki.internal.token.SourceLinkReplacementToken;
import org.eclipse.mylyn.wikitext.tracwiki.internal.token.TicketAttachmentLinkReplacementToken;
import org.eclipse.mylyn.wikitext.tracwiki.internal.token.TicketLinkReplacementToken;
import org.eclipse.mylyn.wikitext.tracwiki.internal.token.WikiLinkReplacementToken;
import org.eclipse.mylyn.wikitext.tracwiki.internal.token.WikiWordReplacementToken;

/**
 * An implementation of the <a href="http://trac.edgewall.org/wiki/TracWiki">TracWiki</a> markup language.
 *
 * @author David Green
 * @since 4.6
 */
public class TracWikiLanguage extends AbstractMarkupLanguage {

	private boolean autoLinking = true;

	private String serverUrl;

	public TracWikiLanguage() {
		setName("TracWiki"); //$NON-NLS-1$
	}

	/**
	 * Convert a page name to an href to the page.
	 *
	 * @param pageName
	 *            the name of the page to target
	 * @return the href to access the page
	 * @see MarkupLanguage#getInternalLinkPattern()
	 */
	public String toInternalHref(String pageName) {
		String pageId = pageName;
		if (pageId.startsWith("#")) { //$NON-NLS-1$
			// internal anchor
			return pageId;
		}
		return MessageFormat.format(internalLinkPattern, pageId);
	}

	/**
	 * convert a ticket id to a hyperlink based on the {@link #getServerUrl() server url}
	 *
	 * @param ticketId
	 *            the id of the ticket
	 * @param commentNumber
	 *            the comment number or null if the url should not reference a specific comment
	 */
	public String toTicketHref(String ticketId, String commentNumber) {
		String url = serverUrl + "ticket/" + ticketId; //$NON-NLS-1$
		if (commentNumber != null) {
			url += "#comment:" + commentNumber; //$NON-NLS-1$
		}
		return url;
	}

	/**
	 * convert a changeset id to a hyperlink based on the {@link #getServerUrl() server url}
	 *
	 * @param changesetId
	 *            the changeset id
	 * @param restriction
	 *            the restriction, or null if there is no restriction. eg: "trunk"
	 */
	public String toChangesetHref(String changesetId, String restriction) {
		String url = serverUrl + "changeset/" + changesetId; //$NON-NLS-1$
		if (restriction != null) {
			url += "/" + restriction; //$NON-NLS-1$
		}
		return url;
	}

	/**
	 * convert a revisions to a revision log hyperlink based on the {@link #getServerUrl() server url}
	 *
	 * @param revision1
	 *            the first revision
	 * @param revision2
	 *            the second revision
	 * @param restriction
	 *            the restriction, or null if there is no restriction. eg: "trunk"
	 */
	public String toRevisionLogHref(String revision1, String revision2, String restriction) {
		String url = serverUrl + "log/"; //$NON-NLS-1$
		if (restriction != null) {
			url += restriction;
		}
		url += "?revs=" + revision1 + "-" + revision2; //$NON-NLS-1$ //$NON-NLS-2$
		return url;
	}

	/**
	 * convert a report id to a hyperlink based on the {@link #getServerUrl() server url}
	 *
	 * @param reportId
	 *            the id of the report
	 */
	public String toReportHref(String reportId) {
		String url = serverUrl + "report/" + reportId; //$NON-NLS-1$
		return url;
	}

	/**
	 * convert a milestone id to a hyperlink based on the {@link #getServerUrl() server url}
	 *
	 * @param milestoneId
	 *            the id of the milesonte
	 */
	public String toMilestoneHref(String milestoneId) {
		String url = serverUrl + "milestone/" + milestoneId; //$NON-NLS-1$
		return url;
	}

	/**
	 * create an URL to an attachment ticket based on the {@link #getServerUrl() server url}
	 *
	 * @param ticketId
	 *            the id of the ticket
	 * @param attachment
	 *            the name of the attachment
	 */
	public String toTicketAttachmentHref(String ticketId, String attachment) {
		String url = serverUrl + "ticket/" + ticketId + "/" + attachment; //$NON-NLS-1$ //$NON-NLS-2$
		return url;
	}

	/**
	 * create an URL to the source browser
	 *
	 * @param source
	 *            the source to be viewed
	 * @param revision
	 *            the revision, or null if there is no revision
	 * @param line
	 *            the line, or null if there is no line
	 */
	public String toSourceBrowserHref(String source, String revision, String line) {
		String url = serverUrl + "browser"; //$NON-NLS-1$
		if (source.charAt(0) != '/') {
			url += '/';
		}
		url += source;
		if (revision != null) {
			url += "?rev=" + revision; //$NON-NLS-1$
		}
		if (line != null) {
			url += "#L" + line; //$NON-NLS-1$
		}
		return url;
	}

	/**
	 * for the purpose of converting wiki words into links, determine if the wiki word exists.
	 *
	 * @see WikiWordReplacementToken
	 */
	public boolean computeInternalLinkExists(String link) {
		return true;
	}

	/**
	 * Indicate if the markup should match WikiWords as hyperlinks. The default is true.
	 */
	public boolean isAutoLinking() {
		return autoLinking;
	}

	/**
	 * Indicate if the markup should match WikiWords as hyperlinks. The default is true.
	 */
	public void setAutoLinking(boolean autoLinking) {
		this.autoLinking = autoLinking;
	}

	/**
	 * set the server URL, for example <code>http://trac.edgewall.org/</code> from which links may be derrived, such as
	 * <code>http://trac.edgewall.org/wiki/WikiPage</code> or <code>http://trac.edgewall.org/tickets/1</code>
	 *
	 * @param url
	 *            the url, or null if it is unknown.
	 */
	public void setServerUrl(String url) {
		if (url != null && !url.endsWith("/")) { //$NON-NLS-1$
			url = url + "/"; //$NON-NLS-1$
		}
		serverUrl = url;
	}

	/**
	 * the server URL, for example <code>http://trac.edgewall.org/</code> from which links may be derrived, such as
	 * <code>http://trac.edgewall.org/wiki/WikiPage</code> or <code>http://trac.edgewall.org/tickets/1</code>
	 *
	 * @see #setServerUrl(String)
	 */
	public String getServerUrl() {
		return serverUrl;
	}

	@Override
	protected void addStandardBlocks(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
		// IMPORTANT NOTE: Most items below have order dependencies.  DO NOT REORDER ITEMS BELOW!!

		// TODO: processors

		ListBlock listBlock = new ListBlock();
		blocks.add(listBlock);
		paragraphBreakingBlocks.add(listBlock);
		DefinitionListBlock definitionListBlock = new DefinitionListBlock();
		blocks.add(definitionListBlock);
		paragraphBreakingBlocks.add(definitionListBlock);
		HeadingBlock headingBlock = new HeadingBlock();
		blocks.add(headingBlock);
		paragraphBreakingBlocks.add(headingBlock);
		PreformattedBlock preformattedBlock = new PreformattedBlock();
		blocks.add(preformattedBlock);
		paragraphBreakingBlocks.add(preformattedBlock);
		QuoteBlock quoteBlock = new QuoteBlock();
		blocks.add(quoteBlock);
		paragraphBreakingBlocks.add(quoteBlock);
		TableBlock tableBlock = new TableBlock();
		blocks.add(tableBlock);
		paragraphBreakingBlocks.add(tableBlock);

	}

	@Override
	protected void addStandardPhraseModifiers(PatternBasedSyntax phraseModifierSyntax) {
		// IMPORTANT NOTE: Most items below have order dependencies.  DO NOT REORDER ITEMS BELOW!!
		phraseModifierSyntax.add(new MonospacePhraseModifier());
		phraseModifierSyntax.beginGroup("(?:(?<=[\\s\\.\\\"'?!;:\\)\\(\\{\\}\\[\\]-])|^)(?:", 0); // always starts at the start of a line or after a non-word character excluding '!' and '-' //$NON-NLS-1$
		phraseModifierSyntax.add(new EscapePhraseModifier());
		phraseModifierSyntax.add(new SimplePhraseModifier("'''''", new SpanType[] { SpanType.BOLD, SpanType.ITALIC }, //$NON-NLS-1$
				true));
		phraseModifierSyntax.add(new SimplePhraseModifier("'''", SpanType.BOLD, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("''", SpanType.ITALIC, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("__", SpanType.UNDERLINED, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("~~", SpanType.DELETED, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("^", SpanType.SUPERSCRIPT, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier(",,", SpanType.SUBSCRIPT, true)); //$NON-NLS-1$
		phraseModifierSyntax.endGroup(")(?=\\W|$)", 0); //$NON-NLS-1$
	}

	@Override
	protected void addStandardTokens(PatternBasedSyntax tokenSyntax) {
		// IMPORTANT NOTE: Most items below have order dependencies.  DO NOT REORDER ITEMS BELOW!!
		tokenSyntax.add(new BangEscapeToken());
		tokenSyntax.add(new LineBreakToken());
		tokenSyntax.beginGroup("(?:(?<=[\\s\\.\\\"'?!;:\\)\\(\\{\\}\\[\\]-])|^)(?:", 0); // always starts at the start of a line or after a non-word character excluding '!' and '-' //$NON-NLS-1$
		tokenSyntax.add(new MacroReplacementToken());
		tokenSyntax.add(new RevisionLogReplacementToken());
		tokenSyntax.add(new ChangesetLinkReplacementToken());
		tokenSyntax.add(new HyperlinkReplacementToken());
		tokenSyntax.add(new ImpliedHyperlinkReplacementToken());
		tokenSyntax.add(new TicketAttachmentLinkReplacementToken());
		tokenSyntax.add(new TicketLinkReplacementToken());
		tokenSyntax.add(new ReportLinkReplacementToken());
		tokenSyntax.add(new MilestoneLinkReplacementToken());
		tokenSyntax.add(new SourceLinkReplacementToken());
		tokenSyntax.add(new WikiLinkReplacementToken());
		if (configuration == null || configuration.isWikiWordLinking() == null || configuration.isWikiWordLinking()) {
			tokenSyntax.add(new WikiWordReplacementToken());
		}
		tokenSyntax.endGroup(")(?=\\W|$)", 0); //$NON-NLS-1$
	}

	@Override
	protected Block createParagraphBlock() {
		return new ParagraphBlock();
	}

}
