package hn.shadowcore.mercadox.core.controller;

import hn.shadowcore.mercadox.core.mapper.LocationMapper;
import hn.shadowcore.mercadox.core.service.LocationService;
import hn.shadowcore.mercadox.library.entity.response.BaseResponseDto;
import hn.shadowcore.mercadox.library.entity.response.Response;
import hn.shadowcore.mercadox.library.entity.response.dto.LocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/locations")
@PreAuthorize("permitAll()")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    private final LocationMapper locationMapper;

    @GetMapping("/{locationId}")
    public ResponseEntity<? extends Response<LocationDto>> findLocationById(@PathVariable String locationId) {
        BaseResponseDto<LocationDto> response = new BaseResponseDto<>();
        LocationDto location = locationMapper.toDto(locationService.findById(locationId));
        return response.buildResponseEntity(HttpStatus.OK, "Location was found.", location);
    }

}
