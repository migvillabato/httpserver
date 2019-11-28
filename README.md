HTTP Server Implementation
==========================

*Author of this document: Miguel Villanueva Lobato.*

I recommend [StackEdit](https://stackedit.io/) to read this file.

## Brief Description ##
This is a partial simplified implementation of an HTTP server that handles GET/HEAD requests as well as a subset of conditional requests described in RFC 7232, among other information sources available on the Internet. It also supports persistent connections as specified in HTTP 1.0., i.e., connections explicitly requested through the 'Connection' header.
GET/HEAD client requests may obtain resources available on the server under a predefined directory known as the server root. If the target is an existing file, the client receives its contents, whereas, requests targeting a directory, obtain as response this list of its files and directories.

The server infrastructure consists of a thread pool, responsible of managing the JVM resources in a multi-threaded runtime environment. Thanks to this abstraction (interface ExecutorService since Java 1.5) we can forget about taking care of threads’ lifecycle. In case they stay idle for a long period of time, the thread manager will remove them from the pool and, when the application requires more threads than those existing in the pool, others are additionally created. Therefore this approach is very suitable for situations where small tasks need to be quickly dispatched, as it is the case with requests and responses in client-server architectures.

Fortunately I didn’t have to start the implementation from scratch. Specially pool configuration and socket administration was already tackled in one project hosted on GitHub (link: [Alfusainey Jallow’s server implementation](https://github.com/Alfusainey/httpserver)). Moreover, this project initially contained GET/HEAD handlers, that despite of incompleteness, where maintainable and had a good scalability. Therefore I chose Alfusainey’s project above others available on the web, which lack of good design and were very rigid for future extensions.
  
## Project structure##

 - **Package `http`** 
 
This is clearly the package that has suffered less alterations since the Alfusainey’s original project.
Abstractions of sockets and corresponding input-output streams to communicate with the client. 
Contains a very useful concept, `HttpMessage`, that allows to save and retrieve Header-Value pairs previously read from client requests. In fact, `HttpRequest` and `HttpResponse` are subclasses of `HttpMessage`. Requests contain very important information, e.g., the HTTP version, the URI and the type of request: only GET and HEAD supported in this case.

 - **Package `http.methods`** [refactored and extended]
 
Handlers dispatching GET/HEAD requests:  `GetMethodHandler` and `HeadMethodHandler`.
I had to adapt original behavior of this handlers to adapt them to the assignment requirements. Moreover, they stamp now etags and dates expressing when resource was last time modified.

- **Package `http.conditional`** [new]

New package containing all necessary handlers for conditional requests. Having a look at the RFCs I noticed that responses may vary for different method requests, specially between those that alter the state of server resources (PUT, DELETE...) and those that don’t (GET, HEAD). Therefore I strove to build up a completely separated layer designed to plug in new 'conditional request evaluators' for other methods but the already implemented, GET and HEAD.
This package is completely new in the existing project. For each supported conditional request there is one `ConditionEvaluator` , interface for:

`IfMatchEvaluator`, `IfNoneMatchEvaluator` and `IfModifiedSinceEvaluator`, both used by `ConditionalGetHeadHandler` in order to evaluate conditions stated by the client about resources on the server. In case of positive evaluation,  *ConditionalGetHeadHandler* uses *GetMethodHandler* and  *HeadMethodHandler* to perform the required actions, either retrieving resource contents or just metadata. 

 - **Package `http.log`** [new]
 
I added a very simple utility to log message. It is very useful to find the root of some problems when the server is in production.
 
 - Package `http.server` [refactored and extended]

The thread pool and a router class, `HttpConnectionHandler`, redirecting requests to the right handlers, either conditional or not. I added to *HttpConnectionHandler* the necessary logic to support persistent connections. 

 - Package `http.utils` [new]

Utils used by more than one package to format and parse dates, headers, etc.

##Key new Features##

 1. The package `http.conditional`
 2. Support for persistent connections.
 3. Get/Head methods handle directory requests by printing its content lists.
 4. Get/Head stamp etags and Last-Modified dates.
 2. Server may be configured with command line arguments.
 3. Updated all Apache components (IO, Core, Commons lang3 and Client).
 4. Remove all unnecessary libraries that I wasn’t using anymore.

##Testing##
I have implemented isolated test cases with Mockito and Hamcrest for all *condition evaluators* in package http.conditional and GET/HEAD handlers.
Added end-to-end test cases for conditional and normal handlers.
I performed also some manual tests using this client: [RestClient](https://github.com/wiztools/rest-client)

## Compilation ##
Requires:
 - Java 7 or higher.
 - JUnit 4.11
 - Mockito 1.10.19
 - Apache HttpClient 4.5.1
 - Apache IO 2.5
 - Apache HttpCore 4.4.5
 - Apache commons lang3 3.4

Luckily the solution is a Maven project and all dependencies are managed by this framework.

 - One option is to compile the project from Eclipse once imported the project as Maven project.
 - The second option is doing it manually. For that, run the following Maven command if you are located in the project root, directory called `HttpServer`.
 
> mvn assembly:assembly -DdescriptorId=jar-with-dependencies -DskipTests

In any case, I’ve provided the JAR with all dependencies one level above the project root, inside the directory `HttpServerAdobe`.

## Run the Server!##

This message is printed by the application when it is launched.

> java -jar HttpServer.jar [Port] [Server root] [Verbose mode]

 1.  Listening port (optional int): Server listening port for http requests. if not given: assumed value in properties. If not in properties, then '8080'.
 2.  Server root (optional string): Server root directory. if not given: assumed value in properties. If not in properties, then './www/' (directory 'www' in jar location).
 3.  Debug (optional bool): Turns off/on verbose mode. Values should be in form 'true'/'false'.

For example, the following command starts the server listening on port 8088 with server root www/ and verbose mode set to true.

> java -jar HttpServer.jar 8088 www/ true



