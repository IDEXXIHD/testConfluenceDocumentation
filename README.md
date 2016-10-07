# testConfluenceDocumentation
Javadoc Doclet for test documentation.

Usage:

1. Install lib to maven repo


      **mvn clean install**

2. Add maven profile to the target project

 ```xml
        <profile>
            <id>testDocs</id>

            <dependencies>
                <dependency>
                    <groupId>com.sun</groupId>
                    <artifactId>tools</artifactId>
                    <version>1.7.0_09</version>
                    <scope>system</scope>
                    <systemPath>${java.home}/../lib/tools.jar</systemPath>
                </dependency>
            </dependencies>

            <build>
                <plugins>
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-javadoc-plugin</artifactId>
                        <version>2.10.4</version>
                        <configuration>
                            <doclet>com.idexx.labstation.javadoc.ConfluenceDoclet</doclet>
                            <docletArtifact>
                                <groupId>com.idexx.labstation</groupId>
                                <artifactId>javadoc</artifactId>
                                <version>1.0</version>
                            </docletArtifact>
                            <useStandardDocletOptions>false</useStandardDocletOptions>
                        </configuration>
                    </plugin>
                </plugins>
            </build>
        </profile>
 ```

3. Execute maven goal to generate docs


    **mvn -P testDocs javadoc:test-aggregate**
    
4. See generated docs in the **<project_root>/target/site/testapidocs/ConfluenceTestDocumentation.txt**