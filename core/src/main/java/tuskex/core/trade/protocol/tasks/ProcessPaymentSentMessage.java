/*
 * This file is part of Tuskex.
 *
 * Tuskex is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Tuskex is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Tuskex. If not, see <http://www.gnu.org/licenses/>.
 */

package tuskex.core.trade.protocol.tasks;

import tuskex.common.taskrunner.TaskRunner;
import tuskex.core.trade.TuskexUtils;
import tuskex.core.trade.Trade;
import tuskex.core.trade.messages.PaymentSentMessage;
import tuskex.core.util.Validator;
import lombok.extern.slf4j.Slf4j;

import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public class ProcessPaymentSentMessage extends TradeTask {
    public ProcessPaymentSentMessage(TaskRunner<Trade> taskHandler, Trade trade) {
        super(taskHandler, trade);
    }

    @Override
    protected void run() {
        try {
            runInterceptHook();
            log.debug("current trade state " + trade.getState());
            PaymentSentMessage message = (PaymentSentMessage) processModel.getTradeMessage();
            checkNotNull(message);
            Validator.checkTradeId(processModel.getOfferId(), message);

            // verify signature of payment sent message
            TuskexUtils.verifyPaymentSentMessage(trade, message);

            // update latest peer address
            trade.getBuyer().setNodeAddress(processModel.getTempTradePeerNodeAddress());

            // update state from message
            trade.getBuyer().setPaymentSentMessage(message);
            trade.getBuyer().setUpdatedMultisigHex(message.getUpdatedMultisigHex());
            trade.getSeller().setAccountAgeWitness(message.getSellerAccountAgeWitness());
            String counterCurrencyTxId = message.getCounterCurrencyTxId();
            if (counterCurrencyTxId != null && counterCurrencyTxId.length() < 100) trade.setCounterCurrencyTxId(counterCurrencyTxId);
            String counterCurrencyExtraData = message.getCounterCurrencyExtraData();
            if (counterCurrencyExtraData != null && counterCurrencyExtraData.length() < 100) trade.setCounterCurrencyExtraData(counterCurrencyExtraData);

            // if seller, decrypt buyer's payment account payload
            if (trade.isSeller()) trade.decryptPeerPaymentAccountPayload(message.getPaymentAccountKey());
            trade.requestPersistence();

            // update state
            trade.advanceState(Trade.State.BUYER_SENT_PAYMENT_SENT_MSG);
            trade.requestPersistence();
            complete();
        } catch (Throwable t) {
            failed(t);
        }
    }
}
