package hn.shadowcore.mercadox.core.utils.mother;

import hn.shadowcore.mercadox.library.entity.request.ClientLeadRequest;

public final class ClientLeadRequestMother {

    public static ClientLeadRequest valid() {
        return new ClientLeadRequest("Carlos", "Ditek", "carlos@ditek.com",
                "9000-0000", "Hello world!");
    }

    public static ClientLeadRequest invalid() {
        return new ClientLeadRequest("", "", "i@.com", null, null);
    }

}
