package co.ucc.esyrent.service;

public interface EmailNotificationService {

    void sendContractCreatedNotification(Long contractId);

    void sendContractExpiringNotification(Long contractId);

    void sendPaymentRegisteredNotification(Long paymentId);
}
