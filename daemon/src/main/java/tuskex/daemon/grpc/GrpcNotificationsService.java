package tuskex.daemon.grpc;

import com.google.inject.Inject;
import tuskex.core.api.CoreApi;
import tuskex.core.api.NotificationListener;
import tuskex.daemon.grpc.interceptor.CallRateMeteringInterceptor;
import tuskex.daemon.grpc.interceptor.GrpcCallRateMeter;
import static tuskex.daemon.grpc.interceptor.GrpcServiceRateMeteringConfig.getCustomRateMeteringInterceptor;
import tuskex.proto.grpc.NotificationMessage;
import tuskex.proto.grpc.NotificationsGrpc.NotificationsImplBase;
import static tuskex.proto.grpc.NotificationsGrpc.getRegisterNotificationListenerMethod;
import static tuskex.proto.grpc.NotificationsGrpc.getSendNotificationMethod;
import tuskex.proto.grpc.RegisterNotificationListenerRequest;
import tuskex.proto.grpc.SendNotificationReply;
import tuskex.proto.grpc.SendNotificationRequest;
import io.grpc.Context;
import io.grpc.ServerInterceptor;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import java.util.HashMap;
import java.util.Optional;
import static java.util.concurrent.TimeUnit.SECONDS;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class GrpcNotificationsService extends NotificationsImplBase {

    private final CoreApi coreApi;
    private final GrpcExceptionHandler exceptionHandler;

    @Inject
    public GrpcNotificationsService(CoreApi coreApi, GrpcExceptionHandler exceptionHandler) {
        this.coreApi = coreApi;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void registerNotificationListener(RegisterNotificationListenerRequest request,
                                             StreamObserver<NotificationMessage> responseObserver) {
        Context ctx = Context.current().fork(); // context is independent for long-lived request
        ctx.run(() -> {
            try {
                coreApi.addNotificationListener(new GrpcNotificationListener(responseObserver));
                // No onNext / onCompleted, as the response observer should be kept open
            } catch (Throwable t) {
                exceptionHandler.handleException(log, t, responseObserver);
            }
        });
    }

    @Override
    public void sendNotification(SendNotificationRequest request,
                                 StreamObserver<SendNotificationReply> responseObserver) {
        Context ctx = Context.current().fork(); // context is independent from notification delivery
        ctx.run(() -> {
            try {
                coreApi.sendNotification(request.getNotification());
                responseObserver.onNext(SendNotificationReply.newBuilder().build());
                responseObserver.onCompleted();
            } catch (Throwable t) {
                exceptionHandler.handleException(log, t, responseObserver);
            }
        });
    }

    @Value
    private static class GrpcNotificationListener implements NotificationListener {

        @NonNull
        StreamObserver<NotificationMessage> responseObserver;

        @Override
        public void onMessage(@NonNull NotificationMessage message) {
            if (!((ServerCallStreamObserver<NotificationMessage>) responseObserver).isCancelled()) {
                responseObserver.onNext(message);
            }
        }
    }

    final ServerInterceptor[] interceptors() {
        Optional<ServerInterceptor> rateMeteringInterceptor = rateMeteringInterceptor();
        return rateMeteringInterceptor.map(serverInterceptor ->
                new ServerInterceptor[]{serverInterceptor}).orElseGet(() -> new ServerInterceptor[0]);
    }

    final Optional<ServerInterceptor> rateMeteringInterceptor() {
        return getCustomRateMeteringInterceptor(coreApi.getConfig().appDataDir, this.getClass())
                .or(() -> Optional.of(CallRateMeteringInterceptor.valueOf(
                        new HashMap<>() {{
                            put(getRegisterNotificationListenerMethod().getFullMethodName(), new GrpcCallRateMeter(10, SECONDS));
                            put(getSendNotificationMethod().getFullMethodName(), new GrpcCallRateMeter(10, SECONDS));
                        }}
                )));
    }
}
