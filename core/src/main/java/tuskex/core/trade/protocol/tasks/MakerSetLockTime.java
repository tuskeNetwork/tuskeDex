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

package tuskex.core.trade.protocol.tasks;

import tuskex.common.config.Config;
import tuskex.common.taskrunner.TaskRunner;
import tuskex.core.trade.Trade;
import tuskex.core.tsk.wallet.Restrictions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MakerSetLockTime extends TradeTask {
    public MakerSetLockTime(TaskRunner<Trade> taskHandler, Trade trade) {
        super(taskHandler, trade);
    }

    @Override
    protected void run() {
        try {
            runInterceptHook();

            // 10 days for cryptos, 20 days for other payment methods
            // For regtest dev environment we use 5 blocks
            int delay = Config.baseCurrencyNetwork().isTestnet() ?
                    5 :
                    Restrictions.getLockTime(processModel.getOffer().getPaymentMethod().isBlockchain());

            long lockTime = processModel.getBtcWalletService().getBestChainHeight() + delay;
            log.info("lockTime={}, delay={}", lockTime, delay);
            trade.setLockTime(lockTime);

            processModel.getTradeManager().requestPersistence();

            complete();
        } catch (Throwable t) {
            failed(t);
        }
    }
}
