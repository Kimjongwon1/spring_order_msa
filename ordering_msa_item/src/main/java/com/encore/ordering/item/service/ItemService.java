package com.encore.ordering.item.service;

import com.encore.ordering.item.domain.Item;
import com.encore.ordering.item.dto.ItemQuantityDto;
import com.encore.ordering.item.dto.ItemReqDto;
import com.encore.ordering.item.dto.ItemRestDto;
import com.encore.ordering.item.dto.ItemSerchDto;
import com.encore.ordering.item.repository.ItemRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Predicates;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item create(ItemReqDto itemReqDto){
        MultipartFile multipartFile = itemReqDto.getItemImage();
        String fileName = multipartFile.getOriginalFilename();
        Item new_item = Item.builder()
                .name(itemReqDto.getName())
                .category(itemReqDto.getCategory())
                .price(itemReqDto.getPrice())
                .stockQuantity(itemReqDto.getStockQuantity())
                .build();
        Item item = itemRepository.save(new_item);
        Path path = Paths.get("C:/Users/Playdata/Desktop/tmp",item.getId()+"_"+fileName);
        item.setImagePath(path.toString());
        try {
            byte[] bytes = multipartFile.getBytes();
            Files.write(path,bytes, StandardOpenOption.CREATE,StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new IllegalArgumentException("Image not Available");
        }
        return item;
    }

    public Resource getImage(Long id){
        Item item = itemRepository.findById(id).orElseThrow(()->new EntityNotFoundException("nof found Item"));
        String imagePath = item.getImagePath();
        Path path = Paths.get(imagePath);
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("url form is not vaild");
        }
        return resource;
    }

    public Item delete(Long id){
        Item item =  itemRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Not found Item"));
        item.deleteItem();
        return item;
    }
    public Item update(Long id, ItemReqDto itemReqDto){
        Item item = itemRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Not found id"));
        MultipartFile multipartFile = itemReqDto.getItemImage();
        String fileName = multipartFile.getOriginalFilename();
        Path path = Paths.get("C:/Users/Playdata/Desktop/tmp",item.getId()+"_"+fileName);
        item.updateItem(itemReqDto.getName()
                ,itemReqDto.getCategory()
                ,itemReqDto.getPrice()
                ,itemReqDto.getStockQuantity()
                ,path.toString());
        try {
            byte[] bytes = multipartFile.getBytes();
            Files.write(path,bytes, StandardOpenOption.CREATE,StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new IllegalArgumentException("Image not Available");
        }
        return item;
    }

    public List<ItemRestDto> findAll(ItemSerchDto itemSerchDto, Pageable pageable) {
//       검색을 위해 specification 객체 사용
//        specification 객체는 복잡한 쿼리를 명세를 이용한 정의하여 쉽게 생성
        Specification<Item> spec = new Specification<Item>() {
            @Override
            //아래 매개변수에서 root는 엔티티의 속성을 접근하기 위한 객체
            //query는
            //CriteriaBuilder는 쿼리를 생성하기 위한 객체
            public Predicate toPredicate(Root<Item> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(itemSerchDto.getName() != null){
                    predicates.add(criteriaBuilder
                            .like(root.get("name")
                                    ,"%" + itemSerchDto.getName() + "%"));
                }
                if(itemSerchDto.getCategory() != null){
                    predicates.add(criteriaBuilder
                            .like(root.get("category")
                                    ,"%" + itemSerchDto.getCategory() + "%"));
                }
                predicates.add(criteriaBuilder.equal(root.get("delYn"),"N"));

                Predicate[] predicatesArr = new Predicate[predicates.size()];
               for(int i=0; i< predicates.size();i++){
                   predicatesArr[i] = predicates.get(i);
               }
                return criteriaBuilder.and(predicatesArr);
            }
        };
        Page<Item> items = itemRepository.findAll(spec,pageable);
        List<Item> itemList =items.getContent();
        List<ItemRestDto> itemRestDtos = new ArrayList<>();
        itemRestDtos = itemList.stream().map(i-> ItemRestDto.builder()
                .id(i.getId())
                .name(i.getName())
                .category(i.getCategory())
                .price(i.getPrice())
                .stockQuantity(i.getStockQuantity())
                .imagePath(i.getImagePath())
                .build()).collect(Collectors.toList());
        return itemRestDtos;
    }

    public void updatequantity(List<ItemQuantityDto> itemQuantityDto) {
        for (ItemQuantityDto quantityDto:itemQuantityDto){
            Item item = itemRepository.findById(quantityDto.getId()).orElseThrow(()->new EntityNotFoundException("Not found id"));
            item.updateStockQuantity(quantityDto.getStockQuantity());
        }

    }

    public ItemRestDto findById(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        ItemRestDto itemRestDto = ItemRestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .category(item.getCategory())
                .stockQuantity(item.getStockQuantity())
                .price(item.getPrice())
                .build();
        return itemRestDto;
    }
}
