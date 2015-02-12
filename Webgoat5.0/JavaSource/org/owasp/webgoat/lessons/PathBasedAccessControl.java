package org.owasp.webgoat.lessons;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.ecs.Element;
import org.apache.ecs.ElementContainer;
import org.apache.ecs.StringElement;
import org.apache.ecs.html.BR;
import org.apache.ecs.html.HR;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.Table;

import org.owasp.webgoat.session.ECSFactory;
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
public class PathBasedAccessControl extends LessonAdapter
{

    private final static String FILE = "File";


    /**
     *  Description of the Method
     *
     * @param  s  Description of the Parameter
     * @return    Description of the Return Value
     */
    protected Element createContent(WebSession s)
    {
	ElementContainer ec = new ElementContainer();

	try
	{
	    String dir = s.getContext().getRealPath("/lesson_plans");
	    File d = new File(dir);

	    Table t = new Table().setCellSpacing(0).setCellPadding(2).setWidth(
		    "90%").setAlign("center");

	    if (s.isColor())
	    {
		t.setBorder(1);
	    }

	    String[] list = d.list();
	    String listing = " <p><B>Current Directory is:</B> "
		    + Encoding.urlDecode(dir)
		    + "<br><br> Choose the file to view:</p>";

	    TR tr = new TR();
	    tr.addElement(new TD().setColSpan(2).addElement(
		    new StringElement(listing)));
	    t.addElement(tr);

	    tr = new TR();
	    tr.addElement(new TD().setWidth("35%").addElement(
		    ECSFactory.makePulldown(FILE, list, "", 15)));
	    tr.addElement(new TD().addElement(ECSFactory
		    .makeButton("View File")));
	    t.addElement(tr);

	    ec.addElement(t);

	    // FIXME: would be cool to allow encodings here -- hex, percent, url, etc...
	    String file = s.getParser().getRawParameter(FILE, "");

	    // defuse file searching
	    boolean illegalCommand = s.isDefuseOSCommands();
	    if (s.isDefuseOSCommands())
	    {
		// allow them to look at any file in the webgoat hierachy.  Don't allow them
		// to look about the webgoat root, except to see the LICENSE file
		if (upDirCount(file) == 3 && !file.endsWith("LICENSE"))
		{
		    s.setMessage("Access denied");
		    s
			    .setMessage("It appears that you are on the right track.  "
				    + "Commands that may compromise the operating system have been disabled.  "
				    + "You are only allowed to see one file in this directory. ");;
		}
		else if (upDirCount(file) > 3)
		{
		    s.setMessage("Access denied");
		    s
			    .setMessage("It appears that you are on the right track.  "
				    + "Commands that may compromise the operating system have been disabled.  "
				    + "You are only allowed to see files in the webgoat directory. ");
		}
		else
		{
		    illegalCommand = false;
		}
	    }

	    // Using the URI supports encoding of the data.  
	    // We could force the user to use encoded '/'s == %2f to make the lesson more difficult.
	    // We url Encode our dir name to avoid problems with special characters in our own path.
	    //File f = new File( new URI("file:///" + Encoding.urlEncode(dir).replaceAll("\\\\","/") + "/" + file.replaceAll("\\\\","/")) );
	    File f = new File((dir + "\\" + file).replaceAll("\\\\", "/"));

	    if (s.isDebug())
	    {

		s.setMessage("File: " + file);
		s.setMessage("Dir: " + dir);
		//s.setMessage("File URI: " + "file:///" + (Encoding.urlEncode(dir) + "\\" + Encoding.urlEncode(file)).replaceAll("\\\\","/"));
		s.setMessage("  - isFile(): " + f.isFile());
		s.setMessage("  - exists(): " + f.exists());
	    }
	    if (!illegalCommand)
	    {
		if (f.isFile() && f.exists())
		{
		    // Don't set completion if they are listing files in the 
		    // directory listing we gave them.
		    if (upDirCount(file) >= 1)
		    {
			s.setMessage("Congratulations! Access to file allowed");
			s.setMessage(" ==> "
				+ Encoding.urlDecode(f.getCanonicalPath()));
			makeSuccess(s);
		    }
		    else
		    {
			s
				.setMessage("File is already in allowed directory - try again!");
			s.setMessage(" ==> "
				+ Encoding.urlDecode(f.getCanonicalPath()));
		    }
		}
		else if (file != null && file.length() != 0)
		{
		    s.setMessage("Access to file/directory \""
			    + Encoding.urlDecode(f.getCanonicalPath())
			    + "\" denied");
		}
		else
		{
		    // do nothing, probably entry screen
		}

		try
		{
		    // Show them the file
		    // Strip out some of the extra html from the "help" file
		    ec.addElement(new BR());
		    ec.addElement(new BR());
		    ec.addElement(new HR().setWidth("100%"));
		    ec.addElement("Viewing file: " + f.getCanonicalPath());
		    ec.addElement(new HR().setWidth("100%"));
		    if (f.length() > 80000)
		    {
			throw new Exception("File is too large");
		    }
		    String fileData = getFileText(new BufferedReader(
			    new FileReader(f)), false);
		    if (fileData.indexOf(0x00) != -1)
		    {
			throw new Exception("File is binary");
		    }
		    ec.addElement(new StringElement(fileData.replaceAll(
			    System.getProperty("line.separator"), "<br>")
			    .replaceAll("(?s)<!DOCTYPE.*/head>", "")
			    .replaceAll("<br><br>", "<br>").replaceAll(
				    "<br>\\s<br>", "<br>").replaceAll("<\\?",
				    "&lt;").replaceAll("<(r|u|t)", "&lt;$1")));
		}
		catch (Exception e)
		{
		    ec.addElement(new BR());
		    ec
			    .addElement("The following error occurred while accessing the file: <");
		    ec.addElement(e.getMessage());
		}
	    }
	}
	catch (Exception e)
	{
	    s.setMessage("Error generating " + this.getClass().getName());
	    e.printStackTrace();
	}

	return (ec);
    }


