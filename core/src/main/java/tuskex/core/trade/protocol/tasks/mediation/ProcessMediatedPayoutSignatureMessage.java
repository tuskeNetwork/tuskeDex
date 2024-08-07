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

package tuskex.core.trade.protocol.tasks.mediation;

import tuskex.common.taskrunner.TaskRunner;
import tuskex.core.support.dispute.mediation.MediationResultState;
import tuskex.core.trade.Trade;
import tuskex.core.trade.messages.MediatedPayoutTxSignatureMessage;
import tuskex.core.trade.protocol.tasks.TradeTask;
import tuskex.core.util.Validator;
import lombok.extern.slf4j.Slf4j;

import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public class ProcessMediatedPayoutSignatureMessage extends TradeTask {
    public ProcessMediatedPayoutSignatureMessage(TaskRunner<Trade> taskHandler, Trade trade) {
        super(taskHandler, trade);
    }

    @Override
    protected void run() {
        try {
            runInterceptHook();
            log.debug("current trade state " + trade.getState());
            MediatedPayoutTxSignatureMessage message = (MediatedPayoutTxSignatureMessage) processModel.getTradeMessage();
            Validator.checkTradeId(processModel.getOfferId(), message);
            checkNotNull(message);

            trade.getTradePeer().setMediatedPayoutTxSignature(checkNotNull(message.getTxSignature()));

            // update to the latest peer address of our peer if the message is correct
            trade.getTradePeer().setNodeAddress(processModel.getTempTradePeerNodeAddress());

            trade.setMediationResultState(MediationResultState.RECEIVED_SIG_MSG);

            processModel.getTradeManager().requestPersistence();

            complete();
        } catch (Throwable t) {
            failed(t);
        }
    }
}
