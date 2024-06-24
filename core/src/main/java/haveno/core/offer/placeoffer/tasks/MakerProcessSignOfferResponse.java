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

package haveno.core.offer.placeoffer.tasks;

import haveno.common.taskrunner.Task;
import haveno.common.taskrunner.TaskRunner;
import haveno.core.offer.Offer;
import haveno.core.offer.placeoffer.PlaceOfferModel;
import haveno.core.support.dispute.arbitration.arbitrator.Arbitrator;
import haveno.core.trade.HavenoUtils;

import static com.google.common.base.Preconditions.checkNotNull;

public class MakerProcessSignOfferResponse extends Task<PlaceOfferModel> {
    public MakerProcessSignOfferResponse(TaskRunner<PlaceOfferModel> taskHandler, PlaceOfferModel model) {
        super(taskHandler, model);
    }

    @Override
    protected void run() {
        Offer offer = model.getOpenOffer().getOffer();
        try {
            runInterceptHook();
            
            // get arbitrator
            Arbitrator arbitrator = checkNotNull(model.getUser().getAcceptedArbitratorByAddress(offer.getOfferPayload().getArbitratorSigner()), "user.getAcceptedArbitratorByAddress(arbitratorSigner) must not be null");
            
            // validate arbitrator signature
            if (!HavenoUtils.isArbitratorSignatureValid(model.getSignOfferResponse().getSignedOfferPayload(), arbitrator)) {
                throw new RuntimeException("Arbitrator's offer payload has invalid signature, offerId=" + offer.getId());
            }
            
            // set arbitrator signature for maker's offer
            offer.getOfferPayload().setArbitratorSignature(model.getSignOfferResponse().getSignedOfferPayload().getArbitratorSignature());
            if (!HavenoUtils.isArbitratorSignatureValid(offer.getOfferPayload(), arbitrator)) {
                throw new RuntimeException("Maker's offer payload has invalid signature, offerId=" + offer.getId());
            }
            offer.setState(Offer.State.AVAILABLE);
            complete();
        } catch (Exception e) {
            offer.setErrorMessage("An error occurred.\n" +
                    "Error message:\n"
                    + e.getMessage());
            failed(e);
        }
    }
}
