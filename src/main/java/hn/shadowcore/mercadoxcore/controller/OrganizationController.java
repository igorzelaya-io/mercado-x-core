package hn.shadowcore.mercadoxcore.controller;


import hn.shadowcore.mercadoxcore.service.OrganizationService;
import hn.shadowcore.mercadoxlibrary.entity.model.auth.Organization;
import hn.shadowcore.mercadoxlibrary.entity.response.BaseResponseDto;
import hn.shadowcore.mercadoxlibrary.entity.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organization")
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
public class OrganizationController {

    private final OrganizationService orgQueryService;

    @GetMapping("/{orgId}")
    public ResponseEntity<? extends Response<Organization>> findOrgById(@PathVariable String orgId) {
        BaseResponseDto<Organization> dto = new BaseResponseDto<>();
        Organization org = orgQueryService.findActiveOrgById(UUID.fromString(orgId));
        return dto.buildResponseEntity(HttpStatus.OK, "Organization found.", org);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<? extends Response<Organization>> createOrg(@RequestBody Organization organization,
                                                                      @RequestParam(required = false) String creatorId) {
        BaseResponseDto<Organization> responseDto = new BaseResponseDto<>();
        Organization org = orgQueryService.createOrganization(organization, Optional.ofNullable(creatorId));
        return responseDto.buildResponseEntity(HttpStatus.CREATED, "Organization created successfully.", org);
    }

}
