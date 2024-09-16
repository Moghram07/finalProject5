package com.example.finalproject.Service;

import com.example.finalproject.ApiException.ApiException;
import com.example.finalproject.Model.*;
import com.example.finalproject.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsedItemService {

    private final UsedItemRepository usedItemRepository;
    private final OrdersRepository ordersRepository;
    private final AuthRepository userRepository;

    public List<UsedItem> getAllUsedItems() {
        return usedItemRepository.findAll();
    }
    // Add method by Omar
    public UsedItem addUsedItem(UsedItem usedItem, Integer sellerId) {
        User seller = userRepository.findUserById(sellerId);
        if (seller == null) {
            throw new ApiException("Seller not found");
        }
        usedItem.setSeller(seller);
        return usedItemRepository.save(usedItem);
    }

    public void updateUsedItem(Integer id, UsedItem usedItem) {
        UsedItem usedItem1 = usedItemRepository.findUsedItemById(id);
        if (usedItem1 == null) {
            throw new ApiException("Used item not found");
        }
        usedItem1.setName(usedItem.getName());
        usedItem1.setDescription(usedItem.getDescription());
        usedItem1.setPrice(usedItem.getPrice());
        usedItem1.setCategory(usedItem.getCategory());
        usedItem1.setUsed(usedItem.isUsed());
        usedItemRepository.save(usedItem1);
    }

    public void deleteUsedItem(Integer id) {
        UsedItem usedItem1 = usedItemRepository.findUsedItemById(id);
        if (usedItem1 == null) {
            throw new ApiException("Used item not found");
        }
        usedItemRepository.delete(usedItem1);
    }

    // Buy method by Omar
    // Secure method to buy a used item
    public void buyUsedItem(Integer itemId, User user) {
        // Find the used item
        UsedItem usedItem = usedItemRepository.findById(itemId)
                .orElseThrow(() -> new ApiException("Used item not found"));

        // Check if the item already has a buyer (i.e., it's already sold)
        if (usedItem.getBuyer() != null) {
            throw new ApiException("Item is already sold");
        }

        // Check if the seller exists
        if (usedItem.getSeller() == null) {
            throw new ApiException("Seller not assigned for this item");
        }

        // Create a new order
        Orders order = new Orders();
        order.setOrderDate(LocalDate.now());
        order.setTotalPrice(usedItem.getPrice());
        order.setStatus("COMPLETED");
        order.setBuyer(user); // Ensure the authenticated user is the buyer
        order.setSeller(usedItem.getSeller());

        ordersRepository.save(order);

        // Set the buyer and link the order
        usedItem.setBuyer(user); // Set the authenticated user as the buyer
        usedItem.setOrders(order);

        usedItemRepository.save(usedItem);
    }

    // Secure method to get items by seller
    public List<UsedItem> getItemsBySeller(User user) {
        // Ensure that the authenticated user is the seller
        return usedItemRepository.findAllBySeller(user);
    }
}
