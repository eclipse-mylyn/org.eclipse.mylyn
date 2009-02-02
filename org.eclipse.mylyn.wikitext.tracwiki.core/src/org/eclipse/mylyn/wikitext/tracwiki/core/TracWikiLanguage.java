/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.tracwiki.core;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.mylyn.internal.wikitext.tracwiki.core.block.HeadingBlock;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.block.ListBlock;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.block.ParagraphBlock;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.block.PreformattedBlock;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.block.QuoteBlock;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.block.TableBlock;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.phrase.DeletedPhraseModifier;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.phrase.EscapePhraseModifier;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.phrase.SimplePhraseModifier;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.token.BangEscapeToken;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.token.ChangesetLinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.token.HyperlinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.token.LineBreakToken;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.token.MilestoneLinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.token.ReportLinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.token.RevisionLogReplacementToken;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.token.SourceLinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.token.TicketAttachmentLinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.token.TicketLinkReplacementToken;
import org.eclipse.mylyn.internal.wikitext.tracwiki.core.token.WikiWordReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.ImpliedHyperlinkReplacementToken;

/**
 * An implementation of the <a href="http://trac.edgewall.org/wiki/TracWiki">TracWiki</a> markup language.
 * 
 * @author David Green
 * @since 1.0
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
	 * 
	 * @return the href to access the page
	 * 
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

	/**
	 * subclasses may override this method to add blocks to the TracWiki language. Overriding classes should call
	 * <code>super.addBlockExtensions(blocks,paragraphBreakingBlocks)</code> if the default language extensions are
	 * desired.
	 * 
	 * @param blocks
	 *            the list of blocks to which extensions may be added
	 * @param paragraphBreakingBlocks
	 *            the list of blocks that end a paragraph
	 */
	@Override
	protected void addBlockExtensions(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
		// no block extensions
	}

	/**
	 * subclasses may override this method to add tokens to the TracWiki language. Overriding classes should call
	 * <code>super.addTokenExtensions(tokenSyntax)</code> if the default language extensions are desired.
	 * 
	 * @param tokenSyntax
	 *            the token syntax
	 */
	@Override
	protected void addTokenExtensions(PatternBasedSyntax tokenSyntax) {
		// no token extensions
	}

	/**
	 * subclasses may override this method to add phrases to the TracWiki language. Overriding classes should call
	 * <code>super.addPhraseModifierExtensions(phraseModifierSyntax)</code> if the default language extensions are
	 * desired.
	 * 
	 * @param phraseModifierSyntax
	 *            the phrase modifier syntax
	 */
	@Override
	protected void addPhraseModifierExtensions(PatternBasedSyntax phraseModifierSyntax) {
		// no phrase extensions
	}

	@Override
	protected void addStandardBlocks(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
		// IMPORTANT NOTE: Most items below have order dependencies.  DO NOT REORDER ITEMS BELOW!!

		// TODO: images, macros, processors

		ListBlock listBlock = new ListBlock();
		blocks.add(listBlock);
		paragraphBreakingBlocks.add(listBlock);
		HeadingBlock headingBlock = new HeadingBlock();
		blocks.add(headingBlock);
		paragraphBreakingBlocks.add(listBlock);
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
		phraseModifierSyntax.beginGroup("(?:(?<=[\\s\\.\\\"'?!;:\\)\\(\\{\\}\\[\\]-])|^)(?:", 0); // always starts at the start of a line or after a non-word character excluding '!' and '-' //$NON-NLS-1$
		phraseModifierSyntax.add(new EscapePhraseModifier());
		phraseModifierSyntax.add(new SimplePhraseModifier("'''''", new SpanType[] { SpanType.BOLD, SpanType.ITALIC }, //$NON-NLS-1$
				true));
		phraseModifierSyntax.add(new SimplePhraseModifier("'''", SpanType.BOLD, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("''", SpanType.ITALIC, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier("__", SpanType.UNDERLINED, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new DeletedPhraseModifier());
		phraseModifierSyntax.add(new SimplePhraseModifier("^", SpanType.SUPERSCRIPT, true)); //$NON-NLS-1$
		phraseModifierSyntax.add(new SimplePhraseModifier(",,", SpanType.SUBSCRIPT, true)); //$NON-NLS-1$
		phraseModifierSyntax.endGroup(")(?=\\W|$)", 0); //$NON-NLS-1$
	}

	@Override
	protected void addStandardTokens(PatternBasedSyntax tokenSyntax) {
		// IMPORTANT NOTE: Most items below have order dependencies.  DO NOT REORDER ITEMS BELOW!!
		tokenSyntax.add(new BangEscapeToken());
		tokenSyntax.add(new LineBreakToken());
		tokenSyntax.add(new RevisionLogReplacementToken());
		tokenSyntax.add(new ChangesetLinkReplacementToken());
		tokenSyntax.add(new HyperlinkReplacementToken());
		tokenSyntax.add(new ImpliedHyperlinkReplacementToken());
		tokenSyntax.add(new TicketAttachmentLinkReplacementToken());
		tokenSyntax.add(new TicketLinkReplacementToken());
		tokenSyntax.add(new ReportLinkReplacementToken());
		tokenSyntax.add(new MilestoneLinkReplacementToken());
		tokenSyntax.add(new SourceLinkReplacementToken());
		tokenSyntax.add(new WikiWordReplacementToken());
	}

	@Override
	protected Block createParagraphBlock() {
		return new ParagraphBlock();
	}

}
