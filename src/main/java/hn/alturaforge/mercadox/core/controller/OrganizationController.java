package hn.alturaforge.mercadox.core.controller;


import hn.alturaforge.mercadox.core.service.OrganizationService;
import hn.alturaforge.mercadox.library.entity.model.auth.Organization;
import hn.alturaforge.mercadox.library.entity.response.BaseResponseDto;
import hn.alturaforge.mercadox.library.entity.response.Response;
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
@RequestMapping("/api/v1/organizations")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<? extends Response<Organization>> createOrg(@RequestBody Organization organization,
                                                                      @RequestParam(required = false) String creatorId) {
        BaseResponseDto<Organization> responseDto = new BaseResponseDto<>();
        Organization org = orgQueryService.createOrganization(organization, Optional.ofNullable(creatorId));
        return responseDto.buildResponseEntity(HttpStatus.CREATED, "Organization created successfully.", org);
    }

}
