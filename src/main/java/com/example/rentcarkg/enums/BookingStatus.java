package com.example.rentcarkg.enums;

public enum BookingStatus {
    PENDING,            // заявка создана
    EMAIL_CONFIRMED,    // клиент подтвердил по почте
    CONFIRMED,          // владелец подтвердил
    REJECTED,           // отклонено владельцем
    CANCELLED,           // отменено клиентом
    EXPIRED             // автоотмена
}


