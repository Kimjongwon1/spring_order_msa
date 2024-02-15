package com.encore.ordering.item.controller;

import com.encore.ordering.common.CommonResponse;
import com.encore.ordering.item.domain.Item;
import com.encore.ordering.item.dto.ItemReqDto;
import com.encore.ordering.item.dto.ItemRestDto;
import com.encore.ordering.item.dto.ItemSerchDto;
import com.encore.ordering.item.service.ItemService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item")
public class ItemController {
    private final ItemService itemService;


    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }
    @PostMapping("/create")
    public ResponseEntity<CommonResponse> itemCreate(ItemReqDto itemReqDto){
        Item item = itemService.create(itemReqDto);
        return new ResponseEntity<>(
                new CommonResponse(HttpStatus.CREATED,"Item Successfully Created",item.getId())
                , HttpStatus.CREATED);
    }
    @GetMapping("/items")
    public ResponseEntity<List<ItemRestDto>> itemList(ItemSerchDto itemSerchDto, Pageable pageable){
        List<ItemRestDto> itemRestDtos = itemService.findAll(itemSerchDto,pageable);
        return new ResponseEntity<>(itemRestDtos,HttpStatus.OK);
    }
    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getImage(@PathVariable Long id){
            Resource resource = itemService.getImage(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(resource,headers,HttpStatus.OK);

    }


    @PatchMapping("/{id}/update")
    public ResponseEntity<CommonResponse> itemUpdate(@PathVariable Long id, ItemReqDto itemReqDto){
        Item item = itemService.update(id,itemReqDto);
        return new ResponseEntity<>(
                new CommonResponse(HttpStatus.OK,"Item Successfully updated",item.getId())
                ,HttpStatus.OK);
    }


    @DeleteMapping("/{id}/delete")
    public ResponseEntity<CommonResponse> itemDelete(@PathVariable Long id){
        Item item = itemService.delete(id);
        return new ResponseEntity<>(
                new CommonResponse(HttpStatus.OK,"Item Successfully delete",item.getId())
                , HttpStatus.OK);
    }


}
