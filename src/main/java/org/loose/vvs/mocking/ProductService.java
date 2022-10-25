package org.loose.vvs.mocking;

import java.util.List;

public class ProductService {

    private ProductProvider productProvider;

    public double computeAveragePricesOfPhones() {
        return productProvider.searchForProduct("phone").stream()
                .mapToInt(Product::getPrice)
                .average()
                .orElse(0.);
    }

    public void setIphoneRating() {
        List<Product> products = productProvider.getProducts();

        for (Product product : products) {
            if(product.getBrand().equals("Apple")) {
                product.setRating(5);
            }
        }

    }

    public void setProductProvider(ProductProvider productProvider) {
        this.productProvider = productProvider;
    }
}
