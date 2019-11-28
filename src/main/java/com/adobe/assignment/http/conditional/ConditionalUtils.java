package com.adobe.assignment.http.conditional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ConditionalUtils {

    // static public String[] splitStringWithCommaSeparator( String someString )
    // {
    // String[] components = someString.split(",");
    // return components;
    // }

    /**
     * This implementation is a simplification. I didn't find some 'utils' in
     * apache libraries to parse correctly the values contained in If-Match and
     * If-None-Match headers.
     * <p>
     * Weak validators are omitted.
     * 
     * @param ifMatchHeader
     * @return The validators to be compared against a strong validator.
     * @throws ParseException
     */
    static public List< String > parseIfMatchHeaderValues( String ifMatchHeader ) throws ParseException {
        Pattern p = Pattern.compile("((W/){0,1}\"[^\"]*\"(, (W/){0,1}\"[^\"]*\")*)|\\*");
        Matcher matcher = p.matcher(ifMatchHeader);
        if (!matcher.matches())
            throw new ParseException(ifMatchHeader, 0);
        String[] components = ifMatchHeader.split(",");

        List< String > componentsList = new ArrayList< String >(Arrays.asList(components));
        ListIterator< String > iter = componentsList.listIterator();
        while (iter.hasNext()) {
            String next = iter.next();
            if (!next.equals("*")) {
                next = next.replaceAll("\\s", "");
                if (next.startsWith("W/")) {
                    iter.remove();
                    continue;
                }
                next = StringUtils.substringBetween(next, "\"");
                iter.set(next);
            }
        }
        return componentsList;
    }
}
