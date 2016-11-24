package org.carbon.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;


/**
 * Created by pubudu on 11/23/16.
 */
public class App {

    private static Log log = LogFactory.getLog(App.class);

    public static void main(String[] args) {
        PropertyConfigurator.configure(System.getProperty("log4j.properties.file.path", "src/main/conf/log4j.properties"));

        log.info("Migration client started!!!");

        String repoPath;
        String options = "options=default";

        if (args.length == 0) {
            log.info("Provide a Synapse Files location!!!");
            log.info("./run.sh <repo_path> <options=TH,UR>");
            log.info("example: ./run.sh /home/files options=TH,UR");
            log.info("TH - Run throttling migration");
            log.info("UR - Run update request time property");
            System.exit(1);
        } else if (args.length == 2) {
            options = args[1];
        }

        repoPath = args[0];
        Client client = new Client(repoPath);
        String optionList[] = options.split("=");
        String optionValues[] = optionList[1].split(",");

        for (String option : optionValues) {
            if (option.equals("TH")) {
                client.migrateThrottlingHandler();
            } else if (option.equals("UR")) {
                client.updateRequestTimeProperty();
            } else if (option.equals("default")) {
                client.migrateThrottlingHandler();
                client.updateRequestTimeProperty();
            }
        }

        log.info("Migration is completed!!!");

    }
}
