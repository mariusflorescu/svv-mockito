package org.loose.vvs.mocking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final List<Product> someProducts = Arrays.asList(
            Product.builder().id(1).price(100).brand("Samsung").rating(2).build(),
            Product.builder().id(2).price(300).brand("Xiaomi").rating(2).build(),
            Product.builder().id(3).price(500).brand("Apple").rating(3).build()
    );

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService();
    }

    @Test
    void testAveragePriceForPhonesWithNoProducts() {
        productService.setProductProvider(new ProductProvider() {
            @Override
            public List<Product> getProducts() {
                return new ArrayList<>();
            }

            @Override
            public List<Product> searchForProduct(String query) {
                return new ArrayList<>();
            }
        });

        assertEquals(0, productService.computeAveragePricesOfPhones());
    }

    @Test
    void testAveragePriceForPhonesWithNoProductsWithMock(@Mock ProductProvider productProvider) {
        productService.setProductProvider(productProvider);

        assertEquals(0, productService.computeAveragePricesOfPhones());
    }

    @Test
    void testAveragePriceForPhones() {
        productService.setProductProvider(new MockProductProvider());

        assertEquals(200, productService.computeAveragePricesOfPhones());
    }

    @Test
    void testAveragePriceWithMockito(@Mock ProductProvider productProvider) {
        when(productProvider.searchForProduct(anyString())).thenReturn(someProducts);
        productService.setProductProvider(productProvider);
        assertEquals(200, productService.computeAveragePricesOfPhones());
        verify(productProvider, atLeastOnce()).searchForProduct("phone");
    }

    @Test
    void testSetIphoneRatingTo5WithMokito(@Mock ProductProvider productProvider) {
        when(productProvider.getProducts()).thenReturn(someProducts);
        productService.setProductProvider(productProvider);
        productService.setIphoneRating();

        //verify that getProducts() was called
        verify(productProvider, times(1)).getProducts();

        //we fetch the list of products
        List<Product> tmpProducts = productProvider.getProducts();
        for (Product tmpProduct : tmpProducts) {
            //if apple it should have 5
            if(tmpProduct.getBrand().equals("Apple")) {
                assertEquals(5, tmpProduct.getRating());
            } else {
                // else not 5
                assertNotEquals(5, tmpProduct.getRating());
            }
        }
    }

    @Test
    void testSetIphoneRatingTo5WithInlineClass() {
        productService.setProductProvider(new ProductProvider() {
            @Override
            public List<Product> getProducts() {
                return someProducts;
            }

            @Override
            public List<Product> searchForProduct(String query) {
                return null;
            }
        });

        productService.setIphoneRating();

        List<Product> appleProducts = someProducts.stream().filter(p -> p.getBrand().equals("Apple")).collect(Collectors.toList());

        for (Product appleProduct : appleProducts) {
            assertEquals(5, appleProduct.getRating());
        }
    }

    @Test
    void testSetIphoneRatingTo5WithInsideClass(){
        productService.setProductProvider(new MockProductProvider());
        productService.setIphoneRating();

        List<Product> appleProducts = someProducts.stream().filter(p -> p.getBrand().equals("Apple")).collect(Collectors.toList());

        for (Product appleProduct : appleProducts) {
            assertEquals(5, appleProduct.getRating());
        }
    }




    private static class MockProductProvider implements ProductProvider {

        @Override
        public List<Product> getProducts() {
            return someProducts;
        }

        @Override
        public List<Product> searchForProduct(String query) {
            return someProducts;
        }
    }
}