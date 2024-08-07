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

import tuskex.core.util.validation.InputValidator;

public final class EmailOrMobileNrValidator extends InputValidator {

    private final EmailValidator emailValidator;

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Public methods
    ///////////////////////////////////////////////////////////////////////////////////////////

    public EmailOrMobileNrValidator() {
        emailValidator = new EmailValidator();
    }

    @Override
    public ValidationResult validate(String input) {
        ValidationResult result = validateIfNotEmpty(input);
        if (!result.isValid) {
            return result;
        } else {
            ValidationResult emailResult = emailValidator.validate(input);
            if (emailResult.isValid)
                return emailResult;
            else
                return validatePhoneNumber(input);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Private methods
    ///////////////////////////////////////////////////////////////////////////////////////////

    // TODO not impl yet -> see InteracETransferValidator
    private ValidationResult validatePhoneNumber(String input) {
        return super.validate(input);
    }
}
