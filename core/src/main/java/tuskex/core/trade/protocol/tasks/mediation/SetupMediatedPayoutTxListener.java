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
import tuskex.core.trade.Trade;
import tuskex.core.trade.protocol.tasks.TradeTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupMediatedPayoutTxListener extends TradeTask {

    @SuppressWarnings({ "unused" })
    public SetupMediatedPayoutTxListener(TaskRunner taskHandler, Trade trade) {
        super(taskHandler, trade);
    }

    @Override
    protected void run() {
        try {
            runInterceptHook();
            if (true) throw new RuntimeException("Not implemented");
            complete();
        } catch (Throwable t) {
            failed(t);
        }
    }
}
