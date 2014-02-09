package com.coherentlogic.coherent.data.model.core.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class provides various utility methods which are shared amongst several
 * modules.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public class Utils {

    public static final String GMT_MINUS_6 = "GMT-6";

    public static final TimeZone GMT_MINUS_6_TIMEZONE = TimeZone.getTimeZone(
        GMT_MINUS_6);

    public static final Calendar TODAY =
        GregorianCalendar.getInstance(GMT_MINUS_6_TIMEZONE);

    /**
     * Method created a date with the time zone set to GMT-6.
     */
    public static Date using (int year, int month, int day) {

        Calendar calendar = Calendar.getInstance(GMT_MINUS_6_TIMEZONE);

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        return calendar.getTime();
    }

    /**
     * Method iterates over all children under the <i>targetElement</i> and, for
     * each one with a tag name equal to <i>tagName</i>, executes the <i>action
     * </i>, passing in that child element.
     */
    public static void forEachChild (
        Element targetElement,
        String tagName,
        Action<Element> action
    ) {

        List<Element> children = null;

        if (targetElement != null && targetElement.hasChildNodes())
            children =
                DomUtils.getChildElementsByTagName(targetElement, tagName);

        if (children != null)
            for (Element next : children)
                action.execute(next);
    }

    /**
     * Method reads in the <i>xml</i> and returns the top level <i>document</i>.
     */
    public static Document toDocument (String xml)
        throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource source = new InputSource (new StringReader(xml));

        return builder.parse ( source );
    }
}
