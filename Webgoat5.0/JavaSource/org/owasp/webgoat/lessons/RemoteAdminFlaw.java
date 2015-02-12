package org.owasp.webgoat.lessons;

import java.util.ArrayList;
import java.util.List;

import org.apache.ecs.Element;
import org.apache.ecs.ElementContainer;
import org.owasp.webgoat.session.WebSession;

/*******************************************************************************
 * 
 * 
 * This file is part of WebGoat, an Open Web Application Security Project
 * utility. For details, please see http://www.owasp.org/
 * 
 * Copyright (c) 2002 - 2007 Bruce Mayhew
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Getting Source ==============
 * 
 * Source for this application is maintained at code.google.com, a repository
 * for free software projects.
 * 
 * For details, please see http://code.google.com/p/webgoat/
 *
 * @author     Bruce Mayhew <a href="http://code.google.com/p/webgoat">WebGoat</a>
 * @created    October 28, 2003
 */
public class RemoteAdminFlaw extends LessonAdapter
{

    /**
     *  Description of the Method
     *
     * @param  s  Description of the Parameter
     * @return    Description of the Return Value
     */
    protected Element createContent(WebSession s)
    {
		ElementContainer ec = new ElementContainer();
	
		if (s.completedHackableAdmin())
		{
		    makeSuccess(s);
		}
		else
		{
		    ec.addElement("WebGoat has an admin interface.  To 'complete' this lesson you must figure "
				    + "out how to access the administrative interface for WebGoat.");
		}
		return ec;

    }


    /**
     *  Gets the category attribute of the ForgotPassword object
     *
     * @return    The category value
     */
    protected Category getDefaultCategory()
    {
    	return AbstractLesson.A2;
    }


    /**
     *  Gets the hints attribute of the HelloScreen object
     *
     * @return    The hints value
     */
    public List getHints()
    {
	List<String> hints = new ArrayList<String>();
	hints.add("WebGoat has 2 admin interfaces.");
	hints.add("WebGoat has one admin interface that is controlled via a URL parameter and is 'hackable'");
	hints.add("WebGoat has one admin interface that is controlled via server side security constraints and should not be 'hackable'");
	hints.add("Follow the Source!");

	return hints;
    }

    private final static Integer DEFAULT_RANKING = new Integer(160);


    protected Integer getDefaultRanking()
    {
    	return DEFAULT_RANKING;
    }


    /**
     *  Gets the title attribute of the HelloScreen object
     *
     * @return    The title value
     */
    public String getTitle()
    {
    	return ("Remote Admin Access");
    }

}
