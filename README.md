# Java-Auto-Update
Java Auto-Update - wrapper to automatically upgrade a Java application. 


## Install on Windows 

1. Download zip file from http://mvnrepo.cantara.no/content/repositories/snapshots/no/cantara/jau/java-auto-update/1.0-SNAPSHOT/java-auto-update-1.0-20150731.054516-1.zip 
2. unzip 
3. Update configServiceUrl in config_override/jau.properties
4. Execute download-java.bat
5. Open command prompt with 'Run as Administrator'
6. cd java-auto-update-1.0-SNAPSHOT\bin
7. java-auto-update remove & java-auto-update install
8. java-auto-update start

Tip! If it fails to start, check the wrapper.log.   


## Run on Linux 

1. wget http://mvnrepo.cantara.no/content/repositories/snapshots/no/cantara/jau/java-auto-update/1.0-SNAPSHOT/java-auto-update-1.0-20150731.054516-1.jar
2. java -DconfigServiceUrl=http://localhost:7000/jau/serviceconfig/query?clientid=clientid1 -jar java-auto-update-1.0-20150731.054516-1.jar


## Configuration


If JAU is running behind a proxy, use the following properties
* "http.useProxy"
* "http.proxyPort"
* "http.proxyHost"
* "https.proxyHost"
* "https.proxyPort"

jau.properties
```
configservice.url=http://localhost:8086/jau/client
configservice.username=read
configservice.password=baretillesing
configservice.artifactid=cantara-demo

updateinterval=60
isrunninginterval=40

clientName=local-jau

monitor.events=testkey

# "startPattern" is a regex defining the start of a log entry. Setting this property causes multi-line
# log entries to be collated before being sent to ConfigService.
# For example, if your log entries starts with a UTC timestamp (e.g., 2016-06-09T14:01:05.348) you can
# use the following regex:

# startPattern=\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}
```


## Example application configuration JAU receives from ConfigService

```
{
  "name": "hello-world_0.1-SNAPSHOT",
  "lastChanged": "2016-03-09T07:50:18.994Z",
  "downloadItems": [
    {
      "url": "repository-url/hello-world-0.1-SNAPSHOT.jar",
      "username": "basic-auth-username",
      "password": "basic-auth-password",
      "metadata": {
        "groupId": "com.example",
        "artifactId": "hello-world-service",
        "version": "0.1-SNAPSHOT",
        "packaging": "jar",
        "lastUpdated": null,
        "buildNumber": null
      }
    }
  ],
  "configurationStores": [
    {
      "fileName": "helloworld_overrides.properties",
      "properties": {
        "hello.world.message": "Hello World"
      }
    }
  ],
  "eventExtractionConfigs" : [ {
     "groupName" : "hw-agent",
     "tags" : [ {
       "tagName" : "jau",
       "regex" : ".*",
       "filePath" : "logs/jau.log"
     }, {
       "tagName" : "agent",
       "regex" : ".*",
       "filePath" : "logs/hwagent.log"
     } ]
   } ],

  ],
  "startServiceScript": "java -jar hello-world-0.1-SNAPSHOT.jar"
}
```

## Develop

Java Auto-Update depends on ConfigService, so this must be available in your maven repositories.
You can download ConfigService from github: https://github.com/Cantara/ConfigService

