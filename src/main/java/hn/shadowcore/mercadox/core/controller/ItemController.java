package hn.shadowcore.mercadox.core.controller;


import hn.shadowcore.mercadox.library.entity.ports.incoming.ItemUseCase;
import hn.shadowcore.mercadox.library.entity.response.BaseResponseDto;
import hn.shadowcore.mercadox.library.entity.response.Response;
import hn.shadowcore.mercadox.library.entity.response.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@PreAuthorize("permitAll()")
@RequiredArgsConstructor
public class ItemController {

    private final ItemUseCase itemUseCase;

    @PostMapping
    public ResponseEntity<? extends Response<String>> saveItem
            (@RequestBody ItemDto itemDto) {
        BaseResponseDto<String> response = new BaseResponseDto<>();
        // Call Service use case.
        return ResponseEntity.ok(response);
    }


}
