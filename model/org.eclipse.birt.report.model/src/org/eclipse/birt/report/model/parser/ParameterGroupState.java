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

import org.eclipse.birt.report.model.core.DesignElement;
import org.eclipse.birt.report.model.elements.ParameterGroup;
import org.eclipse.birt.report.model.elements.ReportDesign;
import org.eclipse.birt.report.model.util.AbstractParseState;
import org.eclipse.birt.report.model.util.XMLParserException;
import org.xml.sax.Attributes;


/**
 * This class parses the parameter group element.
 * 
 */

public class ParameterGroupState extends ReportElementState
{
    /**
     * The ParameterGroup instance.
     */
	
    protected ParameterGroup paramGroup;

    /**
     * Constructs the parameter group state with the design file parser handler.
     * 
     * @param handler the design parser handler.
     */
    
    public ParameterGroupState(DesignParserHandler handler)
    {
        super( handler, handler.getDesign(), ReportDesign.PARAMETER_SLOT );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.birt.report.model.parser.DesignParseState#getElement()
     */
    
    public DesignElement getElement()
    {
        return paramGroup;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.birt.report.model.util.AbstractParseState#startElement(java.lang.String)
     */
    
    public AbstractParseState startElement( String tagName )
    {
        if( tagName.equalsIgnoreCase( DesignSchemaConstants.HELP_TEXT_TAG ) )
            return new ExternalTextState( handler, paramGroup,
                    ParameterGroup.HELP_TEXT_PROP );
        if( tagName.equalsIgnoreCase( DesignSchemaConstants.PARAMETERS_TAG ) )
            return new ParametersState( handler, paramGroup,
                    ParameterGroup.PARAMETERS_SLOT );

        return super.startElement( tagName );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.birt.report.model.util.AbstractParseState#parseAttrs(org.xml.sax.Attributes)
     */
    
    public void parseAttrs( Attributes attrs ) throws XMLParserException
    {
        paramGroup = new ParameterGroup();
        initElement( attrs, true );
        setProperty( ParameterGroup.START_EXPANDED_PROP, attrs,
                DesignSchemaConstants.START_EXPANDED_ATTRIB );
    }
}
