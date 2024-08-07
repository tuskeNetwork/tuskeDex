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

package tuskex.desktop.maker;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import tuskex.core.locale.CryptoCurrency;
import tuskex.core.locale.TraditionalCurrency;
import tuskex.core.locale.TradeCurrency;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;

public class TradeCurrencyMakers {

    public static final Property<TradeCurrency, String> currencyCode = new Property<>();
    public static final Property<TradeCurrency, String> currencyName = new Property<>();

    public static final Instantiator<tuskex.core.locale.CryptoCurrency> CryptoCurrency = lookup ->
            new CryptoCurrency(lookup.valueOf(currencyCode, "TSK"), lookup.valueOf(currencyName, "Monero"));

    public static final Instantiator<tuskex.core.locale.TraditionalCurrency> TraditionalCurrency = lookup ->
            new TraditionalCurrency(lookup.valueOf(currencyCode, "EUR"));

    public static final CryptoCurrency monero = make(a(CryptoCurrency));
    public static final TraditionalCurrency euro = make(a(TraditionalCurrency));
    public static final TraditionalCurrency usd = make(a(TraditionalCurrency).but(with(currencyCode, "USD")));
}

