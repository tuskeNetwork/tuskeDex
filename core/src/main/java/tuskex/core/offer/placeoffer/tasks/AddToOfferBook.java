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

package tuskex.core.offer.placeoffer.tasks;

import tuskex.common.taskrunner.Task;
import tuskex.common.taskrunner.TaskRunner;
import tuskex.core.offer.Offer;
import tuskex.core.offer.placeoffer.PlaceOfferModel;

import static com.google.common.base.Preconditions.checkNotNull;

public class AddToOfferBook extends Task<PlaceOfferModel> {

    public AddToOfferBook(TaskRunner<PlaceOfferModel> taskHandler, PlaceOfferModel model) {
        super(taskHandler, model);
    }

    @Override
    protected void run() {
        try {
            runInterceptHook();
            checkNotNull(model.getSignOfferResponse().getSignedOfferPayload().getArbitratorSignature(), "Offer's arbitrator signature is null: " + model.getOpenOffer().getOffer().getId());
            model.getOfferBookService().addOffer(new Offer(model.getSignOfferResponse().getSignedOfferPayload()),
                    () -> {
                        model.setOfferAddedToOfferBook(true);
                        complete();
                    },
                    errorMessage -> {
                        model.getOpenOffer().getOffer().setErrorMessage("Could not add offer to offerbook.\n" +
                                "Please check your network connection and try again.");

                        failed(errorMessage);
                    });
        } catch (Throwable t) {
            model.getOpenOffer().getOffer().setErrorMessage("An error occurred.\n" +
                    "Error message:\n"
                    + t.getMessage());

            failed(t);
        }
    }
}
