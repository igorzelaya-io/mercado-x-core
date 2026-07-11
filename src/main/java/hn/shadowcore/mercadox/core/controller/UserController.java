package hn.shadowcore.mercadox.core.controller;


import hn.shadowcore.mercadox.core.service.UserService;
import hn.shadowcore.mercadox.library.entity.model.auth.User;
import hn.shadowcore.mercadox.library.entity.response.BaseResponseDto;
import hn.shadowcore.mercadox.library.entity.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
public class UserController {

    private final UserService userService;
    @GetMapping("/{id}")
    public ResponseEntity<? extends Response<User>> findById(@PathVariable String id) {
        BaseResponseDto<User> response = new BaseResponseDto<>();
        final User user = userService.findActiveUserById(id);
        return response.buildResponseEntity(HttpStatus.OK, "User found successfully.", user);
    }

    @GetMapping
    public ResponseEntity<? extends Response<User>> findByUsername(@RequestParam String username) {
        BaseResponseDto<User> response = new BaseResponseDto<>();
        final User user = userService.findActiveUserByUsername(username);
        return response.buildResponseEntity(HttpStatus.OK, "User found successfully.", user);
    }

}
