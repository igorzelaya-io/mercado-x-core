package hn.shadowcore.mercadox.core.utils.mother;

import hn.shadowcore.mercadox.library.entity.request.ClientLeadRequest;

public final class ClientLeadRequestMother {

    public static ClientLeadRequest valid() {
        return new ClientLeadRequest("Carlos", "Ditek", "carlos@ditek.com",
                "9000-0000", "Hello world!", null);
    }

    public static ClientLeadRequest invalid() {
        return new ClientLeadRequest("", "", "i@.com", null, null, null);
    }

    public static ClientLeadRequest withOversizedUserName() {
        return new ClientLeadRequest("A".repeat(101), "Ditek", "carlos@ditek.com",
                "9000-0000", "Hello world!", null);
    }

    public static ClientLeadRequest withMalformedEmail() {
        return new ClientLeadRequest("Carlos", "Ditek", "not-an-email",
                "9000-0000", "Hello world!", null);
    }

    public static ClientLeadRequest withMalformedPhone() {
        return new ClientLeadRequest("Carlos", "Ditek", "carlos@ditek.com",
                "call me maybe", "Hello world!", null);
    }

    public static ClientLeadRequest withOversizedMessage() {
        return new ClientLeadRequest("Carlos", "Ditek", "carlos@ditek.com",
                "9000-0000", "A".repeat(2001), null);
    }

    public static ClientLeadRequest withHoneypotFilled() {
        return new ClientLeadRequest("Carlos", "Ditek", "carlos@ditek.com",
                "9000-0000", "Hello world!", "https://spam-bot.example");
    }

}
