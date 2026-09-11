package hn.alturaforge.mercadox.core.controller;


import hn.alturaforge.mercadox.library.entity.ports.incoming.ItemUseCase;
import hn.alturaforge.mercadox.library.entity.response.BaseResponseDto;
import hn.alturaforge.mercadox.library.entity.response.Response;
import hn.alturaforge.mercadox.library.entity.response.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/items")
@PreAuthorize("permitAll()")
@RequiredArgsConstructor
public class ItemController {

    private final ItemUseCase itemUseCase;

    @PostMapping
    public ResponseEntity<? extends Response<ItemDto>> saveItem(@RequestBody ItemDto itemDto) {
        BaseResponseDto<ItemDto> response = new BaseResponseDto<>();
        ItemDto created = itemUseCase.createItem(itemDto);
        return response.buildResponseEntity(HttpStatus.CREATED, "Item created successfully.", created);
    }

}
