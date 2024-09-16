package com.example.finalproject.Controller;

import com.example.finalproject.ApiResponse.ApiResponse;
import com.example.finalproject.Model.UsedItem;
import com.example.finalproject.Model.User;
import com.example.finalproject.Service.UsedItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/usedItem")
public class UsedItemController {

    private final UsedItemService usedItemService;

    @GetMapping("/get")
    public ResponseEntity getAllUsedItem() {
        return ResponseEntity.status(200).body(usedItemService.getAllUsedItems());
    }

    @PostMapping("/add/{sellerId}")
    public ResponseEntity<String> addUsedItem(@RequestBody @Valid UsedItem usedItem, @PathVariable Integer sellerId) {
        usedItemService.addUsedItem(usedItem, sellerId);
        return ResponseEntity.ok("Used item added successfully");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity updateUsedItem(@PathVariable Integer id,@Valid @RequestBody UsedItem usedItem) {
        usedItemService.updateUsedItem(id, usedItem);
        return ResponseEntity.status(200).body("Used item updated");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteUsedItem(@PathVariable Integer id) {
        usedItemService.deleteUsedItem(id);
        return ResponseEntity.status(200).body("Used item deleted");
    }

    @PostMapping("/buy/{itemId}")
    public ResponseEntity buyUsedItem(@PathVariable Integer itemId, @AuthenticationPrincipal User user) {
        usedItemService.buyUsedItem(itemId, user);
        return ResponseEntity.status(200).body(new ApiResponse("Item purchased successfully"));
    }

    // Endpoint for getting all items sold by the logged-in seller
    @GetMapping("/seller-items")
    public ResponseEntity getItemsBySeller(@AuthenticationPrincipal User user) {
        List<UsedItem> items = usedItemService.getItemsBySeller(user);
        return ResponseEntity.status(200).body(items);
    }

}
