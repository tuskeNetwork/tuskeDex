/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package tuskex.apitest.scenario;

import tuskex.apitest.config.ApiTestConfig;
import tuskex.apitest.method.BitcoinCliHelper;
import tuskex.apitest.scenario.bot.AbstractBotTest;
import tuskex.apitest.scenario.bot.BotClient;
import tuskex.apitest.scenario.bot.RobotBob;
import tuskex.apitest.scenario.bot.script.BashScriptGenerator;
import tuskex.apitest.scenario.bot.shutdown.ManualBotShutdownException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIf;

import static tuskex.apitest.Scaffold.BitcoinCoreApp.bitcoind;
import static tuskex.apitest.config.TuskexAppConfig.alicedaemon;
import static tuskex.apitest.config.TuskexAppConfig.arbdaemon;
import static tuskex.apitest.config.TuskexAppConfig.bobdaemon;
import static tuskex.apitest.config.TuskexAppConfig.seednode;
import static tuskex.apitest.scenario.bot.shutdown.ManualShutdown.startShutdownTimer;
import static org.junit.jupiter.api.Assertions.fail;

// The test case is enabled if AbstractBotTest#botScriptExists() returns true.
@EnabledIf("botScriptExists")
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ScriptedBotTest extends AbstractBotTest {

    private RobotBob robotBob;

    @BeforeAll
    public static void startTestHarness() {
        botScript = deserializeBotScript();

        if (botScript.isUseTestHarness()) {
            startSupportingApps(true,
                    true,
                    bitcoind,
                    seednode,
                    arbdaemon,
                    alicedaemon,
                    bobdaemon);
        } else {
            // We need just enough configurations to make sure Bob and testers use
            // the right apiPassword, to create a bitcoin-cli helper, and RobotBob's
            // gRPC stubs.  But the user will have to register dispute agents before
            // an offer can be taken.
            config = new ApiTestConfig("--apiPassword", "xyz");
            bitcoinCli = new BitcoinCliHelper(config);
            log.warn("Don't forget to register dispute agents before trying to trade with me.");
        }

        botClient = new BotClient(bobClient);
    }

    @BeforeEach
    public void initRobotBob() {
        try {
            BashScriptGenerator bashScriptGenerator = getBashScriptGenerator();
            robotBob = new RobotBob(botClient, botScript, bitcoinCli, bashScriptGenerator);
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    @Order(1)
    public void runRobotBob() {
        try {

            startShutdownTimer();
            robotBob.run();

        } catch (ManualBotShutdownException ex) {
            // This exception is thrown if a /tmp/bottest-shutdown file was found.
            // You can also kill -15 <pid>
            // of worker.org.gradle.process.internal.worker.GradleWorkerMain 'Gradle Test Executor #'
            //
            // This will cleanly shut everything down as well, but you will see a
            // Process 'Gradle Test Executor #' finished with non-zero exit value 143 error,
            // which you may think is a test failure.
            log.warn("{}  Shutting down test case before test completion;"
                            + "  this is not a test failure.",
                    ex.getMessage());
        } catch (Throwable throwable) {
            fail(throwable);
        }
    }

    @AfterAll
    public static void tearDown() {
        if (botScript.isUseTestHarness())
            tearDownScaffold();
    }
}