    private int upDirCount(String fileName)
    {
	int count = 0;
	int startIndex = fileName.indexOf("..");
	while (startIndex != -1)
	{
	    count++;
	    startIndex = fileName.indexOf("..", startIndex + 1);
	}
	return count;
    }


    /**
     *  DOCUMENT ME!
     *
     * @return    DOCUMENT ME!
     */
    protected Category getDefaultCategory()
    {
	return AbstractLesson.A2;
    }


    /**
     *  Gets the hints attribute of the AccessControlScreen object
     *
     * @return    The hints value
     */
    protected List getHints()
    {
	List<String> hints = new ArrayList<String>();
	hints
		.add("Most operating systems allow special characters in the path.");
	hints
		.add("Use a file explorer to find the tomcat\\webapps\\WebGoat\\lesson_plans directory");
	hints.add("Try .. in the path");
	hints.add("Try ..\\..\\..\\LICENSE");

	return hints;
    }


    /**
     *  Gets the instructions attribute of the WeakAccessControl object
     *
     * @return    The instructions value
     */
    public String getInstructions(WebSession s)
    {
	String instructions = "The '"
		+ s.getUserName()
		+ "' user has access to all the files in the "
		+ "lesson_plans directory.  Try to break the access control mechanism and access a "
		+ "resource that is not in the listed directory.  After selecting a file to view, WebGoat "
		+ "will report if access to the file was granted.  An interesting file to try and obtain might "
		+ "be a file like tomcat/conf/tomcat-users.xml";

	return (instructions);
    }

    private final static Integer DEFAULT_RANKING = new Integer(115);


    protected Integer getDefaultRanking()
    {
    	return DEFAULT_RANKING;
    }


    /**
     *  Gets the title attribute of the AccessControlScreen object
     *
     * @return    The title value
     */
    public String getTitle()
    {
    	return ("How to Bypass a Path Based Access Control Scheme");
    }
}
