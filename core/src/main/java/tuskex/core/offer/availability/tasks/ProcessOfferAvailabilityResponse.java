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

package tuskex.core.offer.availability.tasks;

import tuskex.common.taskrunner.Task;
import tuskex.common.taskrunner.TaskRunner;
import tuskex.core.offer.AvailabilityResult;
import tuskex.core.offer.Offer;
import tuskex.core.offer.availability.OfferAvailabilityModel;
import tuskex.core.offer.messages.OfferAvailabilityResponse;
import lombok.extern.slf4j.Slf4j;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public class ProcessOfferAvailabilityResponse extends Task<OfferAvailabilityModel> {
    public ProcessOfferAvailabilityResponse(TaskRunner<OfferAvailabilityModel> taskHandler,
                                            OfferAvailabilityModel model) {
        super(taskHandler, model);
    }

    @Override
    protected void run() {
        Offer offer = model.getOffer();
        try {
            runInterceptHook();

            checkArgument(offer.getState() != Offer.State.REMOVED, "Offer state must not be Offer.State.REMOVED");

            // check availability result
            OfferAvailabilityResponse offerAvailabilityResponse = model.getMessage();
            if (offerAvailabilityResponse.getAvailabilityResult() != AvailabilityResult.AVAILABLE) {
                offer.setState(Offer.State.NOT_AVAILABLE);
                failed("Take offer attempt rejected because of: " + offerAvailabilityResponse.getAvailabilityResult());
                return;
            }
            
            offer.setState(Offer.State.AVAILABLE);
            model.setMakerSignature(offerAvailabilityResponse.getMakerSignature());
            checkNotNull(model.getMakerSignature());

            complete();
        } catch (Throwable t) {
            offer.setErrorMessage("An error occurred.\n" +
                    "Error message:\n"
                    + t.getMessage());

            failed(t);
        }
    }
}
