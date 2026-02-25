package hn.shadowcore.mercadox.core.controller;


import hn.shadowcore.mercadoxlibrary.entity.ports.incoming.ClientLeadUseCase;
import hn.shadowcore.mercadoxlibrary.entity.request.ClientLeadRequest;
import hn.shadowcore.mercadoxlibrary.entity.response.BaseResponseDto;
import hn.shadowcore.mercadoxlibrary.entity.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
@RequestMapping("/api/v1/orgs/{orgId}/leads")
public class LeadController {

    private final ClientLeadUseCase clientLeadUseCase;

    @PostMapping("/{orgId}")
    public ResponseEntity<? extends Response<String>> generateLeads(@PathVariable("orgId") final String orgId,
                                                                    @RequestBody @Validated ClientLeadRequest request) {
        BaseResponseDto<String> response = new BaseResponseDto<>();
        clientLeadUseCase.generateLead(request);
        return response
                .buildResponseEntity(HttpStatus.CREATED,
                        "Lead created successfully.", "Lead created successfully.");
    }
}
