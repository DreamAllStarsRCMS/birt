/*******************************************************************************
* Copyright (c) 2004 Actuate Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  Actuate Corporation  - initial API and implementation
*******************************************************************************/ 

package org.eclipse.birt.report.model.parser;

import org.eclipse.birt.report.model.elements.SimpleMasterPage;
import org.eclipse.birt.report.model.util.AbstractParseState;
import org.eclipse.birt.report.model.util.XMLParserException;
import org.eclipse.birt.report.model.util.XMLParserHandler;
import org.xml.sax.Attributes;

/**
 * This class parses a simple master page.
 * 
 */

public class SimpleMasterPageState extends MasterPageState
{

	/**
	 * Constructs the simple master page with the design file parser handler.
	 * 
	 * @param handler
	 */
	
	public SimpleMasterPageState( DesignParserHandler handler )
	{
		super( handler );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.model.util.AbstractParseState#parseAttrs(org.xml.sax.Attributes)
	 */

	public void parseAttrs( Attributes attrs ) throws XMLParserException
	{
		element = new SimpleMasterPage( );
		initElement( attrs, true );
		super.parseAttrs( attrs );

		setProperty( SimpleMasterPage.SHOW_HEADER_ON_FIRST_PROP, attrs,
				DesignSchemaConstants.SHOW_HEADER_ON_FIST_ATTRIB );
		setProperty( SimpleMasterPage.SHOW_FOOTER_ON_LAST_PROP, attrs,
				DesignSchemaConstants.SHOW_FOOTER_ON_LAST_ATTRIB );
		setProperty( SimpleMasterPage.FLOATING_FOOTER, attrs,
				DesignSchemaConstants.FLOATING_FOOTER );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.model.util.AbstractParseState#startElement(java.lang.String)
	 */
	
	public AbstractParseState startElement( String tagName )
	{
		if ( tagName.equalsIgnoreCase( DesignSchemaConstants.PAGE_HEADER_TAG ) )
			return new PageState( SimpleMasterPage.PAGE_HEADER_SLOT );
		if ( tagName.equalsIgnoreCase( DesignSchemaConstants.PAGE_FOOTER_TAG ) )
			return new PageState( SimpleMasterPage.PAGE_FOOTER_SLOT );
		return super.startElement( tagName );
	}

	/**
	 * This state is related to the 'page-header' & 'page-footer' element of
	 * simple master page. Since the 'page-header' & 'page-footer' look exactly
	 * the same except the parent element tag, only one inner class, PageState,
	 * is provided.
	 */
	class PageState extends AbstractParseState
	{

		/**
		 * The marker to identify which header this state is referencing for, the
		 * header or footer.
		 */
		private int	page;

		/**
		 * Constructor
		 * 
		 * @param page
		 *            the header or footer identification.
		 */
		public PageState( int page )
		{
			this.page = page;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.birt.report.model.util.AbstractParseState#getHandler()
		 */
		
		public XMLParserHandler getHandler( )
		{
			return handler;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.birt.report.model.util.AbstractParseState#startElement(java.lang.String)
		 */
		
		public AbstractParseState startElement( String tagName )
		{

			if ( tagName.equalsIgnoreCase( DesignSchemaConstants.TEXT_TAG ) )
				return new TextItemState( handler, element, page );
			else if ( tagName.equalsIgnoreCase( DesignSchemaConstants.GRID_TAG ) )
				return new GridItemState( handler, element, page );
			else if ( tagName
					.equalsIgnoreCase( DesignSchemaConstants.FREE_FORM_TAG ) )
				return new FreeFormState( handler, element, page );
			return super.startElement( tagName );
		}
	}
}
