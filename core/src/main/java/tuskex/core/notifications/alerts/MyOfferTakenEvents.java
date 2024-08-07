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

package tuskex.core.notifications.alerts;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tuskex.core.locale.Res;
import tuskex.core.notifications.MobileMessage;
import tuskex.core.notifications.MobileMessageType;
import tuskex.core.notifications.MobileNotificationService;
import tuskex.core.offer.OpenOffer;
import tuskex.core.offer.OpenOfferManager;
import java.util.UUID;
import javafx.collections.ListChangeListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class MyOfferTakenEvents {
    private final OpenOfferManager openOfferManager;
    private final MobileNotificationService mobileNotificationService;

    @Inject
    public MyOfferTakenEvents(OpenOfferManager openOfferManager, MobileNotificationService mobileNotificationService) {
        this.openOfferManager = openOfferManager;
        this.mobileNotificationService = mobileNotificationService;
    }

    public void onAllServicesInitialized() {
        openOfferManager.getObservableList().addListener((ListChangeListener<OpenOffer>) c -> {
            c.next();
            if (c.wasRemoved())
                c.getRemoved().forEach(this::onOpenOfferRemoved);
        });
        openOfferManager.getObservableList().forEach(this::onOpenOfferRemoved);
    }

    private void onOpenOfferRemoved(OpenOffer openOffer) {
        OpenOffer.State state = openOffer.getState();
        if (state == OpenOffer.State.RESERVED) {
            log.info("We got a offer removed. id={}, state={}", openOffer.getId(), state);
            String shortId = openOffer.getShortId();
            MobileMessage message = new MobileMessage(Res.get("account.notifications.offer.message.title"),
                    Res.get("account.notifications.offer.message.msg", shortId),
                    shortId,
                    MobileMessageType.OFFER);
            try {
                mobileNotificationService.sendMessage(message);
            } catch (Exception e) {
                log.error(e.toString());
                e.printStackTrace();
            }
        }
    }

    public static MobileMessage getTestMsg() {
        String shortId = UUID.randomUUID().toString().substring(0, 8);
        return new MobileMessage(Res.get("account.notifications.offer.message.title"),
                Res.get("account.notifications.offer.message.msg", shortId),
                shortId,
                MobileMessageType.OFFER);
    }
}
