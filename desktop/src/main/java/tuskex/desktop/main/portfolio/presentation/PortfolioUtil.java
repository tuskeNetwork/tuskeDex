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

package tuskex.desktop.main.portfolio.presentation;

import tuskex.core.offer.OfferPayload;
import tuskex.desktop.Navigation;
import tuskex.desktop.main.MainView;
import tuskex.desktop.main.portfolio.PortfolioView;
import tuskex.desktop.main.portfolio.duplicateoffer.DuplicateOfferView;

public class PortfolioUtil {

    public static void duplicateOffer(Navigation navigation, OfferPayload offerPayload) {
        navigation.navigateToWithData(offerPayload, MainView.class, PortfolioView.class, DuplicateOfferView.class);
    }
}
