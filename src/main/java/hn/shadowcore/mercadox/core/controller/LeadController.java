package hn.shadowcore.mercadox.core.controller;


import hn.shadowcore.mercadox.library.entity.ports.incoming.ClientLeadUseCase;
import hn.shadowcore.mercadox.library.entity.request.ClientLeadRequest;
import hn.shadowcore.mercadox.library.entity.response.BaseResponseDto;
import hn.shadowcore.mercadox.library.entity.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/orgs/{orgId}/leads")
public class LeadController {

    private final ClientLeadUseCase clientLeadUseCase;

    @PostMapping
    public ResponseEntity<? extends Response<String>> generateLeads(@PathVariable("orgId") final String orgId,
                                                                    @RequestBody @Validated ClientLeadRequest request) {
        BaseResponseDto<String> response = new BaseResponseDto<>();
        clientLeadUseCase.generateLead(request, orgId);
        return response
                .buildResponseEntity(HttpStatus.CREATED,
                        "Lead = successfully.", "Lead created successfully.");
    }
}
