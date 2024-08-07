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

package tuskex.apitest.method.offer;

import tuskex.apitest.method.MethodTest;
import tuskex.cli.CliMain;
import tuskex.cli.table.builder.TableBuilder;
import tuskex.proto.grpc.OfferInfo;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import protobuf.PaymentAccount;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.function.Function;

import static tuskex.apitest.Scaffold.BitcoinCoreApp.bitcoind;
import static tuskex.apitest.config.ApiTestConfig.TSK;
import static tuskex.apitest.config.TuskexAppConfig.alicedaemon;
import static tuskex.apitest.config.TuskexAppConfig.arbdaemon;
import static tuskex.apitest.config.TuskexAppConfig.bobdaemon;
import static tuskex.apitest.config.TuskexAppConfig.seednode;
import static tuskex.cli.table.builder.TableType.OFFER_TBL;
import static java.lang.String.format;
import static java.lang.System.out;

@Slf4j
public abstract class AbstractOfferTest extends MethodTest {

    protected static final int ACTIVATE_OFFER = 1;
    protected static final int DEACTIVATE_OFFER = 0;
    protected static final String NO_TRIGGER_PRICE = "0";

    @Setter
    protected static boolean isLongRunningTest;

    protected static PaymentAccount alicesBtcAcct;
    protected static PaymentAccount bobsBtcAcct;

    protected static PaymentAccount alicesTskAcct;
    protected static PaymentAccount bobsTskAcct;

    @BeforeAll
    public static void setUp() {
        setUp(false);
    }

    public static void setUp(boolean startSupportingAppsInDebugMode) {
        startSupportingApps(true,
                startSupportingAppsInDebugMode,
                bitcoind,
                seednode,
                arbdaemon,
                alicedaemon,
                bobdaemon);
        initPaymentAccounts();
    }

    protected static final Function<OfferInfo, String> toOfferTable = (offer) ->
            new TableBuilder(OFFER_TBL, offer).build().toString();

    protected static final Function<List<OfferInfo>, String> toOffersTable = (offers) ->
            new TableBuilder(OFFER_TBL, offers).build().toString();

    protected static String calcPriceAsString(double base, double delta, int precision) {
        var mathContext = new MathContext(precision);
        var priceAsBigDecimal = new BigDecimal(Double.toString(base), mathContext)
                .add(new BigDecimal(Double.toString(delta), mathContext))
                .round(mathContext);
        return format("%." + precision + "f", priceAsBigDecimal.doubleValue());
    }

    @SuppressWarnings("ConstantConditions")
    public static void initPaymentAccounts() {
        alicesBtcAcct = aliceClient.getPaymentAccount("BTC");
        bobsBtcAcct = bobClient.getPaymentAccount("BTC");
    }

    @SuppressWarnings("ConstantConditions")
    public static void createTskPaymentAccounts() {
        alicesTskAcct = aliceClient.createCryptoCurrencyPaymentAccount("Alice's TSK Account",
                TSK,
                "44G4jWmSvTEfifSUZzTDnJVLPvYATmq9XhhtDqUof1BGCLceG82EQsVYG9Q9GN4bJcjbAJEc1JD1m5G7iK4UPZqACubV4Mq",
                false);
        log.trace("Alices TSK Account: {}", alicesTskAcct);
        bobsTskAcct = bobClient.createCryptoCurrencyPaymentAccount("Bob's TSK Account",
                TSK,
                "4BDRhdSBKZqAXs3PuNTbMtaXBNqFj5idC2yMVnQj8Rm61AyKY8AxLTt9vGRJ8pwcG4EtpyD8YpGqdZWCZ2VZj6yVBN2RVKs",
                false);
        log.trace("Bob's TSK Account: {}", bobsTskAcct);
    }

    @AfterAll
    public static void tearDown() {
        tearDownScaffold();
    }

    protected static void runCliGetOffer(String offerId) {
        out.println("Alice's CLI 'getmyoffer' response:");
        CliMain.main(new String[]{"--password=xyz", "--port=9998", "getmyoffer", "--offer-id=" + offerId});
        out.println("Bob's CLI 'getoffer' response:");
        CliMain.main(new String[]{"--password=xyz", "--port=9999", "getoffer", "--offer-id=" + offerId});
    }
}
