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

import org.eclipse.birt.report.model.api.DesignFileException;
import org.eclipse.birt.report.model.core.DesignSession;
import org.eclipse.birt.report.model.elements.ReportDesign;
import org.eclipse.birt.report.model.util.AbstractParseState;
import org.eclipse.birt.report.model.util.XMLParserException;
import org.eclipse.birt.report.model.util.XMLParserHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Top-level handler for the XML design file. Holds the design being created and
 * recognizes the top-level tags in the file.
 *  
 */

public class DesignParserHandler extends XMLParserHandler
{

	/**
	 * The design session that will own this design.
	 */

	protected DesignSession session = null;

	/**
	 * The design being created.
	 */

	protected ReportDesign design = null;

	/**
	 * Constructs the design parser handler with the design session.
	 * 
	 * @param theSession
	 *            the design session that is to own the design
	 */

	public DesignParserHandler( DesignSession theSession )
	{
		session = theSession;
		design = new ReportDesign( session );
	}

	/**
	 * Overrides the super method. This method first parses attributes of the
	 * current state, and then query whether to use a new state or the current
	 * one according to the attributes value.
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */

	public void startElement( String namespaceURI, String localName,
			String qName, Attributes atts ) throws SAXException
	{
		currentElement = qName;
		AbstractParseState newState = topState( ).startElement( qName );
		newState.parseAttrs( atts );
		AbstractParseState jumpToState = newState.jumpTo( );
		if ( jumpToState != null )
		{
			pushState( jumpToState );
			return;
		}

		newState.setElementName( currentElement );
		pushState( newState );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */

	public void startDocument( ) throws SAXException
	{
		super.startDocument( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */

	public void endDocument( ) throws SAXException
	{
		super.endDocument( );

		// Skip the semantic check if we've already found errors.
		// Doing the semantic check would just uncover bogus errors
		// due to the ones we've already seen.

		if ( !design.getErrors( ).isEmpty( ) )
		{
			DesignFileException exception = new DesignFileException( design
					.getFileName( ), design.getErrors( ) );

			throw new SAXException( exception );
		}

		design.semanticCheck( design );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.model.util.XMLParserHandler#createStartState()
	 */

	public AbstractParseState createStartState( )
	{
		return new StartState( );
	}

	/**
	 * Adds a recoverable semantic error to the error list.
	 * 
	 * @param e
	 *            The exception to log.
	 */

	public void semanticError( XMLParserException e )
	{
		e.setLineNumber( locator.getLineNumber( ) );
		e.setTag( currentElement );
		design.getErrors( ).add( e );
	}

	/**
	 * Recognizes the top-level tags: Report or Template.
	 */

	class StartState extends InnerParseState
	{

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.birt.report.model.util.AbstractParseState#startElement(java.lang.String)
		 */

		public AbstractParseState startElement( String tagName )
		{
			if ( tagName.equalsIgnoreCase( DesignSchemaConstants.REPORT_TAG ) )
				return new ReportState( DesignParserHandler.this );
			if ( tagName.equalsIgnoreCase( DesignSchemaConstants.TEMPLATE_TAG ) )
				return new InnerAnyTagState( );
			return super.startElement( tagName );
		}
	}

	/**
	 * Returns the design being created.
	 * 
	 * @return the design being created
	 */

	public ReportDesign getDesign( )
	{
		return design;
	}

}