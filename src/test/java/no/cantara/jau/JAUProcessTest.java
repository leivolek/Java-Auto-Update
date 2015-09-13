package no.cantara.jau;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.ir.annotations.Ignore;
import no.cantara.jau.serviceconfig.client.ConfigServiceClient;
import no.cantara.jau.serviceconfig.client.ConfigurationStoreUtil;
import no.cantara.jau.serviceconfig.client.DownloadUtil;
import no.cantara.jau.serviceconfig.dto.ServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import static java.util.concurrent.TimeUnit.MILLISECONDS;



public class JAUProcessTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> restarterHandle;
    private ApplicationProcess processHolder;
    private static final Logger log = LoggerFactory.getLogger(JAUProcessTest.class);




    @BeforeClass
    public void startServer() throws InterruptedException {
        processHolder = new ApplicationProcess();

    }


    @AfterClass
    public void stop() {
        processHolder.startProcess();
        restarterHandle.cancel(true);

    }


    @Ignore
    @Test
    public void testProcessDownloadStartupAndRunning() throws Exception {


        String jsonResponse = "{  \n" +
                "   \"name\":\"Service1-1.23\",\n" +
//                "   \"changedTimestamp\":\"2015-08-11T10:13:12.141Z\",\n" +
                "   \"downloadItems\":[  \n" +
                "      {  \n" +
                "         \"url\":\"http://mvnrepo.capraconsulting.no/service/local/artifact/maven/redirect?r=nmdsnapshots&g=no.asa.as&a=as-agent&v=0.7-SNAPSHOT&p=jar\",\n" +
                "         \"username\":\"as\",\n" +
                "         \"password\":\"hjhk\",\n" +
                "         \"metadata\":{  \n" +
                "            \"groupId\":\"no.xx.armacy\",\n" +
                "            \"artifactId\":\"pharmacy-agent\",\n" +
                "            \"version\":\"0.7-SNAPSHOT\",\n" +
                "            \"packaging\":\"jar\",\n" +
                "            \"lastUpdated\":null,\n" +
                "            \"buildNumber\":null\n" +
                "         }\n" +
                "      }\n" +
                "   ],\n" +
                "   \"configurationStores\":[  \n" +
                "      {  \n" +
                "         \"fileName\":\"config_override.properties\",\n" +
                "         \"properties\":{  \n" +
                "            \"stocklevel.aws.accessKey\":\"\",\n" +
                "            \"stocklevel.aws.secretKey\":\"jRTh7lv+\",\n" +
                "            \"stocklevel.aws.destination.name\":\"as\",\n" +
                "            \"stocklevel.aws.region\":\"us-east-1\",\n" +
                "            \"jms.rest.host\":\"localhost\"\n" +
                "         }\n" +
                "      }\n" +
                "   ],\n" +
                "   \"startServiceScript\":\"java -DDEMO_MODE=true -jar pharmacy-agent-0.7-SNAPSHOT.jar\"\n" +
                "}";

        // let us type a configuration the quick way..
        ServiceConfig serviceConfig = mapper.readValue(jsonResponse, ServiceConfig.class);

        // Process stuff
        ApplicationProcess processHolder= new ApplicationProcess();
        processHolder.setWorkingDirectory(new File("./"));
        String workingDirectory = processHolder.getWorkingDirectory().getAbsolutePath();


        // Download stuff
        DownloadUtil.downloadAllFiles(serviceConfig.getDownloadItems(), workingDirectory);
        ConfigurationStoreUtil.toFiles(serviceConfig.getConfigurationStores(), workingDirectory);

        // Lets try to start
        String initialCommand = serviceConfig.getStartServiceScript();
        int updateInterval=100;

        System.out.println("Initial command: "+initialCommand);
        processHolder.setWorkingDirectory(new File(workingDirectory));
        processHolder.setCommand(initialCommand.split("\\s+"));

        processHolder.startProcess();

        restarterHandle = scheduler.scheduleAtFixedRate(
                () -> {

                    try {
                        // Restart, whatever the reason the process is not running.
                        if (!processHolder.processIsrunning()) {
                            log.debug("Process is not running - restarting... clientId={}, lastChanged={}, command={}",
                                    processHolder.getClientId(), processHolder.getLastChangedTimestamp(), processHolder.getCommand());
                            processHolder.startProcess();
                        }
                    } catch (Exception e) {
                        log.debug("Error thrown from scheduled lambda.", e);
                    }
                },
                1, updateInterval, MILLISECONDS
        );


        Thread.sleep(4000);
        assertTrue(processHolder.processIsrunning(), "First check");
        Thread.sleep(1000);
        assertTrue(processHolder.processIsrunning(), "Second check");

        processHolder.stopProcess();
        assertFalse(processHolder.processIsrunning(), "Seventh check");
        Thread.sleep(4000);
        assertTrue(processHolder.processIsrunning(), "Eigth check");

    }

    private static String getStringProperty(final Properties properties, String propertyKey, String defaultValue) {
        String property = properties.getProperty(propertyKey, defaultValue);
        if (property == null) {
            //-Dconfigservice.url=
            property = System.getProperty(propertyKey);
        }
        return property;
    }


}