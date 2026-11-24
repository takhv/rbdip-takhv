package com.rbdip.bookstore.review;

/** Review-owned port for checking purchase history. */
public interface PurchaseVerifier {

    boolean wasProductPurchased(Long productId);
}
