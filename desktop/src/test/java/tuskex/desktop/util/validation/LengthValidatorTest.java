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
import tuskex.core.payment.validation.LengthValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LengthValidatorTest {

    @BeforeEach
    public void setup() {
        final BaseCurrencyNetwork baseCurrencyNetwork = Config.baseCurrencyNetwork();
        final String currencyCode = baseCurrencyNetwork.getCurrencyCode();
        Res.setBaseCurrencyCode(currencyCode);
        Res.setBaseCurrencyName(baseCurrencyNetwork.getCurrencyName());
        CurrencyUtil.setBaseCurrencyCode(currencyCode);
    }

    @Test
    public void validate() throws Exception {
        LengthValidator validator = new LengthValidator();

        assertTrue(validator.validate("").isValid);
        assertTrue(validator.validate(null).isValid);
        assertTrue(validator.validate("123456789").isValid);

        validator.setMinLength(2);
        validator.setMaxLength(5);

        assertTrue(validator.validate("12").isValid);
        assertTrue(validator.validate("12345").isValid);

        assertFalse(validator.validate("1").isValid); // too short
        assertFalse(validator.validate("").isValid); // too short
        assertFalse(validator.validate(null).isValid); // too short
        assertFalse(validator.validate("123456789").isValid); // too long

        LengthValidator validator2 = new LengthValidator(2, 5);

        assertTrue(validator2.validate("12").isValid);
        assertTrue(validator2.validate("12345").isValid);

        assertFalse(validator2.validate("1").isValid); // too short
        assertFalse(validator2.validate("").isValid); // too short
        assertFalse(validator2.validate(null).isValid); // too short
        assertFalse(validator2.validate("123456789").isValid); // too long
    }
}
