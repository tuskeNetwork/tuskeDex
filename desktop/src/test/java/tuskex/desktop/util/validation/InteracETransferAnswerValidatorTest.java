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
import tuskex.core.payment.validation.InteracETransferAnswerValidator;
import tuskex.core.payment.validation.LengthValidator;
import tuskex.core.util.validation.RegexValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InteracETransferAnswerValidatorTest {

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
        InteracETransferAnswerValidator validator = new InteracETransferAnswerValidator(new LengthValidator(), new RegexValidator());

        assertTrue(validator.validate("abcdefghijklmnopqrstuvwxy").isValid);
        assertTrue(validator.validate("ABCDEFGHIJKLMNOPQRSTUVWXY").isValid);
        assertTrue(validator.validate("1234567890").isValid);
        assertTrue(validator.validate("zZ-").isValid);

        assertFalse(validator.validate(null).isValid); // null
        assertFalse(validator.validate("").isValid); // empty
        assertFalse(validator.validate("two words").isValid); // two words
        assertFalse(validator.validate("ab").isValid); // too short
        assertFalse(validator.validate("abcdefghijklmnopqrstuvwxyz").isValid); // too long
        assertFalse(validator.validate("abc !@#").isValid); // invalid characters
    }

}
