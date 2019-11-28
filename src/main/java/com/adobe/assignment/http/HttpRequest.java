package com.adobe.assignment.http;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.CharArrayBuffer;

import com.adobe.assignment.http.server.ServerConfig;

/**
 * An encapsulation of an HTTP request
 * 
 * This version:
 * 
 * Handles headers Handles query string parameters. A slight modification has
 * been done with regards to parsing the request. We do this using the
 * org.apache.http.message.LineParser to parse the request line of this
 * HttpRequest.
 * 
 * @author Prof. David Bernstein, James Madison University
 * @author Alfusainey Jallow, University of the Gambia
 * 
 * @version 0.1
 */
public class HttpRequest extends HttpMessage {
    private final static Logger log = Logger.getLogger(ServerConfig.LOGGER_ID);

    /**
     * The LineParser for parsing HTTP Request lines.
     */
    private final LineParser lineParser;

    private RequestLine requestLine;

    private final HttpInputStream inputStream;

    /**
     * Default Constructor
     */
    public HttpRequest(HttpInputStream inputStream) throws IOException {
        super();
        this.inputStream = inputStream;
        this.lineParser = BasicLineParser.DEFAULT;
    }

    /**
     * Returns the name of the HTTP method with which this request was made,
     * (for example, GET, POST, or PUT)
     * 
     * @return The HTTP method
     */
    public String getMethod( ) {
        if (requestLine != null)
            return requestLine.getMethod();
        else
            return null;
    }
    
    public ProtocolVersion getProtocolVersion(){
        if(requestLine != null)
            return requestLine.getProtocolVersion();
        else
            return null;
    }

    public String getRequestURI( ) {
        if (requestLine != null)
            return requestLine.getUri();
        else
            return null;
    }

    /**
     * Read this HttpRequest (up to, but not including, the content).
     * 
     * Note: The content is not read so that the content may be read in a
     * specialized way (e.g., as formatted binary data) or in case the content
     * is large (and should be "streamed"). @throws IOException @throws
     * 
     */
    public void read( ) throws IOException {
        String token;

        String line;
        if ((line = inputStream.readHttpLine()) != null && !line.equals("")){
            requestLine = createRequestLine(line);
            log.finest("Inputstream not ended!!" + line);
        }
        
        if(line != null)
            log.finest("\nRequest: " + line);

        while ((line = inputStream.readHttpLine()) != null && !line.equals("")) {
            CharArrayBuffer buf = new CharArrayBuffer(line.length() - 1);
            buf.append(line);
            Header header = lineParser.parseHeader(buf);
            headers.put(header.getName(), header.getValue());
            log.finest(header.getName() + ": " + header.getValue());
        }
        log.finest("*");
        // Process the headers
        // headers = NameValueMapper.createNameValueMap();
        // headers.putPairs(inputStream, ":");

        // Get the content length
        int cl = ((token = headers.getValue("Content-Length")) != null) ? Integer.parseInt(token) : -1;
        setContentLength(cl);
    }

    /**
     * Creates a RequestLine instance for the specified requestLine. Note:
     * Creation of the instance will fail(throw an exception) if the request
     * line String is not a valid HTTP request-line.
     *
     * @param requestLine
     * @return
     */
    private RequestLine createRequestLine( String requestLine ) {
        if (requestLine != null) {
            CharArrayBuffer buf = new CharArrayBuffer(requestLine.length() - 1);
            buf.append(requestLine);
            ParserCursor cursor = new ParserCursor(0, buf.length());
            return lineParser.parseRequestLine(buf, cursor);
        }
        return null;
    }

    /**
     * Returns a String representation of this Object
     * 
     * @return The String representation
     */
    public String toString( ) {
        Iterator< String > i;
        String name, s, value;

        s = "Method: \n\t" + getMethod() + "\n";
        s += "URI: \n\t" + getRequestURI() + "\n";

        s += "Headers:\n";
        i = getHeaderNames();
        while (i.hasNext()) {
            name = i.next();
            value = headers.getValue(name);
            s += "\t" + name + "\t" + value + "\n";
        }

        return s;
    }

    public void clear( ) {
        this.headers = NameValueMapper.createNameValueMap();
        content = null;
    }
    
    public RequestLine getRequestLine(){
        return this.requestLine;
    }

}
