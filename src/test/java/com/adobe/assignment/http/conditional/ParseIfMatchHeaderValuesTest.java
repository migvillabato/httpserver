package com.adobe.assignment.http.conditional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ParseIfMatchHeaderValuesTest {
   
    @Test
    public void testThatWeakValidatorAreOmitted( )throws Exception {
        List<String> mu = new ArrayList<String>();
        mu.add("ab");
        mu.add(" ce");
        String ifMatchValue = "W/\"xyzzy\", \"r2d2xxxx\", W/\"c3piozzzz\"";
        List<String> components;
        components = ConditionalUtils.parseIfMatchHeaderValues(ifMatchValue);
        assertThat(components, hasSize(1));
        assertThat(components, contains("r2d2xxxx"));
    }
    
    @Test
    public void testAllStrongValidators( )throws Exception {
        String ifMatchValue = "\"xyzzy\", \"r2d2xxxx\", \"c3piozzzz\"";
        List<String> components;
        components = ConditionalUtils.parseIfMatchHeaderValues(ifMatchValue);
        assertThat(components, contains("xyzzy", "r2d2xxxx", "c3piozzzz"));
    }
    
    @Test
    public void testPatterWithoutCommasInside( )throws Exception {
        String ifMatchValue = "\"xyzzy\"";
        List<String> components;
        components = ConditionalUtils.parseIfMatchHeaderValues(ifMatchValue);
        assertThat(components, contains("xyzzy"));
    }
    
    @Test(expected=ParseException.class)
    public void testPatternWithQuotesInside( )throws Exception {
        String ifMatchValue = "\"xy\"zzy\"";
        ConditionalUtils.parseIfMatchHeaderValues(ifMatchValue);
    }
    
    @Test
    public void testPatternsCommaSeparatedWithSpaces( )throws Exception {
        String ifMatchValue = "\"xyzzy\", \"xyzzy\"";
        List<String> components;
        components = ConditionalUtils.parseIfMatchHeaderValues(ifMatchValue);
        assertThat(components, contains("xyzzy", "xyzzy"));
    }
    
    @Test(expected=ParseException.class)
    public void testPatternsCommaSeparatedWithoutSpaces( )throws Exception {
        String ifMatchValue = "\"xyzzy\",\"xyzzy\"";
        ConditionalUtils.parseIfMatchHeaderValues(ifMatchValue);
    }
    
    @Test
    public void testWildcard( )throws Exception {
        String ifMatchValue = "*";
        List<String> components;
        components = ConditionalUtils.parseIfMatchHeaderValues(ifMatchValue);
        assertThat(components, contains("*"));
    }
}
