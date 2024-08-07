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

import com.google.inject.Inject;
import tuskex.asset.AddressValidationResult;
import tuskex.asset.Asset;
import tuskex.asset.AssetRegistry;
import tuskex.common.config.Config;
import tuskex.core.locale.CurrencyUtil;
import tuskex.core.locale.Res;
import tuskex.core.util.validation.InputValidator;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public final class CryptoAddressValidator extends InputValidator {

    private final AssetRegistry assetRegistry;
    private String currencyCode;

    @Inject
    public CryptoAddressValidator(AssetRegistry assetRegistry) {
        this.assetRegistry = assetRegistry;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public ValidationResult validate(String input) {
        ValidationResult validationResult = super.validate(input);
        if (!validationResult.isValid || currencyCode == null)
            return validationResult;

        Optional<Asset> optionalAsset = CurrencyUtil.findAsset(assetRegistry, currencyCode,
                Config.baseCurrencyNetwork());
        if (optionalAsset.isPresent()) {
            Asset asset = optionalAsset.get();
            AddressValidationResult result = asset.validateAddress(input);
            if (!result.isValid()) {
                return new ValidationResult(false, Res.get(result.getI18nKey(), asset.getTickerSymbol(),
                        result.getMessage()));
            }

            return new ValidationResult(true);
        } else {
            return new ValidationResult(false);
        }
    }
}
