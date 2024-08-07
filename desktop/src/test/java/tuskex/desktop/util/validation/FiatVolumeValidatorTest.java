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

package tuskex.desktop.util.validation;

import tuskex.common.config.BaseCurrencyNetwork;
import tuskex.common.config.Config;
import tuskex.core.locale.CurrencyUtil;
import tuskex.core.locale.Res;
import tuskex.core.payment.validation.FiatVolumeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FiatVolumeValidatorTest {

    @BeforeEach
    public void setup() {
        final BaseCurrencyNetwork baseCurrencyNetwork = Config.baseCurrencyNetwork();
        final String currencyCode = baseCurrencyNetwork.getCurrencyCode();
        Res.setBaseCurrencyCode(currencyCode);
        Res.setBaseCurrencyName(baseCurrencyNetwork.getCurrencyName());
        CurrencyUtil.setBaseCurrencyCode(currencyCode);
    }

    @Test
    public void testValidate() {
        FiatVolumeValidator validator = new FiatVolumeValidator();

        assertTrue(validator.validate("1").isValid);
        assertTrue(validator.validate("1,1").isValid);
        assertTrue(validator.validate("1.1").isValid);
        assertTrue(validator.validate(",1").isValid);
        assertTrue(validator.validate(".1").isValid);
        assertTrue(validator.validate("0.01").isValid);
        assertTrue(validator.validate("1000000.00").isValid);
        assertTrue(validator.validate(String.valueOf(validator.getMinValue())).isValid);
        assertTrue(validator.validate(String.valueOf(validator.getMaxValue())).isValid);

        assertFalse(validator.validate(null).isValid);
        assertFalse(validator.validate("").isValid);
        assertFalse(validator.validate("a").isValid);
        assertFalse(validator.validate("2a").isValid);
        assertFalse(validator.validate("a2").isValid);
        assertFalse(validator.validate("0").isValid);
        assertFalse(validator.validate("-1").isValid);
        assertFalse(validator.validate("0.0").isValid);
        assertFalse(validator.validate("0,1,1").isValid);
        assertFalse(validator.validate("0.1.1").isValid);
        assertFalse(validator.validate("1,000.1").isValid);
        assertFalse(validator.validate("1.000,1").isValid);
        assertFalse(validator.validate("0.009").isValid);
        assertFalse(validator.validate(String.valueOf(validator.getMinValue() - 1)).isValid);
        assertFalse(validator.validate(String.valueOf(Double.MIN_VALUE)).isValid);
    }
}
