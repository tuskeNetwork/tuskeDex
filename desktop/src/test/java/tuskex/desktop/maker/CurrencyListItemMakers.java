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
import com.natpryce.makeiteasy.Maker;
import com.natpryce.makeiteasy.Property;
import tuskex.core.locale.TradeCurrency;
import tuskex.desktop.util.CurrencyListItem;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static tuskex.desktop.maker.TradeCurrencyMakers.euro;
import static tuskex.desktop.maker.TradeCurrencyMakers.monero;

public class CurrencyListItemMakers {

    public static final Property<tuskex.desktop.util.CurrencyListItem, TradeCurrency> tradeCurrency = new Property<>();
    public static final Property<CurrencyListItem, Integer> numberOfTrades = new Property<>();

    public static final Instantiator<CurrencyListItem> CurrencyListItem = lookup ->
            new CurrencyListItem(lookup.valueOf(tradeCurrency, monero), lookup.valueOf(numberOfTrades, 0));

    public static final Maker<CurrencyListItem> bitcoinItem = a(CurrencyListItem);
    public static final Maker<CurrencyListItem> euroItem = a(CurrencyListItem, with(tradeCurrency, euro));
}
