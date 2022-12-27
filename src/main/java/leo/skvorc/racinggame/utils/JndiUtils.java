package leo.skvorc.racinggame.utils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class JndiUtils {
    private static final String PROVIDER_URL = "file:C:/temp";
    private static final String CONFIGURATION_FILE_NAME = "config.properties";

    private static InitialContext context;

    private static InitialContext getInitialContext() throws NamingException {
        if (context == null) {
            Properties properties = new Properties();
            properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
            properties.setProperty(Context.PROVIDER_URL, PROVIDER_URL);
            context = new InitialContext(properties);
        }

        return context;
    }

    public static String getConfigurationParameter(String paramName) throws NamingException, IOException {
        Object configurationFileName = getInitialContext().lookup(CONFIGURATION_FILE_NAME);
        Properties configurationProperties = new Properties();
        configurationProperties.load(new FileReader(configurationFileName.toString()));
        return configurationProperties.getProperty(paramName);
    }

}
