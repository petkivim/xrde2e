/*
 * The MIT License
 *
 * Copyright 2016 Petteri Kivimäki
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.pkrete.xrde2e.client.util;

import com.pkrete.xrd4j.common.exception.XRd4JException;
import com.pkrete.xrd4j.common.member.ConsumerMember;
import com.pkrete.xrd4j.common.member.ProducerMember;
import com.pkrete.xrd4j.common.member.SecurityServer;
import com.pkrete.xrd4j.common.message.ServiceRequest;
import com.pkrete.xrd4j.common.util.MessageHelper;
import com.pkrete.xrde2e.client.member.E2EProducerMember;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * This class offers helper methods for the application.
 *
 * @author Petteri Kivimäki
 */
public class ApplicationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationHelper.class);
    private static String jarDir;

    /**
     * Private constructor that hides the implicit public one.
     */
    private ApplicationHelper() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Returns the absolute path of the jar file containing the application. The
     * path is returned with a trailing slash.
     *
     * @return absolute path of the current working directory
     */
    public static String getJarPath() {
        LOGGER.debug("Load jar directory.");
        if (jarDir != null && !jarDir.isEmpty()) {
            LOGGER.debug("Jar directory already loaded! Use cached value : \"{}\".", jarDir);
            return jarDir;
        }
        int limit = 5;
        if ("Win".equals(System.getProperty("os.name").substring(0, 3))) {
            limit = 6;
        }
        String temp = ApplicationHelper.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(limit);
        String[] arr = temp.split("/");
        temp = temp.replace("%20", " ");
        try {
            jarDir = temp.replace(arr[arr.length - 1], "");
            jarDir = jarDir.replaceAll("/+$", "/");
            LOGGER.info("Jar directory loaded : \"{}\".", jarDir);
            return jarDir;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Loads Log4J configuration.
     */
    public static void configureLog4j() {
        LOGGER.debug("Configure Log4J.");
        // Get "log4jConfDirectory" system property
        String log4jDirectoryParameter = System.getProperty(Constants.PROPERTIES_LOG4J_DIR_PARAM_NAME);
        File logConfProperty = new File(log4jDirectoryParameter + Constants.LOG4J_SETTINGS_FILE);
        // Get jar path for checking if it contains log4j.xml configuration
        String path = ApplicationHelper.getJarPath();
        File logConf = new File(path + Constants.LOG4J_SETTINGS_FILE);
        // Execution order system property, jar path, inside jar file
        if (logConfProperty.exists()) {
            DOMConfigurator.configure(logConfProperty.getAbsolutePath());
            LOGGER.info("Logging configuration loaded from " + logConfProperty.getAbsolutePath());
        } else if (logConf.exists()) {
            DOMConfigurator.configure(logConf.getAbsolutePath());
            LOGGER.info("Logging configuration loaded from " + logConf.getAbsolutePath());
        } else {
            DOMConfigurator.configure(ApplicationHelper.class.getClassLoader().getResource(Constants.LOG4J_SETTINGS_FILE));
            LOGGER.info("Couldn't find external configuration files. Use default configuration.");
        }
        LOGGER.debug("Loaded Log4J.");
    }

    /**
     * Parses the string argument as a signed decimal integer. If parsing of the
     * string fails, zero is returned.
     *
     * @param source a String containing the integer representation to be parsed
     * @return the integer value represented by the argument in decimal
     */
    public static int strToInt(String source) {
        try {
            return Integer.parseInt(source);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /**
     * Converts the given milliseconds to string.
     *
     * @param milliseconds time to be converted
     * @return string
     */
    public static String millisecondsToString(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        return hours + " h " + minutes + " min " + seconds + " s";
    }

    /**
     * Goes through the given properties and extracts all the defined target
     * producer members and security servers. Returns a list containing
     * ServiceRequest objects.
     *
     * @param settings application properties
     * @param consumer ConsumerMember object
     * @return List containing ServiceRequest objects
     */
    public static List<ServiceRequest> extractTargets(Properties settings, ConsumerMember consumer) {
        List<ServiceRequest> targets = new ArrayList<>();

        LOGGER.info("Start extracting targets from properties.");
        if (settings == null || settings.isEmpty()) {
            LOGGER.warn("No targets were founds. The list was null or empty.");
            return targets;
        }

        int i = 0;
        String key = Integer.toString(i);

        // Loop through all the endpoints
        while (settings.containsKey(key + "." + Constants.PROPERTIES_SUBSYSTEM) && settings.containsKey(key + "." + Constants.PROPERTIES_SERVER)) {

            String subsystem = settings.getProperty(key + "." + Constants.PROPERTIES_SUBSYSTEM);
            String server = settings.getProperty(key + "." + Constants.PROPERTIES_SERVER);
            String label = settings.getProperty(key + "." + Constants.PROPERTIES_LABEL);

            // Validate subsystem and server value. Returned Matcher contains
            // four groups that are used for creating a new
            // SecurityServer object.
            Matcher matcher = validateSubsystemAndServer(subsystem, server);

            // If matcher is null, subsystem and/or server values are invalid
            if (matcher == null) {
                // Increase counter by one
                i++;
                // Update counter
                key = Integer.toString(i);
                // Jump to next
                continue;
            }

            // Check label for null and set value to "" if needed
            if (label == null) {
                LOGGER.debug("Label is null. Set value to \"\".");
                label = "";
            }

            // Get subsystem parts that are used for creating a new
            // ProducerMember
            String[] partsSubsystem = subsystem.split("\\.");

            try {
                ProducerMember producer = new E2EProducerMember(partsSubsystem[0], partsSubsystem[1], partsSubsystem[2], partsSubsystem[3], "listMethods", label);
                SecurityServer securityServer = new SecurityServer(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
                // Create a new ServiceRequest object, unique message id is generated by MessageHelper.
                // Type of the ServiceRequest is the type of the request data (String in this case)
                ServiceRequest<String> request = new ServiceRequest<>(consumer, producer, MessageHelper.generateId());
                // Set security server
                request.setSecurityServer(securityServer);
                targets.add(request);
                LOGGER.info("New producer found: \"{}\"", producer.toString());
                LOGGER.info("New server found: \"{}\"", securityServer.toString());
            } catch (XRd4JException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }

            // Increase counter by one
            i++;
            // Update key
            key = Integer.toString(i);
        }

        LOGGER.info("{} targets extracted from properties.", targets.size());
        return targets;
    }

    private static Matcher validateSubsystemAndServer(String subsystem, String server) {
        if (subsystem == null || subsystem.isEmpty() || server == null || server.isEmpty()) {
            LOGGER.warn("Subsystem or server is null or empty. Target skipped.");
            return null;
        }
        if (subsystem.split("\\.").length != 4) {
            LOGGER.warn("Subsystem is invalid: \"{}\". Target is skipped.", subsystem);
            return null;
        }
        String pattern = "(.+?)\\.(.+?)\\.(.+?)\\.(.+)";
        Pattern regex = Pattern.compile(pattern);
        Matcher serverMatcher = regex.matcher(server);
        if (!serverMatcher.find()) {
            LOGGER.warn("Server is invalid: \"{}\". Target is skipped.", server);
            return null;
        }
        return serverMatcher;
    }

    public static ConsumerMember extractConsumer(String consumerStr) {
        if (consumerStr == null || consumerStr.isEmpty()) {
            LOGGER.error("Consumer string is null or empty. Target skipped.");
            return null;
        }
        String[] parts = consumerStr.split("\\.");
        if (parts.length != 4) {
            LOGGER.error("Consumer is invalid: \"{}\". Target is skipped.", consumerStr);
            return null;
        }

        try {
            ConsumerMember consumer = new ConsumerMember(parts[0], parts[1], parts[2], parts[3]);
            LOGGER.info("Consumer found: \"{}\"", consumer.toString());
            return consumer;
        } catch (XRd4JException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }
}
