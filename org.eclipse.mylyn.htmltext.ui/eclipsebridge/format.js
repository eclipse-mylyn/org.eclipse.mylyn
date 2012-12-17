/*******************************************************************************
 * Copyright (c) 2010 Tom Seidel, Remus Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 * Contributors:
 *     Tom Seidel - initial API and implementation
 *******************************************************************************/
 
function EclipseStyles() {
	
	
}

EclipseStyles.prototype.init = function(editor) {
	var config = editor.config,
	lang = editor.lang.format;
	this.editor = editor;
	// Gets the list of tags from the settings.
	var tags = config.format_tags.split( ';' );
	
	// Create style objects for all defined styles.
	this.styles = {};
	this.sizes = {};
	this.plainSizes = {};
	this.plainFonts = {};
	this.fonts = {};
	var availableSizes = this.getAvailableSizes().split(';');
	for ( var i = 0 ; i < availableSizes.length ; i++ ) {
		var size = availableSizes[i].split("/")[0];
		var vars = {};
		vars['size'] = size + 'px';
		var fontSizeStyle = new CKEDITOR.style( CKEDITOR.config.fontSize_style, vars );
		this.sizes[i] = fontSizeStyle;
		this.plainSizes[i] = size;
	}
	var availableFonts = this.getAvailableFonts().split(';');
	for ( var i = 0 ; i < availableFonts.length ; i++ ) {
		var fontfamily = availableFonts[i].split("/")[1];
		var vars = {};
		vars['family'] = fontfamily;
		var fontNameStyle = new CKEDITOR.style( CKEDITOR.config.font_style, vars );
		this.fonts[i] = fontNameStyle;
		this.plainFonts[i] = fontfamily;
	}
	for ( var i = 0 ; i < tags.length ; i++ )
	{
		var tag = tags[ i ];
		this.styles[ tag ] = new CKEDITOR.style( config[ 'format_' + tag ] );
		this.styles[ tag ]._.enterMode = editor.config.enterMode;
		this.editor.addCommand( tag, new CKEDITOR.styleCommand(new CKEDITOR.style( this.styles[tag] )) );

	}
	var thisInstance = this;
	this.editor.on( 'selectionChange', function( ev )
	{
		var elementPath = ev.data.path;
		for ( var tag in thisInstance.styles )
		{
			if ( thisInstance.styles[ tag ].checkActive( elementPath ) )
			{
				_delegate_selectedformat(tag);
				return;
			}
		}
		// If no styles match, just empty it.
		_delegate_selectedformat('');
	
	});
	this.editor.on( 'selectionChange', function( ev )
	{
		var elementPath = ev.data.path;
		for ( var tag in thisInstance.sizes )
		{
			if ( thisInstance.sizes[ tag ].checkActive( elementPath ) )
			{
				_delegate_selectedsize(thisInstance.plainSizes[ tag ]);
				return;
			}
		}
		// If no styles match, send -1
		_delegate_selectedsize(-1);
		
	});
	this.editor.on( 'selectionChange', function( ev )
	{
		var elementPath = ev.data.path;
		for ( var tag in thisInstance.fonts )
		{
			if ( thisInstance.fonts[ tag ].checkActive( elementPath ) )
			{
				_delegate_selectedfontfamily(thisInstance.plainFonts[ tag ]);
				return;
			}
		}
		// If no styles match, send -1
		_delegate_selectedfontfamily(' ');
	});
	
}
EclipseStyles.prototype.setStyle = function(style) {
	this.editor.execCommand(style);
}
EclipseStyles.prototype.setSize = function(size) {
	this.buildFontSizeStyle(size).apply( this.editor.document );
}
EclipseStyles.prototype.setFont = function(font) {
	this.buildFontStyle(font).apply( this.editor.document );
}

EclipseStyles.prototype.getAvailableSizes = function() {
	return CKEDITOR.config.fontSize_sizes;
}
EclipseStyles.prototype.getAvailableFonts = function() {
	return CKEDITOR.config.font_names;
}

EclipseStyles.prototype.buildFontSizeStyle = function(size) {
	var vars = {};
	vars['size'] = size + 'px';
	var fontSizeStyle = new CKEDITOR.style( CKEDITOR.config.fontSize_style, vars );
	return fontSizeStyle;
}
EclipseStyles.prototype.buildFontStyle = function(fontFamily) {
	var vars = {};
	vars['family'] = fontFamily;
	var fontSizeStyle = new CKEDITOR.style( CKEDITOR.config.font_style, vars );
	return fontSizeStyle;
}