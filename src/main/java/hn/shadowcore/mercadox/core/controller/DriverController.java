package hn.shadowcore.mercadox.core.controller;

import hn.shadowcore.mercadox.core.mapper.UserMapper;
import hn.shadowcore.mercadox.core.service.UserService;
import hn.shadowcore.mercadoxlibrary.entity.response.PaginatedResponse;
import hn.shadowcore.mercadoxlibrary.entity.response.Response;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
@PreAuthorize("permitAll()")
@RequiredArgsConstructor
public class DriverController {

    private final UserService userService;

    private final UserMapper userMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ORG_ADMIN', 'ADMIN')")
    public ResponseEntity<? extends Response<List<UserDto>>> findAvailableDrivers(@RequestParam("page") int page,
                                                                                  @RequestParam("size") int size,
                                                                                  @RequestParam("sort") String[] sort) {
        PaginatedResponse<UserDto> response = new PaginatedResponse<>();
        List<UserDto> dtos = userMapper.toDtoList(userService.findAvailableDrivers());
        return response.buildPaginatedResponse(HttpStatus.OK, "Drivers available.", dtos, page, size, sort);
    }


}
