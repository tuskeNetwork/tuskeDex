package tuskex.core.api;

import com.google.inject.Singleton;
import tuskex.core.api.model.TradeInfo;
import tuskex.core.support.messages.ChatMessage;
import tuskex.core.trade.Trade;
import tuskex.proto.grpc.NotificationMessage;
import tuskex.proto.grpc.NotificationMessage.NotificationType;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class CoreNotificationService {

    private final Object lock = new Object();
    private final List<NotificationListener> listeners = new LinkedList<>();

    public void addListener(@NonNull NotificationListener listener) {
        synchronized (lock) {
            listeners.add(listener);
        }
    }

    public void sendNotification(@NonNull NotificationMessage notification) {
        synchronized (lock) {
            for (Iterator<NotificationListener> iter = listeners.iterator(); iter.hasNext(); ) {
                NotificationListener listener = iter.next();
                try {
                    listener.onMessage(notification);
                } catch (RuntimeException e) {
                    log.warn("Failed to send notification to listener {}: {}", listener, e.getMessage());
                    iter.remove();
                }
            }
        }
    }

    public void sendAppInitializedNotification() {
        sendNotification(NotificationMessage.newBuilder()
                .setType(NotificationType.APP_INITIALIZED)
                .setTimestamp(System.currentTimeMillis())
                .build());
    }

    public void sendTradeNotification(Trade trade, String title, String message) {
        sendNotification(NotificationMessage.newBuilder()
                .setType(NotificationType.TRADE_UPDATE)
                .setTrade(TradeInfo.toTradeInfo(trade).toProtoMessage())
                .setTimestamp(System.currentTimeMillis())
                .setTitle(title)
                .setMessage(message)
                .build());
    }

    public void sendChatNotification(ChatMessage chatMessage) {
        sendNotification(NotificationMessage.newBuilder()
                .setType(NotificationType.CHAT_MESSAGE)
                .setTimestamp(System.currentTimeMillis())
                .setChatMessage(chatMessage.toProtoNetworkEnvelope().getChatMessage())
                .build());
    }

    public void sendErrorNotification(String title, String errorMessage) {
        sendNotification(NotificationMessage.newBuilder()
                .setType(NotificationType.ERROR)
                .setTimestamp(System.currentTimeMillis())
                .setTitle(title)
                .setMessage(errorMessage == null ? "null" : errorMessage)
                .build());
    }
}
