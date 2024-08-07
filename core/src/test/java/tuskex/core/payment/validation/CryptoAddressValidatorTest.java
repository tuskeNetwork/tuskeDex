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

package tuskex.core.payment.validation;

import tuskex.asset.AssetRegistry;
import tuskex.common.config.BaseCurrencyNetwork;
import tuskex.common.config.Config;
import tuskex.core.locale.CurrencyUtil;
import tuskex.core.locale.Res;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CryptoAddressValidatorTest {

    @Test
    public void test() {
        CryptoAddressValidator validator = new CryptoAddressValidator(new AssetRegistry());

        BaseCurrencyNetwork baseCurrencyNetwork = Config.baseCurrencyNetwork();
        String currencyCode = baseCurrencyNetwork.getCurrencyCode();
        Res.setBaseCurrencyCode(currencyCode);
        Res.setBaseCurrencyName(baseCurrencyNetwork.getCurrencyName());
        CurrencyUtil.setBaseCurrencyCode(currencyCode);

        validator.setCurrencyCode("BTC");
        assertTrue(validator.validate("17VZNX1SN5NtKa8UQFxwQbFeFc3iqRYhem").isValid);

        validator.setCurrencyCode("TSK");
        assertTrue(validator.validate("4AuUM6PedofLWKfRCX1fP3SoNZUzq6FSAbpevHRR6tVuMpZc3HznVeudmNGkEB75apjE7WKVgZZh1YvPVxZoHFN88NCdmWw").isValid);

        validator.setCurrencyCode("LTC");
        assertTrue(validator.validate("Lg3PX8wRWmApFCoCMAsPF5P9dPHYQHEWKW").isValid);

        validator.setCurrencyCode("BOGUS");

        assertFalse(validator.validate("1BOGUSADDR").isValid);
    }
}
