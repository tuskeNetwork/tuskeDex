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

package tuskex.desktop.util;

import tuskex.common.config.Config;
import tuskex.core.locale.Res;
import tuskex.core.util.coin.CoinFormatter;
import tuskex.core.util.coin.ImmutableCoinFormatter;
import org.bitcoinj.core.CoinMaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.bitcoinj.core.CoinMaker.oneBitcoin;
import static org.bitcoinj.core.CoinMaker.satoshis;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImmutableCoinFormatterTest {

    private final CoinFormatter formatter = new ImmutableCoinFormatter(Config.baseCurrencyNetworkParameters().getMonetaryFormat());

    @BeforeEach
    public void setUp() {
        Locale.setDefault(new Locale("en", "US"));
        Res.setBaseCurrencyCode("TSK");
        Res.setBaseCurrencyName("Monero");
    }

    @Test
    public void testFormatCoin() {
        assertEquals("1.00", formatter.formatCoin(oneBitcoin));
        assertEquals("1.0000", formatter.formatCoin(oneBitcoin, 4));
        assertEquals("1.00", formatter.formatCoin(oneBitcoin, 5));
        assertEquals("0.000001", formatter.formatCoin(make(a(CoinMaker.Coin).but(with(satoshis, 100L)))));
        assertEquals("0.00000001", formatter.formatCoin(make(a(CoinMaker.Coin).but(with(satoshis, 1L)))));
    }
}
