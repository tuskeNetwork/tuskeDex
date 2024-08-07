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

package tuskex.core.util.coin;

import tuskex.core.monetary.Price;
import tuskex.core.trade.TuskexUtils;
import tuskex.core.tsk.wallet.Restrictions;
import org.bitcoinj.core.Coin;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CoinUtilTest {

    @Test
    public void testGetPercentOfAmount() {
        BigInteger bi = new BigInteger("703100000000");
        assertEquals(new BigInteger("105465000000"), TuskexUtils.multiply(bi, .15));
    }

    @Test
    public void testGetFeePerTsk() {
        assertEquals(TuskexUtils.tskToAtomicUnits(1), TuskexUtils.multiply(TuskexUtils.tskToAtomicUnits(1), 1.0));
        assertEquals(TuskexUtils.tskToAtomicUnits(0.1), TuskexUtils.multiply(TuskexUtils.tskToAtomicUnits(0.1), 1.0));
        assertEquals(TuskexUtils.tskToAtomicUnits(0.01), TuskexUtils.multiply(TuskexUtils.tskToAtomicUnits(0.1), 0.1));
        assertEquals(TuskexUtils.tskToAtomicUnits(0.015), TuskexUtils.multiply(TuskexUtils.tskToAtomicUnits(0.3), 0.05));
    }

    @Test
    public void testParseTsk() {
        String tskStr = "0.266394780889";
        BigInteger au = TuskexUtils.parseTsk(tskStr);
        assertEquals(new BigInteger("266394780889"), au);
        assertEquals(tskStr, "" + TuskexUtils.atomicUnitsToTsk(au));
        assertEquals(tskStr, TuskexUtils.formatTsk(au, false));
    }

    @Test
    public void testMinCoin() {
        assertEquals(Coin.parseCoin("1"), CoinUtil.minCoin(Coin.parseCoin("1"), Coin.parseCoin("1")));
        assertEquals(Coin.parseCoin("0.1"), CoinUtil.minCoin(Coin.parseCoin("0.1"), Coin.parseCoin("1")));
        assertEquals(Coin.parseCoin("0.01"), CoinUtil.minCoin(Coin.parseCoin("0.1"), Coin.parseCoin("0.01")));
        assertEquals(Coin.parseCoin("0"), CoinUtil.minCoin(Coin.parseCoin("0"), Coin.parseCoin("0.05")));
        assertEquals(Coin.parseCoin("0"), CoinUtil.minCoin(Coin.parseCoin("0.05"), Coin.parseCoin("0")));
    }

    @Test
    public void testMaxCoin() {
        assertEquals(Coin.parseCoin("1"), CoinUtil.maxCoin(Coin.parseCoin("1"), Coin.parseCoin("1")));
        assertEquals(Coin.parseCoin("1"), CoinUtil.maxCoin(Coin.parseCoin("0.1"), Coin.parseCoin("1")));
        assertEquals(Coin.parseCoin("0.1"), CoinUtil.maxCoin(Coin.parseCoin("0.1"), Coin.parseCoin("0.01")));
        assertEquals(Coin.parseCoin("0.05"), CoinUtil.maxCoin(Coin.parseCoin("0"), Coin.parseCoin("0.05")));
        assertEquals(Coin.parseCoin("0.05"), CoinUtil.maxCoin(Coin.parseCoin("0.05"), Coin.parseCoin("0")));
    }

    @Test
    public void testGetAdjustedAmount() {
        BigInteger result = CoinUtil.getAdjustedAmount(
                TuskexUtils.tskToAtomicUnits(0.1),
                Price.valueOf("USD", 1000_0000),
                TuskexUtils.tskToAtomicUnits(0.2).longValueExact(),
                1);
        assertEquals(
                TuskexUtils.formatTsk(Restrictions.MIN_TRADE_AMOUNT, true),
                TuskexUtils.formatTsk(result, true),
                "Minimum trade amount allowed should be adjusted to the smallest trade allowed."
        );

        try {
            CoinUtil.getAdjustedAmount(
                    BigInteger.ZERO,
                    Price.valueOf("USD", 1000_0000),
                    TuskexUtils.tskToAtomicUnits(0.2).longValueExact(),
                    1);
            fail("Expected IllegalArgumentException to be thrown when amount is too low.");
        } catch (IllegalArgumentException iae) {
            assertEquals(
                    "amount needs to be above minimum of 0.1 tsk",
                    iae.getMessage(),
                    "Unexpected exception message."
            );
        }

        result = CoinUtil.getAdjustedAmount(
                TuskexUtils.tskToAtomicUnits(0.1),
                Price.valueOf("USD", 1000_0000),
                TuskexUtils.tskToAtomicUnits(0.2).longValueExact(),
                1);
        assertEquals(
                "0.10 TSK",
                TuskexUtils.formatTsk(result, true),
                "Minimum allowed trade amount should not be adjusted."
        );

        result = CoinUtil.getAdjustedAmount(
                TuskexUtils.tskToAtomicUnits(0.1),
                Price.valueOf("USD", 1000_0000),
                TuskexUtils.tskToAtomicUnits(0.25).longValueExact(),
                1);
        assertEquals(
                "0.10 TSK",
                TuskexUtils.formatTsk(result, true),
                "Minimum trade amount allowed should respect maxTradeLimit and factor, if possible."
        );

        // TODO(chirhonul): The following seems like it should raise an exception or otherwise fail.
        // We are asking for the smallest allowed BTC trade when price is 1000 USD each, and the
        // max trade limit is 5k sat = 0.00005 BTC. But the returned amount 0.00005 BTC, or
        // 0.05 USD worth, which is below the factor of 1 USD, but does respect the maxTradeLimit.
        // Basically the given constraints (maxTradeLimit vs factor) are impossible to both fulfill..
        result = CoinUtil.getAdjustedAmount(
                TuskexUtils.tskToAtomicUnits(0.1),
                Price.valueOf("USD", 1000_0000),
                TuskexUtils.tskToAtomicUnits(0.00005).longValueExact(),
                1);
        assertEquals(
                "0.00005 TSK",
                TuskexUtils.formatTsk(result, true),
                "Minimum trade amount allowed with low maxTradeLimit should still respect that limit, even if result does not respect the factor specified."
        );
    }
}
