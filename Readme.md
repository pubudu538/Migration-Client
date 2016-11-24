##Migration Client for WSO2 API Manager

This is a migration client for WSO2 API Manger 2.0.0

This client does the following.

1. Replace the handler org.wso2.carbon.apimgt.gateway.handlers.throttling.APIThrottleHandler with 
org.wso2.carbon.apimgt.gateway.handlers.throttling.ThrottleHandler
2. Add the following property in the Insequences in the API.

```xml
<property name="api.ut.requestTime" expression="get-property('SYSTEM_TIME')"/>
```


###How to build

1. Following dependency is needed. 

```xml
      <groupId>org.wso2.carbon</groupId>
      <artifactId>org.wso2.carbon.apimgt.migrate.client</artifactId>
      <version>2.0.x-2</version>
```

Above is the official migration client shipped by WSO2. This client uses some of the classes from above mentioned dependency. 

2. Run mvn clean install command from the parent directory.

3. Distribution zip file can be found in the target folder.

###How to run

1. Unzip the distribution pack.

2. Run as follows.

`<Pack_Home>/bin/run.sh <repo_path> <options=TH,UR>`

example: `/home/migration-client/bin/run.sh /home/files options=TH,UR`

 - TH = Run throttling migration
 - UR = Run update request time property
 - default = Run both throttling and update request migration.

Note: This can be run in nohup mode as well. Uncomment nohup mode and comment default mode.