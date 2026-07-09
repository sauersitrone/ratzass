package de.simone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompilerProvider;

public class EsperUtils {

    private static final Logger log = LoggerFactory.getLogger(EsperUtils.class);

    // list of units lost
    public static String unitLost = "SELECT unit, count(*) as unitLost FROM UnitEvent WHERE status = 'DESTROYED' AND isEnemy = false GROUP BY unit;\n";

    // list of enemy units destroyed
    public static String enemyUnitDestroyed = "SELECT unit, count(*) as enemyUnitDestroyed FROM UnitEvent WHERE status = 'DESTROYED' AND isEnemy = true GROUP BY unit;\n";

    public static EPCompiled compileEPL(Configuration configuration) {

        // Units hit counts
        String unitHitCount = "SELECT unit, count(*) as hitCount FROM UnitEvent WHERE  GROUP BY unit;\n";

        // Units kill counts
        String unitKillCount = "SELECT unit, count(*) as killCount FROM UnitEvent GROUP BY unit;\n";

        // Compile EPL
        log.info("Compiling EPL");
        EPCompiled compiled;

        try {
            compiled = EPCompilerProvider.getCompiler().compile(
                    unitHitCount + unitKillCount + unitLost + enemyUnitDestroyed,
                    new CompilerArguments(configuration));
        } catch (EPCompileException ex) {
            throw new RuntimeException(ex);
        }

        return compiled;
    }
}