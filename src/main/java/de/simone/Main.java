package de.simone;

import java.util.LinkedList;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.runtime.client.DeploymentOptions;
import com.espertech.esper.runtime.client.EPDeployException;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPRuntimeProvider;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static EPRuntime ePRuntime;

    private static void initializeEsper() {
        log.info("creating configuration");
        Configuration configuration = new Configuration();
        configuration.getCommon().addEventType(UnitEvent.class);

        EPCompiled compiled = EsperUtils.compileEPL(configuration);

        log.info("Setting up runtime");
        ePRuntime = EPRuntimeProvider.getRuntime("default", configuration);
        ePRuntime.initialize();

        log.info("Deploying compiled EPL");
        try {
            ePRuntime.getDeploymentService().deploy(compiled, new DeploymentOptions().setDeploymentId("stockticker"));
        } catch (EPDeployException ex) {
            throw new RuntimeException(ex);
        }

        log.info("Esper initialized");
    }

    public static void main(String[] args) {

        initializeEsper();
        Env.init();
        Ratzass ratzass = new Ratzass();
        Ratzass.ePRuntime = ePRuntime;
        ratzass.run();
    }
}
