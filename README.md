Important: This project is not supported or maintained by Puppet Labs and is
incompatible with recent versions of PuppetDB.

# Java Client for the PuppetDB API

PuppetDB is the fast, scalable, and reliable data warehouse for Puppet. This client is
built on top of its [JSON based API](http://docs.puppetlabs.com/puppetdb/1.3/api/query/v2/query.html)
to ease the task of accessing PuppetDB from a remote client.

## Download
Prebuilt binaries of the client are available in the Maven repository at **T.B.D.** and
also available as an OSGi bundle from the P2/OBR repository **T.B.D.**

## Build from source
Building from source should be very straight forward provided that Maven (3.0
or later) is installed. Just clone the puppetlabs/puppetdb-javaclient git repository,
cd down to the root, and type:
```bash
mvn clean install -DskipTests
```
The build will need a live puppetdb instance to run the tests (don't worry. They are all
read-only unless the @Test annotations for the replaceFacts() and storeReport() are
uncommented) and it will need a private key and a certificate in a directory structure
that looks like this:
```
<ssl directory>/
  private_keys/
    <puppetdb host>.pem
  certs/
    <puppetdb host>.pem
```
Once that is set up, you should be able to build and test using this command
```bash
mvn -Dpuppetdb.hostname=<puppetdb host> -Dssldir=<ssl directory> install
```

## Packages

### Javaclient (com.puppetlabs.puppetdb.javaclient)
This package contains the top level interfaces and exceptions and would be the starting
point when using the library such as the _PuppetDBClientFactory_ that is used
for creating the _PuppetDBClient_ which then is used for obtaining various types of
data from the remote service.

### Model (com.puppetlabs.puppetdb.javaclient.model)
Contains model objects in the form of standard POJOs that maps to the JSON responses from
the PuppetDB server. It contains classes like Resource, Fact, Node, Event, and Report.

### Query  (com.puppetlabs.puppetdb.javaclient.query)
Contains the Query Builder. It contains what's needed to build all the query expressions
that are used by the PuppetDB Query API:

- comparisons using operators =, ~, &lt;, &lt;=, &gt; , &gt;=
- negation of expression using NOT operator
- combine expressions with AND or OR
- declare subqueries

### Impl (com.puppetlabs.puppetdb.javaclient.impl)
Contains classes that should be considered private, implementation specific, and not part
of the public API exposed by this library. In essence, it's a bad idea to make code dependent
on anything that resides in this package.


## Examples
### Obtaining the _PuppetDBClient_ instance
The easiest way to do that is by using the _PuppetDBClientFactory_.

```java
PuppetDBClient client = PuppetDBClientFactory.newClient("https://your.host:8081/"); 
```
Now the client can be used to retrieve data from the PuppetDB service.

```java
List<String> names = client.getFactNames();
```
### Using the _Query_ class
The _Query_ class implements a [Polish Notation](http://en.wikipedia.org/wiki/Polish_notation) style DSL that makes it easy to build advanced queries. It's recommended to do a static import of the methods to remove the need to qualify each call:
```java
import static com.puppetlabs.puppetdb.javaclient.query.Query.*;
```
#### Example 1, equality comparison:
This query will return all events that has the __certname__ equal to 'my.domain.com'
```java
List<Event> events = client.getEvents(eq(Event.CERTNAME, "my.comain.com"));
```
#### Example 2, OR query:
This query will resources that has the __title__ equal to 'main' or 'Settings'
```java
List<Resource> classes = client.getResources(
    or(eq(Resource.TITLE, "main"), eq(Resource.TITLE, "Settings")));
```
#### Example 3, Nodes using a Fact subquery:
The _Query_ class has two methods that will produce subquery expression. They are <code>inFacts()</code> and <code>inResources()</code>. Both will produce an expression that selects a specific column from the sub_table from a set limited by another sub_expression. A column in the parent expression must then match at least one of the values returned from the subquery. In essence, these subqueries form a traditional SQL IN expression:
```
parent_column IN (SELECT sub_column FROM sub_table WHERE sub_expression)
```
where sub_table is either _facts_ or _resources_.

This query will select all facts for the IP address of all nodes with Class[Apache]
```java
List<Fact> facts = client.getFacts(and(
    eq(Fact.NAME, "ipaddress"),
    inResources(
		Fact.CERTNAME, Resource.CERTNAME,
		  and(eq(Resource.TYPE, "Class"), eq(Resource.TITLE, "Apache")))));
```
In plain SQL lingo, this would translate into something like this:
```sql
SELECT * FROM Fact f
 WHERE f.name = 'ipaddress' AND f.certname IN (
  SELECT r.certname FROM Resource r
   WHERE r.type == 'Class' and r.title == 'Apache')
```
### Using qualifiers
When querying for resources it is possible to add two optional arguments, a _&lt;Type&gt;_ and a _&lt;Title&gt;_ directly as parameters to the <code>getResources()</code> or <code>getNodeResources()</code> methods. Similarly, when querying for facts, the two optional arguments _&lt;Name&gt;_ and a _&lt;Value&gt;_ can be used as optional parameters to the <code>getFacts()</code> and <code>getNodeFacts()</code> methods.

#### Example 4, Specifying the resource Type and Title as a qualifiers:
The following three queries will all query for resources of with _Class[Settings]_:
```java
client.getResources(and(eq(Resource.TYPE, "Class"), eq(Resource.TITLE, "Settings")));
client.getResources(eq(Resource.TITLE, "Settings"), "Class");
client.getResources(null, "Class", "Settings");
```
The reason for having multiple ways of performing the same query is that the underlying
[Query API endpoint](http://docs.puppetlabs.com/puppetdb/1.3/api/query/v2/resources.html) exposes shortcuts where the type and title can be specified as URL segments.
#### Example 5, Specifying the fact Name and Value as a qualifiers:
The facts endpoint is similar to the resources endpoint in that it also allows [similar URL segments](http://docs.puppetlabs.com/puppetdb/1.3/api/query/v2/facts.html). This is also visible in the Java API where the following three queries will all query for facts of with _ipaddress[192.168.0.1]_:
```java
client.getFacts(and(eq(Fact.NAME, "ipaddress"), eq(Fact.value, "192.168.0.1")));
client.getFacts(eq(Fact.value, "192.168.0.1") "ipaddress");
client.getFacts(null, "ipaddress", "192.168.0.1");
```
### Retrieving Metrics
PuppetDB makes a lot of useful [metrics available](http://docs.puppetlabs.com/puppetdb/1.3/api/query/v2/metrics.html) through its query API.
The _PuppetDBClient_ interface contains the methods <code>getMetrics()</code> and <code>getMetric(metricName)</code> to make metrics easy to work with.
#### Example 6, Listing available metrics
Available metrics are returned in a map where the key is the name of a valid MBean
and the value is a URI to use for requesting that MBeans attributes.
```java
Map<String,String> metrics = client.getMetrics();
```
#### Example 7, Retrieving metric attributes
Attributes for any named metric are made available as maps with a string key and an object
value. The value will always be a string, a number, or a boolean. Here's an example of how
to obtain the number of nodes in the population.
```java
Map<String,Object> metricAttributes = client.getMetric("com.puppetlabs.puppetdb.query.population:type=default,name=num-nodes");
System.println("The number of nodes is: " + metricAttributes.get("Value"));
```
